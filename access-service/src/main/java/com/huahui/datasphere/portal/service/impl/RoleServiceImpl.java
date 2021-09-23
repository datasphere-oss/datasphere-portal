/*
 * Apache License
 * 
 * Copyright (c) 2021 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huahui.datasphere.portal.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.huahui.datasphere.portal.configuration.CoreMessagingDomain;
import com.huahui.datasphere.portal.convert.security.RoleConverter;
import com.huahui.datasphere.portal.convert.security.RolePropertyConverter;
import com.huahui.datasphere.portal.dao.RoleDao;
import com.huahui.datasphere.portal.dto.RoleDTO;
import com.huahui.datasphere.portal.dto.RolePropertyDTO;
import com.huahui.datasphere.portal.dto.SecuredResourceDTO;
import com.huahui.datasphere.portal.exception.CoreExceptionIds;
import com.huahui.datasphere.portal.security.RoleDef;
import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.Resource;
import com.huahui.datasphere.portal.security.po.ResourceRight;
import com.huahui.datasphere.portal.security.po.Right;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RolePropertyValue;
import com.huahui.datasphere.portal.service.RoleService;
import com.huahui.datasphere.portal.service.SecurityService;
import com.huahui.datasphere.portal.type.messaging.CoreHeaders;
import com.huahui.datasphere.portal.type.messaging.CoreTypes;
import com.huahui.datasphere.portal.type.security.CustomProperty;
import com.huahui.datasphere.portal.type.security.RightInf;
import com.huahui.datasphere.portal.type.security.RoleInf;
import com.huahui.datasphere.portal.type.security.SecuredResourceCategoryInf;
import com.huahui.datasphere.portal.type.security.SecuredResourceTypeInf;
import com.huahui.datasphere.portal.type.security.SecurityLabelAttribute;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;
import com.huahui.datasphere.portal.util.SecurityUtils;
import com.huahui.datasphere.system.exception.PlatformBusinessException;
import com.huahui.datasphere.system.exception.PlatformValidationException;
import com.huahui.datasphere.system.exception.ValidationResult;
import com.huahui.datasphere.system.type.annotation.DomainRef;
import com.huahui.datasphere.system.type.messaging.DomainInstance;
import com.huahui.datasphere.system.type.messaging.Message;
import com.huahui.datasphere.system.type.runtime.MeasurementPoint;

/**
 * The Class RoleService.
 *
 * @author theseusyang
 */
@Component
public class RoleServiceImpl implements RoleService {
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * Parameter name length limit.
     */
    private static final int PARAM_NAME_LIMIT = 2044;

    /**
     * Parameter display name length limit.
     */
    private static final int PARAM_DISPLAY_NAME_LIMIT = 2044;

    /**
     * Validation tags.
     */
    private static final String VIOLATION_REQUIRED_PROPERTY_VALUE = "app.role.property.validationError.value.not.set";
    private static final String VIOLATION_NAME_PROPERTY_EMPTY = "app.role.property.validationError.name.property.empty";
    private static final String VIOLATION_NAME_PROPERTY_LENGTH = "app.role.property.validationError.name.property.length";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY = "app.role.property.validationError.displayName.property.empty";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH = "app.role.property.validationError.displayName.property.length";
    private static final String VIOLATION_NAME_PROPERTY_NOT_UNIQUE = "app.role.property.validationError.name.not.unique";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE = "app.role.property.validationError.displayName.not.unique";
    private static final String VIOLATION_ROLE_NAME_EMPTY = "app.role.data.validationError.roleName.empty";
    private static final String VIOLATION_ROLE_NAME_LENGTH = "app.role.data.validationError.roleName.length";
    private static final String VIOLATION_DISPLAY_NAME_LENGTH = "app.role.data.validationError.displayName.length";
    private static final String VIOLATION_ROLE_TYPE_EMPTY = "app.role.data.validationError.roleType.empty";
    private static final String VIOLATION_ROLE_TYPE_LENGTH = "app.role.data.validationError.roleType.length";

    /**
     * The role dao impl.
     */
    @Autowired
    private RoleDao roleDAO;
    
    /**
     * Security service.
     */
    @Autowired
    private SecurityService securityService;

    @DomainRef(CoreMessagingDomain.NAME)
    private DomainInstance coreMessagingDomain;
    
    /**
     * User Role Validation
     */
    @Autowired
    private RoleValidationService roleValidationService;

    /**
     * The create.
     */
    private RightInf CREATE;

    /**
     * The update.
     */
    private RightInf UPDATE;

    /**
     * The delete.
     */
    private RightInf DELETE;

    /**
     * The read.
     */
    private RightInf READ;

   
    @Override
    @Transactional
    public void create(RoleInf role) {
        try {
            roleValidationService.onCreate(role);

            Role toUpdate = convertRoleDTO(role);
            toUpdate.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            toUpdate.setCreatedBy(SecurityUtils.getCurrentUserName());
            toUpdate = roleDAO.create(toUpdate);
            final List<RightInf> rightDTOs = role.getRights();
            final List<ResourceRight> resourcesToCreate = new ArrayList<>();
            for (final RightInf rightDTO : rightDTOs) {
                if (rightDTO.getSecuredResource() == null || rightDTO.getSecuredResource().getName() == null) {
                    break;
                }

                final Resource resourcePO = roleDAO.findResourceByName(rightDTO.getSecuredResource().getName());
                resourcesToCreate.addAll(connectRolesWithRightsAndResources(toUpdate, resourcePO, rightDTO));
            }

            toUpdate.setConnectedResourceRights(resourcesToCreate);
            roleDAO.update(toUpdate.getName(), toUpdate, role.getSecurityLabels());

            savePropertyValues(toUpdate.getId(), role.getProperties());

            coreMessagingDomain.send(new Message(CoreTypes.ROLE_CREATE)
                    .withHeader(CoreHeaders.ROLE_NAME, role.getName()));

        } catch (Exception e) {
            coreMessagingDomain.send(new Message(CoreTypes.ROLE_CREATE)
                    .withHeader(CoreHeaders.ROLE_NAME, Objects.isNull(role) ? "UNKNOWN" : role.getName())
                    .withCause(e));
            throw e;
        }
    }

  
    @Override
    @Transactional
    public void delete(String roleName) {
        try {
            roleDAO.delete(roleName);
            securityService.logoutByRoleName(roleName);

            coreMessagingDomain.send(new Message(CoreTypes.ROLE_DELETE)
                    .withHeader(CoreHeaders.ROLE_NAME, roleName));

        } catch (Exception e) {

            coreMessagingDomain.send(new Message(CoreTypes.ROLE_DELETE)
                    .withHeader(CoreHeaders.ROLE_NAME, roleName)
                    .withCause(e));

            throw e;
        }

    }

   
    @Override
    @Transactional
    public void update(final String roleName, final RoleInf role) {
        try {
            roleValidationService.onUpdate(role);
            final Role toUpdate = roleDAO.findByName(roleName);
            final List<ResourceRight> resourcesToConnect = new ArrayList<>();
            final List<ResourceRight> resourcesToDisconnect = new ArrayList<>();
            for (final RightInf right : role.getRights()) {
                final Resource resource = roleDAO.findResourceByName(right.getSecuredResource().getName());
                resourcesToConnect.addAll(connectRolesWithRightsAndResources(toUpdate, resource, right));
                resourcesToDisconnect.addAll(disconnectRolesWithRightsAndResources(toUpdate, resource, right));
            }

            toUpdate.setDisplayName(role.getDisplayName());
            toUpdate.setName(role.getName());
            toUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            toUpdate.setUpdatedBy(SecurityUtils.getCurrentUserName());
            toUpdate.setConnectedResourceRights(resourcesToConnect);
            toUpdate.setDisconnectedResourceRights(resourcesToDisconnect);

            roleDAO.update(roleName, toUpdate, role.getSecurityLabels());
            if (role.getSecurityLabels() != null) {
                role.getSecurityLabels().forEach(sl ->
                        coreMessagingDomain.send(new Message(CoreTypes.ROLE_LABEL_ATTACH)
                                .withHeader(CoreHeaders.ROLE_NAME, roleName)
                                .withHeader(CoreHeaders.LABEL_NAME, sl.getName()))
                );
            }

            savePropertyValues(toUpdate.getId(), role.getProperties());
            securityService.logoutByRoleName(roleName);

            coreMessagingDomain.send(new Message(CoreTypes.ROLE_UPDATE)
                    .withHeader(CoreHeaders.ROLE_NAME, role.getName()));

        } catch (Exception e) {

            coreMessagingDomain.send(new Message(CoreTypes.ROLE_UPDATE)
                    .withHeader(CoreHeaders.ROLE_NAME, role.getName())
                    .withCause(e));

            throw e;
        }
    }

    
    @Override
    @Transactional
    public void unlink(String roleName, String resourceName) {

        final Role toUpdate = roleDAO.findByName(roleName);
        final Resource resource = roleDAO.findResourceByName(resourceName);
        final List<ResourceRight> resourcesToDisconnect
                = disconnectRolesWithRightsAndResources(toUpdate, resource, SecurityUtils.ALL_DISABLED);

        toUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        toUpdate.setUpdatedBy(SecurityUtils.getCurrentUserName());
        toUpdate.setDisconnectedResourceRights(resourcesToDisconnect);

        roleDAO.update(roleName, toUpdate, Collections.emptyList());
        securityService.logoutByRoleName(roleName);
    }

  
    @Override
    @Transactional
    public Role getRoleByName(String roleName) {
        MeasurementPoint.start();
        try {
            Role rolePO = roleDAO.findByName(roleName);
            return RoleConverter.convertRole(rolePO);
        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Convert role dto.
     *
     * @param source the source
     * @return the role po
     */
    private Role convertRoleDTO(RoleInf source) {
        if (source == null) {
            return null;
        }
        Role target = new Role();
        target.setName(source.getName().trim());
        target.setDisplayName(source.getDisplayName().trim());
        target.setRType(source.getRoleType().name());
        return target;
    }

    /**
     * Disconnects rights from resources.
     *
     * @param role the role
     * @param resource the resource
     * @param right the right
     * @return list
     */
    private List<ResourceRight> disconnectRolesWithRightsAndResources(Role role, Resource resource, RightInf right) {

        List<ResourceRight> matchByResourceName = role.getConnectedResourceRights().stream()
                .filter(po -> po.getResource().getName().equals(right.getSecuredResource().getName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(matchByResourceName)) {
            return Collections.emptyList();
        }

        List<ResourceRight> result = new ArrayList<>();
        if (!right.isCreate() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(CREATE))) {
            result.add(createResourceRight(role, resource, CREATE));
        }

        if (!right.isUpdate() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(UPDATE))) {
            result.add(createResourceRight(role, resource, UPDATE));
        }

        if (!right.isDelete() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(DELETE))) {
            result.add(createResourceRight(role, resource, DELETE));
        }

        if (!right.isRead() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(READ))) {
            result.add(createResourceRight(role, resource, READ));
        }

        return result;
    }

    /**
     * Connect roles with rights and resources.
     *
     * @param role the to update
     * @param right the right dto
     * @param resource the resource po
     */
    private List<ResourceRight> connectRolesWithRightsAndResources(Role role, Resource resource, RightInf right) {

        List<ResourceRight> result = new ArrayList<>();
        if (right.isCreate()) {
            result.add(createResourceRight(role, resource, CREATE));
        }
        if (right.isDelete()) {
            result.add(createResourceRight(role, resource, DELETE));
        }
        if (right.isUpdate()) {
            result.add(createResourceRight(role, resource, UPDATE));
        }
        if (right.isRead()) {
            result.add(createResourceRight(role, resource, READ));
        }

        for (int i = 0; i < result.size(); i++) {

            final ResourceRight resourceRightPO = result.get(i);
            int existingIdx = CollectionUtils.isEmpty(role.getConnectedResourceRights())
                    ? -1
                    : role.getConnectedResourceRights().indexOf(resourceRightPO);

            if (existingIdx != -1) {
                result.set(i, role.getConnectedResourceRights().get(existingIdx));
            }
        }

        return result;
    }

    /**
     * Creates the resource right.
     *
     * @param toUpdate the to update
     * @param resourcePO the resource po
     * @param right the right
     * @return new resource right
     */
    private ResourceRight createResourceRight(Role toUpdate, Resource resourcePO, Right right) {

        ResourceRight resourceRightPO = new ResourceRight();
        resourceRightPO.setResource(resourcePO);
        resourceRightPO.setRight(right);
        resourceRightPO.setRole(toUpdate);
        resourceRightPO.setCreatedBy(SecurityUtils.getCurrentUserName());
        resourceRightPO.setUpdatedBy(SecurityUtils.getCurrentUserName());

        return resourceRightPO;
    }

   
    @Override
    @Transactional
    public List<RoleInf> getAllRoles() {
        final List<Role> rolePOs = roleDAO.getAll();
        return rolePOs.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }

   
    @Override
    public List<Role> getAllRolesByUserLogin(String login) {
        final List<Role> rolePOs = roleDAO.findRolesByUserLogin(login);
        return rolePOs.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }

   
    @Override
    @Transactional
    public List<SecuredResourceDTO> getAllSecuredResources() {
        List<Resource> resourcePOs = roleDAO.getAllSecurityResources();

        LinkedHashMap<Integer, SecuredResourceDTO> links = resourcePOs.stream()
                .collect(Collectors.toMap(Resource::getId, RoleServiceImpl::toDto, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        resourcePOs.forEach(po -> {
            if (Objects.nonNull(po.getParentId())) {
                SecuredResourceDTO dto = links.get(po.getId());
                SecuredResourceDTO parent = links.get(po.getParentId());
                parent.getChildren().add(dto);
                dto.setParent(parent);
            }
        });
        return links.values().stream().filter(v -> v.getParent() == null).collect(Collectors.toList());
    }

    private static SecuredResourceDTO toDto(Resource po) {
        SecuredResourceDTO dto = new SecuredResourceDTO();
        dto.setName(po.getName());
        dto.setDisplayName(po.getDisplayName());
        dto.setType(po.getRType() == null
                ? SecuredResourceTypeInf.SYSTEM
                : SecuredResourceTypeInf.valueOf(po.getRType()));
        dto.setCategory(po.getCategory() == null
                ? SecuredResourceCategoryInf.SYSTEM
                : SecuredResourceCategoryInf.valueOf(po.getCategory()));
        return dto;
    }

    @Override
    public List<SecuredResourceDTO> getSecuredResourcesFlatList() {
        return roleDAO.getAllSecurityResources().stream()
                .map(r -> {
                    final SecuredResourceDTO securedResourceDTO = new SecuredResourceDTO();
                    securedResourceDTO.setName(r.getName());
                    securedResourceDTO.setDisplayName(r.getDisplayName());
                    securedResourceDTO.setType(r.getRType() == null
                            ? SecuredResourceTypeInf.SYSTEM
                            : SecuredResourceTypeInf.valueOf(r.getRType()));
                    securedResourceDTO.setCategory(r.getCategory() == null
                            ? SecuredResourceCategoryInf.SYSTEM
                            : SecuredResourceCategoryInf.valueOf(r.getCategory()));
                    return securedResourceDTO;
                })
                .collect(Collectors.toList());
    }

   
    @Override
    @Transactional
    public List<SecurityLabelInf> getAllSecurityLabels() {
        return RoleConverter.convertLabels(roleDAO.getAllSecurityLabels());
    }

   
    @Override
    @Transactional
    public void createLabel(SecurityLabelInf label) {
        try {
            upsertSecurityLabesValidation(label);
            boolean isExists = roleDAO.getAllSecurityLabels().stream().anyMatch(l -> l.getName().equals(label.getName()));
            if (isExists) {
                throw new PlatformBusinessException(
                        "security label with name " + label.getName() + " cannot be found",
                        CoreExceptionIds.EX_ERROR_SECURITY_LABEL_NAME_NOT_UNIQUE,
                        label.getName()
                );
            }
            Label labelNew = convertLabelDTOToPO(label);
            roleDAO.createSecurityLabel(labelNew);

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_CREATE)
                    .withHeader(CoreHeaders.LABEL_NAME, label.getName()));

        } catch (Exception e) {

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_CREATE)
                    .withHeader(CoreHeaders.LABEL_NAME, label.getName())
                    .withCause(e));

            throw e;
        }
    }

    /**
     * Convert label dto to po.
     *
     * @param source the source
     * @return the label po
     */
    private Label convertLabelDTOToPO(SecurityLabelInf source) {
        if (source == null) {
            return null;
        }
        Label target = new Label();
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setLabelAttribute(convertAttributesDTOToPO(source.getAttributes(), target));
        return target;
    }

    /**
     * Convert attributes dto to po.
     *
     * @param source the source
     * @param label the label
     * @return the list
     */
    private List<LabelAttribute> convertAttributesDTOToPO(final List<SecurityLabelAttribute> source,
                                                            final Label label) {
        if (source == null) {
            return Collections.emptyList();
        }

        final List<LabelAttribute> target = new ArrayList<>();
        source.forEach(s -> target.add(convertAttributeDTOToPO(s, label)));
        return target;
    }

    /**
     * Convert attribute dto to po.
     *
     * @param source the source
     * @param label the label
     * @return the label attribute po
     */
    private LabelAttribute convertAttributeDTOToPO(SecurityLabelAttribute source, Label label) {
        if (source == null) {
            return null;
        }
        final LabelAttribute target = new LabelAttribute();
        target.setId(source.getId());
        target.setLabel(label);
        target.setName(source.getName());
        target.setValue(source.getValue());
        target.setPath(source.getPath());
        target.setDescription(source.getDescription());
        return target;
    }

    
    @Override
    @Transactional
    public void updateLabel(SecurityLabelInf label, String labelName) {
        try {
            upsertSecurityLabesValidation(label);

            boolean isExists = roleDAO.getAllSecurityLabels().stream().anyMatch(l -> l.getName().equals(label.getName()));
            if (!isExists) {
                throw new PlatformBusinessException(
                        "Security label with name " + label.getName() + " cannot be found",
                        CoreExceptionIds.EX_ERROR_SECURITY_LABEL_NAME_NOT_FOUND,
                        label.getName()
                );
            }
            roleDAO.updateSecurityLabelByName(labelName, convertLabelDTOToPO(label));

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_UPDATE)
                    .withHeader(CoreHeaders.LABEL_NAME, label.getName()));

        } catch (Exception e) {

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_UPDATE)
                    .withHeader(CoreHeaders.LABEL_NAME, label.getName())
                    .withCause(e));

            throw e;
        }
    }

   
    @Override
    @Transactional
    public SecurityLabelInf findLabel(String labelName) {
        return RoleConverter.convertLabel(roleDAO.findSecurityLabelByName(labelName));
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#deleteLabel(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteLabel(String labelName) {
        try {
            roleDAO.deleteSecurityLabelByName(labelName);

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_DELETE)
                    .withHeader(CoreHeaders.LABEL_NAME, labelName));

        } catch (Exception e) {

            coreMessagingDomain.send(new Message(CoreTypes.LABEL_DELETE)
                    .withHeader(CoreHeaders.LABEL_NAME, labelName)
                    .withCause(e));

            throw e;
        }
    }

   
    @Override
    @Transactional
    public boolean isUserInRole(String userName, String roleName) {
        // TODO Add external user support
        return roleDAO.isUserInRole(userName, roleName);
    }

   
    @Override
    @Transactional
    public void createResources(List<SecuredResourceDTO> resources) {

        List<Resource> pos = new ArrayList<>();
        for (SecuredResourceDTO dto : resources) {
            Resource po = new Resource();
            po.setCreatedAt(new Timestamp(dto.getCreatedAt().getTime()));
            po.setUpdatedAt(new Timestamp(dto.getUpdatedAt().getTime()));
            po.setCreatedBy(dto.getCreatedBy());
            po.setUpdatedBy(dto.getUpdatedBy());
            po.setDisplayName(dto.getDisplayName());
            po.setName(dto.getName());
            po.setRType(dto.getType().name());
            po.setCategory(dto.getCategory().name());

            pos.add(po);

            if (!CollectionUtils.isEmpty(dto.getChildren())) {
                pos.addAll(createResourcesRecursive(dto.getChildren()));
            }
        }

        roleDAO.createResources(pos);
    }

    /**
     * Creates resources recursivly.
     *
     * @param resources DTOs to process
     * @return list of PO objects
     */
    private Collection<Resource> createResourcesRecursive(Collection<SecuredResourceDTO> resources) {

        List<Resource> result = new ArrayList<>();
        for (SecuredResourceDTO dto : resources) {

            Resource po = new Resource();
            po.setCreatedAt(new Timestamp(dto.getCreatedAt().getTime()));
            po.setUpdatedAt(new Timestamp(dto.getUpdatedAt().getTime()));
            po.setCreatedBy(dto.getCreatedBy());
            po.setUpdatedBy(dto.getUpdatedBy());
            po.setDisplayName(dto.getDisplayName());
            po.setName(dto.getName());
            po.setRType(dto.getType().name());
            po.setCategory(dto.getCategory().name());

            if (dto.getParent() != null) {
                po.setParentName(dto.getParent().getName());
            }

            result.add(po);

            if (!CollectionUtils.isEmpty(dto.getChildren())) {
                result.addAll(createResourcesRecursive(dto.getChildren()));
            }
        }

        return result;
    }

  
    @Override
    public void deleteResource(String resourceName) {
        roleDAO.deleteResource(resourceName);
    }

  
    @Override
    public void deleteResources(List<String> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            return;
        }

        for (String resource : resources) {
            deleteResource(resource);
        }
    }

    @Override
    public boolean updateResourceDisplayName(String resourceName, String resourceDisplayName) {
        return roleDAO.updateResourceDisplayName(resourceName, resourceDisplayName);
    }

   
    @Override
    public void dropResources(SecuredResourceCategoryInf... categories) {
        roleDAO.dropResources(categories);

    }

   
    @Override
    public void init() {
        this.CREATE = roleDAO.findRightByName("CREATE");
        this.UPDATE = roleDAO.findRightByName("UPDATE");
        this.DELETE = roleDAO.findRightByName("DELETE");
        this.READ = roleDAO.findRightByName("READ");
    }

   
    @Override
    @Transactional
    public List<RolePropertyDTO> loadAllProperties() {
        return RolePropertyConverter.convertPropertiesPoToDto(roleDAO.loadAllProperties());
    }

   
    @Override
    @Transactional
    public void saveProperty(final RolePropertyDTO property) {

        roleValidationService.validateRoleProperty(property);

        validateRoleProperty(property);
        final RoleProperty po = RolePropertyConverter.convertPropertyDtoToPo(property);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String userName = SecurityUtils.getCurrentUserName();

        po.setCreatedAt(now);
        po.setCreatedBy(userName);
        po.setUpdatedAt(now);
        po.setUpdatedBy(userName);

        roleDAO.saveProperty(po);

        property.setId(po.getId());
    }

   
    @Override
    @Transactional
    public void deleteProperty(long id) {
        roleDAO.deleteProperty(id);

        LOGGER.debug("Delete role property [id={}]", id);
    }

    
    @Override
    public List<RolePropertyDTO> loadPropertyValues(int roleId) {

        List<RolePropertyValue> valuePOs = roleDAO
                .loadRolePropertyValuesByRoleIds(Collections.singleton(roleId))
                .get(roleId);

        if (CollectionUtils.isEmpty(valuePOs)) {
            return Collections.emptyList();
        }

        return RolePropertyConverter.convertPropertyValues(valuePOs)
                .stream()
                .map(rp -> (RolePropertyDTO) rp)
                .collect(Collectors.toList());
    }

    
    @Override
    @Transactional
    public void savePropertyValues(long roleId, List<CustomProperty> roleProperties) {
        if (!CollectionUtils.isEmpty(roleProperties)) {

            List<RolePropertyValue> storedRoleProperties = roleDAO.loadRolePropertyValuesByRoleId((int) roleId);

            Map<String, RolePropertyValue> storedRolePropertiesMap = storedRoleProperties == null ? new HashMap<>() : storedRoleProperties.stream()
                    .collect(Collectors.toMap(r -> r.getProperty().getName(), r -> r));

            final List<RolePropertyValue> valuePOs = RolePropertyConverter.convertPropertyValuesDtoToPo(roleProperties);
            valuePOs.forEach(valuePO -> {
                valuePO.setRoleId(roleId);

                RolePropertyValue oldRolePropertyValuePO = storedRolePropertiesMap.get(valuePO.getProperty().getName());

                valuePO.setId(oldRolePropertyValuePO != null ? oldRolePropertyValuePO.getId() : null);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                String userName = SecurityUtils.getCurrentUserName();

                valuePO.setCreatedAt(now);
                valuePO.setCreatedBy(userName);
                valuePO.setUpdatedAt(now);
                valuePO.setUpdatedBy(userName);
            });

            roleDAO.saveRolePropertyValues(valuePOs);
        }
    }

    private void validateRolePropertyValues(List<CustomProperty> toCheck) {
        if (CollectionUtils.isEmpty(toCheck)) {
            return;
        }
        final List<ValidationResult> validationResult = new ArrayList<>();
        List<RolePropertyDTO> allProperties = loadAllProperties();
        if (!CollectionUtils.isEmpty(allProperties)) {
            Map<String, RolePropertyDTO> propertiesMap = allProperties.stream()
                    .collect(Collectors.toMap(RolePropertyDTO::getName, r -> r));
            for (CustomProperty property : toCheck) {
                if (propertiesMap.get(property.getName()).isRequired() && StringUtils.isEmpty(property.getValue())) {
                    validationResult.add(new ValidationResult("Required value for role property {0} not set.",
                            VIOLATION_REQUIRED_PROPERTY_VALUE, property.getName()));
                }
            }
        }
        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new PlatformValidationException("Role property values validation error.",
                    CoreExceptionIds.EX_ROLE_PROPERTY_VALUES_VALIDATION_ERROR, validationResult);
        }

    }

    private void validateRoleProperty(final RolePropertyDTO property) {

        final List<ValidationResult> validationResult = new ArrayList<>();

        property.setName(StringUtils.trim(property.getName()));
        property.setDisplayName(StringUtils.trim(property.getDisplayName()));

        if (StringUtils.isEmpty(property.getName())) {
            validationResult.add(new ValidationResult("Property 'name' is blank/empty. Rejected.",
                    VIOLATION_NAME_PROPERTY_EMPTY));
        } else if (property.getName().length() > PARAM_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'name' parameter is larger than {0} limit.",
                    VIOLATION_NAME_PROPERTY_LENGTH, PARAM_NAME_LIMIT));
        }

        if (StringUtils.isEmpty(property.getDisplayName())) {
            validationResult.add(new ValidationResult("Property 'displayName' is blank/empty. Rejected.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY));
        } else if (property.getDisplayName().length() > PARAM_DISPLAY_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'displayName' parameter is larger than {0} limit.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH, PARAM_DISPLAY_NAME_LIMIT));
        }

        if (validationResult.isEmpty()) {

            RoleProperty existProperty = roleDAO.loadPropertyByName(property.getName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("Role property 'name' must be unique. Found existing property with name {0}.",
                        VIOLATION_NAME_PROPERTY_NOT_UNIQUE, property.getName()));
            }

            existProperty = roleDAO.loadPropertyByDisplayName(property.getDisplayName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("Role property 'displayName' must be unique. Found existing property with displayName {0}.",
                        VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE, property.getDisplayName()));
            }
        }

        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new PlatformValidationException("Role properties validation error.",
                    CoreExceptionIds.EX_ROLE_PROPERTY_VALIDATION_ERROR, validationResult);
        }
    }

    @Override
    public List<RoleDTO> loadAllRoles() {
        return RoleConverter.convertRoles(roleDAO.fetchRolesFullInfo());
    }

    @Override
    public void removeRolesByName(final List<String> roles) {
        roleDAO.removeRolesByName(roles);
    }

    @Override
    public void cleanRolesDataByName(List<String> roles) {
        roleDAO.cleanRolesDataByName(roles);
    }

    @Override
    public List<RoleInf> loadRolesData(List<String> rolesName) {
        final List<RoleDTO> roles = RoleConverter.convertRoles(roleDAO.loadRoles(rolesName));
        return roles.stream().map(r -> (RoleInf) r).collect(Collectors.toList());
    }


    private void upsertSecurityLabesValidation(SecurityLabelInf label) {
        if (StringUtils.isBlank(label.getName())) {
            throw new PlatformBusinessException(
                    "Security label name is null",
                    CoreExceptionIds.EX_ERROR_SECURITY_LABEL_NAME_IS_NULL
            );
        }
        if (StringUtils.isBlank(label.getDisplayName())) {
            throw new PlatformBusinessException(
                    "Security label display name is null",
                    CoreExceptionIds.EX_ERROR_SECURITY_LABEL_DISPLAY_NAME_IS_NULL
            );
        }
        if (CollectionUtils.isEmpty(label.getAttributes())) {
            throw new PlatformBusinessException(
                    "Security label attributes are null or empty",
                    CoreExceptionIds.EX_ERROR_SECURITY_LABEL_ATTRIBUTES_IS_EMPTY
            );
        }
    }
}
