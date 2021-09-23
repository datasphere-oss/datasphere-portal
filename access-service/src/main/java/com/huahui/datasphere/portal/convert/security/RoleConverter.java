/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.huahui.datasphere.portal.convert.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.huahui.datasphere.portal.dto.RoleDTO;
import com.huahui.datasphere.portal.dto.SecurityLabelAttributeDTO;
import com.huahui.datasphere.portal.dto.SecurityLabelDTO;
import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.type.security.RoleTypeInf;
import com.huahui.datasphere.portal.type.security.SecurityLabelAttribute;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;


/**
 * @author mikhail
 * Roles * objects converter.
 */
public final class RoleConverter {


    /**
     * Disable instances.
     */
    private RoleConverter() {
        super();
    }

    /**
     * Convert role po.
     *
     * @param source
     *            the source
     * @return the role dto
     */
    public static RoleDTO convertRole(Role source) {

        if (source == null) {
            return null;
        }

        final RoleDTO target = new RoleDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        if (source.getRType() != null) {
            target.setRoleType(RoleTypeInf.valueOf(source.getRType()));
        }
        target.setRights(RightConverter.convertRightsPoToDto(source.getConnectedResourceRights()));
        final List<SecurityLabelInf> securityLabelInfs = convertSecurityLabels(source.getLabelAttributeValues());
        target.setSecurityLabels(addSecurityLabelsWithoutValues(securityLabelInfs, source.getLabels()));
        target.setProperties(RolePropertyConverter.convertPropertyValues(source.getProperties()));
        return target;
    }


    private static List<SecurityLabelInf> addSecurityLabelsWithoutValues(List<SecurityLabelInf> securityLabelInfs, List<Label> labels) {
        if (CollectionUtils.isEmpty(labels)) {
            return securityLabelInfs;
        }
        final List<SecurityLabelInf> result = new ArrayList<>(securityLabelInfs);
        final Set<String> existLabelNames = securityLabelInfs.stream()
                .map(SecurityLabelInf::getName)
                .collect(Collectors.toSet());
        result.addAll(
                labels.stream()
                        .filter(l -> !existLabelNames.contains(l.getName()))
                        .map(RoleConverter::convertLabel)
                        .collect(Collectors.toList())
        );
        return result;
    }

    /**
     * Convert labels.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelInf> convertLabels(final List<Label> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        final List<SecurityLabelInf> target = new ArrayList<>();
        source.forEach(s -> target.add(convertLabel(s)));
        return target;
    }

    /**
     * Convert label.
     *
     * @param source
     *            the source
     * @return the security label dto
     */
    public static SecurityLabelInf convertLabel(Label source) {

        if (source == null) {
            return null;
        }

        SecurityLabelDTO target = new SecurityLabelDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setAttributes(convertAttributes(source.getLabelAttribute()));
        return target;
    }

    /**
     * Convert attributes.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelAttribute> convertAttributes(List<LabelAttribute> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<SecurityLabelAttribute> target = new ArrayList<>();
        source.forEach(s -> {
                    if (CollectionUtils.isEmpty(s.getLabelAttributeValues())) {
                        target.add(convertAttribute(s));
                    } else {
                        target.addAll(convertAttributeValues(s.getLabelAttributeValues()));
                    }
                }
        );
        return target;
    }

    private static Collection<SecurityLabelAttribute> convertAttributeValues(Collection<LabelAttributeValue> labelAttributeValues) {
        return labelAttributeValues.stream()
                .map(lav ->
                        new SecurityLabelAttributeDTO(
                                lav.getId(),
                                lav.getLabelAttribute().getName(),
                                lav.getLabelAttribute().getPath(),
                                lav.getValue(),
                                lav.getLabelAttribute().getDescription()
                        )
                )
                .collect(Collectors.toList());
    }

    /**
     * Convert attribute.
     *
     * @param source
     *            the source
     * @return the security label attribute dto
     */
    public static SecurityLabelAttributeDTO convertAttribute(LabelAttribute source) {

        if (source == null) {
            return null;
        }

        SecurityLabelAttributeDTO target = new SecurityLabelAttributeDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setValue(null);
        target.setDescription(source.getDescription());
        return target;
    }


    public static List<RoleDTO> convertRoles(Collection<Role> roles) {
        return roles.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }





}
