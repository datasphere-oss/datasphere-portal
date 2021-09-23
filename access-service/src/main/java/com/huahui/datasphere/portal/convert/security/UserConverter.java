

package com.huahui.datasphere.portal.convert.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.huahui.datasphere.portal.dto.PasswordDTO;
import com.huahui.datasphere.portal.dto.UserDTO;
import com.huahui.datasphere.portal.dto.UserEndpointDTO;
import com.huahui.datasphere.portal.dto.UserWithPasswordDTO;
import com.huahui.datasphere.portal.security.po.Api;
import com.huahui.datasphere.portal.security.po.Password;
import com.huahui.datasphere.portal.security.po.Role;
import com.huahui.datasphere.portal.security.po.User;
import com.huahui.datasphere.portal.type.security.EndpointInf;
import com.huahui.datasphere.portal.type.security.RoleInf;
import com.huahui.datasphere.portal.type.security.UserInf;
import com.huahui.datasphere.portal.util.SecurityUtils;


/**
 * The Class UserConverter.
 *
 * @author theseusyang
 */
public final class UserConverter {

    /**
     * No instances.
     */
    private UserConverter() {
        super();
    }

    /**
     * Convert po.
     *
     * @param source
     *            the source
     * @return the user with password dto
     */
    public static UserWithPasswordDTO convert(User source) {

        if (source == null) {
            return null;
        }

        final UserWithPasswordDTO target = new UserWithPasswordDTO();
        target.setActive(source.isActive());
        target.setAdmin(source.isAdmin());
        if (source.getCreatedAt() != null) {
            target.setCreatedAt(source.getCreatedAt());
        }
        if (source.getUpdatedAt() != null) {
            target.setUpdatedAt(source.getUpdatedAt());
        }
        target.setEmail(source.getEmail());
        if (source.getLocale() != null) {
            target.setLocale(new Locale(source.getLocale()));
        }
        target.setFullName(source.getFullName());
        target.setLogin(source.getLogin());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setCreatedBy(source.getCreatedBy());
        target.setSecurityLabels(SecurityUtils.convertSecurityLabels(source.getLabelAttributeValues()));
        target.setRoles(convertRoles(source.getRoles()));

        target.setExternal(source.isExternal());
        target.setSecurityDataSource(source.getSource());
        target.setEnpoints(convertAPIs(source.getApis()));
        target.setEmailNotification(source.isEmailNotification());

        if (source.getPassword() != null) {
            final Optional<Password> currentPassword = source.getPassword().stream().filter(Password::getActive)
                    .findFirst();
            if (currentPassword.isPresent()) {
                final Password pwd = currentPassword.get();
                target.setPassword(pwd.getPasswordText());
                if (pwd.getUpdatedAt() != null) {
                    target.setPasswordLastChangedAt(pwd.getUpdatedAt());
                    target.setPasswordUpdatedBy(pwd.getCreatedBy());
                } else {
                    target.setPasswordLastChangedAt(pwd.getCreatedAt());
                    target.setPasswordUpdatedBy(pwd.getCreatedBy());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(source.getProperties())) {
            target.setProperties(UserPropertyConverter.convertValues(source.getProperties()));
        }
        return target;
    }

    /**
     * Convert AP is.
     *
     * @param source the source
     * @return the list
     */
    public static List<EndpointInf> convertAPIs(List<Api> source) {
        if (source == null) {
            return null;
        }
        List<EndpointInf> target = new ArrayList<>();
        source.stream().forEach(a -> {
            target.add(convertAPI(a));
        });
        return target;
    }

    /**
     * Convert API.
     *
     * @param source the source
     * @return the user APIDTO
     */
    public static EndpointInf convertAPI(Api source) {
        if (source == null) {
            return null;
        }
        UserEndpointDTO target = new UserEndpointDTO();
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        return target;
    }

    /**
     * Convert p os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<UserDTO> convertPOs(List<User> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<UserDTO> target = new ArrayList<>();
        for (User user : source) {
            target.add(convert(user));
        }

        return target;
    }

    /**
     * Convert roles po.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<RoleInf> convertRoles(List<Role> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<RoleInf> target = new ArrayList<>();
        for (Role po : source) {
            target.add(RoleConverter.convertRole(po));
        }

        return target;
    }

    public static List<PasswordDTO> covertPasswords(final List<Password> passwords) {
        return passwords.stream()
                .map(p -> new PasswordDTO(
                        UserConverter.convert(p.getUser()),
                        p.getPasswordText(),
                        p.getActive(),
                        p.getCreatedAt() != null ? p.getCreatedAt().toLocalDateTime() : null
                ))
                .collect(Collectors.toList());
    }
}
