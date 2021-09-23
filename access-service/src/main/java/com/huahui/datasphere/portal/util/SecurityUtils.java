

package com.huahui.datasphere.portal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.huahui.datasphere.portal.dto.ResourceSpecificRightDTO;
import com.huahui.datasphere.portal.dto.RightDTO;
import com.huahui.datasphere.portal.dto.SecurityLabelAttributeDTO;
import com.huahui.datasphere.portal.dto.SecurityLabelDTO;
import com.huahui.datasphere.portal.dto.UserDTO;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;
import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.service.SecurityService;
import com.huahui.datasphere.portal.type.security.RightInf;
import com.huahui.datasphere.portal.type.security.Role;
import com.huahui.datasphere.portal.type.security.SecurityConstants;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;
import com.huahui.datasphere.portal.type.security.SecurityLabelAttribute;
import com.huahui.datasphere.portal.type.security.SecurityToken;


/**
 * The Class SecurityUtils.
 */
public class SecurityUtils {

    /**
     * Default system right.
     */
    public static final RightInf ALL_ENABLED;
    /**
     * Default system right.
     */
    public static final RightInf ALL_DISABLED;
    /**
     * SI.
     */
    static {

        ResourceSpecificRightDTO allEnabled = new ResourceSpecificRightDTO();

        allEnabled.setCreate(true);
        allEnabled.setDelete(true);
        allEnabled.setRead(true);
        allEnabled.setUpdate(true);
        allEnabled.setRestore(true);
        allEnabled.setMerge(true);
        allEnabled.setCreatedAt(new Date());
        allEnabled.setUpdatedAt(new Date());
        allEnabled.setCreatedBy(SecurityConstants.SYSTEM_USER_NAME);
        allEnabled.setUpdatedBy(SecurityConstants.SYSTEM_USER_NAME);

        ALL_ENABLED = allEnabled;

        ResourceSpecificRightDTO allDisabled = new ResourceSpecificRightDTO();

        allDisabled.setCreate(false);
        allDisabled.setDelete(false);
        allDisabled.setRead(false);
        allDisabled.setUpdate(false);
        allDisabled.setRestore(false);
        allDisabled.setMerge(false);
        allDisabled.setCreatedAt(new Date());
        allDisabled.setUpdatedAt(new Date());
        allDisabled.setCreatedBy(SecurityConstants.SYSTEM_USER_NAME);
        allDisabled.setUpdatedBy(SecurityConstants.SYSTEM_USER_NAME);

        ALL_DISABLED = allDisabled;
    }

    /**
     * Admin data management resource name.
     */
    public static final String ADMIN_DATA_MANAGEMENT_RESOURCE_NAME = "ADMIN_DATA_MANAGEMENT";
    public static final String ADMIN_SYSTEM_MANAGEMENT = "ADMIN_SYSTEM_MANAGEMENT";
    public static final String USER_MANAGEMENT = "USER_MANAGEMENT";
    public static final String ROLE_MANAGEMENT = "ROLE_MANAGEMENT";
    public static final String SECURITY_MARKS_MANAGEMENT = "SECURITY_LABELS_MANAGEMENT";
    public static final String DATA_OPERATIONS_MANAGEMENT = "DATA_OPERATIONS_MANAGEMENT";
    public static final String PLATFORM_PARAMETERS_MANAGEMENT = "PLATFORM_PARAMETERS_MANAGEMENT";
    public static final String EXECUTE_DATA_OPERATIONS = "EXECUTE_DATA_OPERATIONS";
    public static final String AUDIT_ACCESS = "AUDIT_ACCESS";
    public static final String DATA_HISTORY_EDITOR = "DATA_HISTORY_EDITOR";

    public static final String ADMIN_CLASSIFIER_MANAGEMENT = "ADMIN_CLASSIFIER_MANAGEMENT";

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);
    /**
     * Security service instance.
     */
    private static SecurityService ssvc;

    /**
     * Default storage name to try in case no storageId was supplied.
     */
    public static final String DEFAULT_STORAGE_NAME = "default";
    /**
     * DataSphere security data source.
     */
    public static final String DATASPHERE_SECURITY_DATA_SOURCE = "DATASPHERE";

    /**
     * Instantiates a new security utils.
     */
    private SecurityUtils() {
        super();
    }

    public static SecurityToken getSecurityTokenForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof SecurityToken) {
            return (SecurityToken) authentication.getDetails();
        }
        return null;
    }

    /**
     * Convenient init method.
     * @param applicationContext
     */
    public static void init() {

        try {
            ssvc = CoreConfiguration.getBean(SecurityService.class);
        } catch (Exception exc) {
            LOGGER.warn("Security service bean GET. Exception caught.", exc);
        }
    }

    /**
     * Gets current user.
     *
     * @return current user LOGIN or "SYSTEM" if the context was not properly
     * initialized
     */
    public static String getCurrentUserName() {
        // Spring context may be null though while being used by tools
        Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        return auth != null ? auth.getName() : SecurityConstants.SYSTEM_USER_NAME;
    }

    /**
     * Gets current token.
     *
     * @return token or null
     */
    public static String getCurrentUserToken() {
        // Spring context may be null though while being used by tools
        Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        return auth != null ? auth.getCredentials().toString() : null;
    }

    /**
     * Gets current token.
     *
     * @return token or null
     */
    public static Locale getCurrentUserLocale() {
        SecurityToken token = getSecurityTokenForCurrentUser();
        return token != null && token.getUser() != null ? token.getUser().getLocale() : null;
    }

    /**
     * Gets current user client IP address.
     * @return ip address string
     */
    public static String getCurrentUserClientIp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.getDetails() instanceof SecurityToken)
                ? ((SecurityToken) authentication.getDetails()).getUserIp()
                : null;
    }

    /**
     * Gets this host IP address.
     * @return ip address string
     */
    public static String getCurrentUserServerIp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.getDetails() instanceof SecurityToken)
                ? ((SecurityToken) authentication.getDetails()).getServerIp()
                : null;
    }

    /**
     * Gets this host IP address.
     * @return ip address string
     */
    public static String getCurrentEndpointName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.getDetails() instanceof SecurityToken)
                ? ((SecurityToken) authentication.getDetails()).getEndpoint().name()
                : null;
    }

    /**
     * Gets the storage ID for the current user.
     *
     * @return storage Id.
     */
    public static String getCurrentUserStorageId() {
        return SecurityUtils.DEFAULT_STORAGE_NAME;
    }

    /**
     * Direct approver (no pending versions) check.
     *
     * @return
     */
    public static boolean isAdminUser() {
        String token = getCurrentUserToken();
        // Tool
        if (token == null || ssvc == null) {
            return true;
        }

        User user = ssvc.getUserByToken(token);
        return user.isAdmin() || user.getRoles().stream().anyMatch(r -> StringUtils.equals(r.getName(),
                SecurityConstants.ROLE_ADMIN));
    }

    /**
     * Check is selected user Admin.
     *
     * @param user
     * @return
     */
    public static boolean isAdminUser(UserDTO user) {
        return user.isAdmin() ||  user.getRoles().stream().anyMatch(r -> StringUtils.equals(r.getName(),
                        SecurityConstants.ROLE_ADMIN));
    }

    /**
     * Gets storage id either from context, or default, if none defined in the context.
     *
     * @param ctx the context
     * @return the id
     */
//    public static String getStorageId(StorageSpecificContext ctx) {
//        return Objects.nonNull(ctx.getStorageId()) ? ctx.getStorageId() : getCurrentUserStorageId();
//    }

    /**
     * Gets security labels for resource.
     *
     * @param name the name of the resource.
     * @return labels or empty list
     */
    public static List<SecurityLabelInf> getSecurityLabelsForResource(String name) {

        String token = getCurrentUserToken();

        // Tool
        if (token == null || ssvc == null) {
            return Collections.emptyList();
        }

        // User
        List<SecurityLabelInf> labels = null;
        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {

            if (tokenObj.getUser().isAdmin()) {
                return Collections.emptyList();
            }

            labels = tokenObj.getLabelsMap().get(name);
        }

        return labels == null ? Collections.emptyList() : labels;
    }


    /**
     * Check read rights for a resource.
     *
     * @param names the names of the resource
     * @return right
     */
    public static boolean isReadRightsForResource(String... names) {
        for (String name : names) {
            if (getRightsForResourceWithDefault(name).isRead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check create rights for a resource.
     *
     * @param names the names of the resource
     * @return right
     */
    public static boolean isCreateRightsForResource(String... names) {
        for (String name : names) {
            if (getRightsForResourceWithDefault(name).isCreate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check delete rights for a resource.
     *
     * @param names the names of the resource
     * @return right
     */
    public static boolean isDeleteRightsForResource(String... names) {
        for (String name : names) {
            if (getRightsForResourceWithDefault(name).isDelete()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check update rights for a resource.
     *
     * @param names the names of the resource
     * @return right
     */
    public static boolean isUpdateRightsForResource(String... names) {
        for (String name : names) {
            if (getRightsForResourceWithDefault(name).isUpdate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets rights for a resource.
     *
     * @param name the name of the resource
     * @return right
     */
    public static RightInf getRightsForResource(String name) {

        String token = getCurrentUserToken();
        // Tool
        if (token == null || ssvc == null) {
            return ALL_ENABLED;
        }

        // User
        RightInf right = null;
        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {

            if (tokenObj.getUser().isAdmin()) {
                return ALL_ENABLED;
            }

            right = tokenObj.getRightsMap().get(name);
        }
        return right;
    }

    /**
     * Filter resources list by rights
     *
     * @param resourceNames list names list for resources
     * @return filtered list
     */
    public static List<String> filterResourcesByRights(Collection<String> resourceNames, Predicate<RightInf> toCheck) {
        List<String> result = new ArrayList<>();
        String token = getCurrentUserToken();
        // Tool
        if (token == null || ssvc == null) {
            return result;
        }

        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {
            if (tokenObj.getUser().isAdmin()) {
                result.addAll(resourceNames);
            } else {
                result = resourceNames
                        .stream()
                        .filter(s -> toCheck.test(ObjectUtils.defaultIfNull(tokenObj.getRightsMap().get(s), ALL_DISABLED)))
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    /**
     * Gets rights for a resource.
     *
     * @param name the name of the resource
     * @return right
     */
    public static RightInf getRightsForResourceWithDefault(String name) {
        RightInf right = getRightsForResource(name);
        return right == null ? ALL_DISABLED : right;
    }

    /**
     * Calculates record state as {@link Right} object.
     *
     * @param name          name of the resource.
     * @param status        current status of the record or relation
     * @param state         current approval state of the record or relation
     * @return {@link Right} object
     */
//    public static ResourceSpecificRightDTO calculateRightsForTopLevelResource(
//            String name, RecordStatus status) {
//
//        // 1. Get rights, defined by roles
//        Right rights = SecurityUtils.getRightsForResourceWithDefault(name);
//
//        // 2. Check status. Disable Edit/Delete operations for already deleted/merged records.
//        if (status == RecordStatus.INACTIVE || status == RecordStatus.MERGED) {
//
//            ResourceSpecificRightDTO result = new ResourceSpecificRightDTO(rights);
//            result.setDelete(false);
//            result.setUpdate(false);
//            result.setMerge(false);
//
//            // 2.1 Check approval state. Prohibit Restore operation for pending records
//
//            result.setRestore(status == RecordStatus.INACTIVE && rights.isCreate() && rights.isUpdate());
//
//            return result;
//        }
//
//        ResourceSpecificRightDTO result = new ResourceSpecificRightDTO(rights);
//        result.setMerge(result.isUpdate() && CollectionUtils.isEmpty(getSecurityLabelsForResource(name)));
//
//        return result;
//    }

    /**
     * Creates the rights.
     *
     * @param list the list
     * @return the list
     */
    public static Map<String, RightInf> createRightsMap(List<Role> list) {

        final Map<String, RightInf> rights = new HashMap<>();
        for (Role role : list) {

            Map<String, RightInf> portion = extractRightsMap(role.getRights());
            for (Entry<String, RightInf> entry : portion.entrySet()) {

                final RightDTO toUpdate = (RightDTO) rights.get(entry.getKey());
                if (Objects.nonNull(toUpdate)) {
                    toUpdate.setCreate(entry.getValue().isCreate() || toUpdate.isCreate());
                    toUpdate.setUpdate(entry.getValue().isUpdate() || toUpdate.isUpdate());
                    toUpdate.setDelete(entry.getValue().isDelete() || toUpdate.isDelete());
                    toUpdate.setRead(entry.getValue().isRead() || toUpdate.isRead());
                } else {
                    rights.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return rights;
    }

    /**
     * Transforms rights list to a map.
     *
     * @param rts list of rights objects
     * @return map
     */
    public static Map<String, RightInf> extractRightsMap(List<RightInf> rts) {

        final Map<String, RightInf> rights = new HashMap<>();
        for (final RightInf right : rts) {
            if (rights.containsKey(right.getSecuredResource().getName())) {
                final RightDTO toUpdate = (RightDTO) rights.get(right.getSecuredResource().getName());
                toUpdate.setSecuredResource(right.getSecuredResource());
                toUpdate.setCreate(right.isCreate() || toUpdate.isCreate());
                toUpdate.setUpdate(right.isUpdate() || toUpdate.isUpdate());
                toUpdate.setDelete(right.isDelete() || toUpdate.isDelete());
                toUpdate.setRead(right.isRead() || toUpdate.isRead());
            } else {
                final RightDTO toCreate = new RightDTO();
                toCreate.setSecuredResource(right.getSecuredResource());
                toCreate.setCreate(right.isCreate());
                toCreate.setUpdate(right.isUpdate());
                toCreate.setDelete(right.isDelete());
                toCreate.setRead(right.isRead());
                rights.put(right.getSecuredResource().getName(), toCreate);
            }
        }
        return rights;
    }

    /**
     * Returns mapped view of the roles list (k -> role name, v -> role itself)
     *
     * @param roles the list
     * @return map
     */
    public static Map<String, Role> createRolesMap(List<Role> roles) {
        return roles.stream().collect(Collectors.toMap(Role::getName, r -> r));
    }

    /**
     * Collects security labels, grouped by resource names.
     *
     * @param roles the roles list
     * @return map
     */
    public static final Map<String, List<SecurityLabelInf>> createLabelsMap(List<Role> roles) {

        Map<String, List<SecurityLabelInf>> labels = new HashMap<>();
        for (Role r : roles) {

            List<SecurityLabelInf> sls = r.getSecurityLabels();
            if (CollectionUtils.isEmpty(sls)) {
                continue;
            }

            Map<String, List<SecurityLabelInf>> portion = extractLabelsMap(sls);
            for (Entry<String, List<SecurityLabelInf>> entry : portion.entrySet()) {
                List<SecurityLabelInf> existing = labels.get(entry.getKey());
                if (Objects.isNull(existing)) {
                    labels.put(entry.getKey(), entry.getValue());
                } else {
                    existing.addAll(entry.getValue());
                }
            }
        }

        return labels;
    }

    /**
     * Extracts labels and returns them a map.
     *
     * @param sls labels list
     * @return map
     */
    public static Map<String, List<SecurityLabelInf>> extractLabelsMap(Collection<SecurityLabelInf> sls) {

        Map<String, List<SecurityLabelInf>> labels = new HashMap<>();
        for (SecurityLabelInf l : sls) {

            if (CollectionUtils.isEmpty(l.getAttributes())) {
                continue;
            }

            final String path = l.getAttributes()
                    .get(0)
                    .getPath();

            if (path == null) {
                continue;
            }

            String entityName = path
                .substring(0,
                    path.indexOf('.'));

            List<SecurityLabelInf> collected = labels.computeIfAbsent(entityName, k -> new ArrayList<>());

            collected.add(l);
        }

        return labels;
    }


    public static Collection<SecurityLabelInf> mergeSecurityLabels(final List<SecurityLabelInf> securityLabels1, final List<SecurityLabelInf> securityLabels2) {
        return Stream.concat(securityLabels1.stream(), securityLabels2.stream())
                .filter(l -> CollectionUtils.isNotEmpty(l.getAttributes()))
                .reduce(new HashMap<>(), SecurityUtils::mergeLabel, SecurityUtils::mergeMaps)
                .values();
    }

    private static Map<Integer, SecurityLabelInf> mergeLabel(Map<Integer, SecurityLabelInf> labels, SecurityLabelInf securityLabelInf) {
        final Integer key = generateKey(securityLabelInf);
        labels.putIfAbsent(key, securityLabelInf);
        return labels;
    }

    private static int generateKey(SecurityLabelInf securityLabelInf) {
        return (securityLabelInf.getName() + ";" + securityLabelInf.getAttributes().stream()
                .sorted(Comparator.comparing(SecurityLabelAttribute::getName))
                .map(SecurityLabelAttribute::getValue)
                .collect(Collectors.joining(";")))
                .hashCode();
    }

    private static Map<Integer, SecurityLabelInf> mergeMaps(
            final Map<Integer, SecurityLabelInf> labels1,
            final Map<Integer, SecurityLabelInf> labels2
    ) {
        labels2.forEach(labels1::putIfAbsent);
        return labels1;
    }


    /**
     * Convert security labels.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelInf> convertSecurityLabels(final List<LabelAttributeValue> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        final List<ImmutablePair<Integer, SecurityLabelInf>> target = new ArrayList<>();
        source.forEach(s -> target.add(convertSecurityLabel(s)));

        final List<ImmutablePair<Integer, SecurityLabelInf>> targetCombined = new ArrayList<>();
        for (ImmutablePair<Integer, SecurityLabelInf> securityLabelInf : target) {
            final Optional<ImmutablePair<Integer, SecurityLabelInf>> sld = targetCombined.stream()
                    .filter(t -> (StringUtils.equals(t.getRight().getName(), securityLabelInf.getRight().getName()))
                            && Objects.equals(t.getLeft(), securityLabelInf.getKey()))
                    .findFirst();
            if (sld.isPresent()) {
                sld.get().getRight().getAttributes().addAll(securityLabelInf.getRight().getAttributes());
            } else {
                targetCombined.add(securityLabelInf);
            }
        }

        return targetCombined.stream().map(ImmutablePair::getRight)
                .collect(Collectors.toList());
    }

    /**
     * Convert security label.
     *
     * @param source
     *            the source
     * @return the immutable pair
     */
    private static ImmutablePair<Integer, SecurityLabelInf> convertSecurityLabel(final LabelAttributeValue source) {
        if (source == null) {
            return null;
        }
        final SecurityLabelDTO target = new SecurityLabelDTO();
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setName(source.getLabelAttribute().getLabel().getName());
        target.setDisplayName(source.getLabelAttribute().getLabel().getDisplayName());
        final SecurityLabelAttributeDTO attributeDTO = new SecurityLabelAttributeDTO();
        attributeDTO.setId(source.getLabelAttribute().getId());
        attributeDTO.setName(source.getLabelAttribute().getName());
        attributeDTO.setValue(source.getValue());
        attributeDTO.setPath(source.getLabelAttribute().getPath());
        final List<SecurityLabelAttribute> attributeDTOs = new ArrayList<>();
        attributeDTOs.add(attributeDTO);
        target.setAttributes(attributeDTOs);
        return new ImmutablePair<>(source.getGroup(), target);
    }
}
