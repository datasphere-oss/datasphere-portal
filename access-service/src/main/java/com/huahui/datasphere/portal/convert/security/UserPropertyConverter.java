

package com.huahui.datasphere.portal.convert.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.huahui.datasphere.portal.dto.UserPropertyDTO;
import com.huahui.datasphere.portal.security.po.UserProperty;
import com.huahui.datasphere.portal.security.po.UserPropertyValue;

/**
 * @author theseusyang
 */
public class UserPropertyConverter {

    /**
     * Constructor.
     */
    private UserPropertyConverter() {
        super();
    }

    /**
     * Convert property po to dto.
     *
     * @param property the property 
     * @return the user property DTO
     */
    public static UserPropertyDTO convert(UserProperty property) {

        if (property == null) {
            return null;
        }

        UserPropertyDTO dto = new UserPropertyDTO();

        dto.setId(property.getId());
        dto.setRequired(property.isRequired());
        dto.setName(property.getName());
        dto.setDisplayName(property.getDisplayName());

        return dto;
    }

    /**
     * Convert properties po to dto.
     *
     * @param propertys the property P os
     * @return the list
     */
    public static List<UserPropertyDTO> convertPropertys(List<UserProperty> propertys) {
        if (propertys == null) {
            return new ArrayList<>();
        }
        final List<UserPropertyDTO> target = new ArrayList<>();
        propertys.forEach(s -> target.add(convert(s)));
        return target;
    }

    /**
     * Convert property dto to po.
     *
     * @param propertyDTO the property DTO
     * @return the user property 
     */
    public static UserProperty convert(UserPropertyDTO propertyDTO) {
        if (propertyDTO == null) {
            return null;
        }

        UserProperty po = new UserProperty();
        po.setId(propertyDTO.getId());
        po.setRequired(propertyDTO.isRequired());
        po.setName(StringUtils.trim(propertyDTO.getName()));
        po.setDisplayName(StringUtils.trim(propertyDTO.getDisplayName()));

        return po;
    }

    /**
     * Convert property value po to dto.
     *
     * @param value the value 
     * @return the user property DTO
     */
    public static UserPropertyDTO convert(UserPropertyValue value) {

        if (value == null) {
            return null;
        }

        UserPropertyDTO dto = new UserPropertyDTO();
        if (value.getProperty() != null) {
            dto.setId(value.getProperty().getId());
            dto.setName(value.getProperty().getName());
            dto.setDisplayName(value.getProperty().getDisplayName());
        }

        dto.setValue(value.getValue());
        return dto;
    }

    /**
     * Convert property values po to dto.
     *
     * @param values the value P os
     * @return the list
     */
    public static List<UserPropertyDTO> convertValues(List<UserPropertyValue> values) {

        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(UserPropertyConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert property value dto to po.
     *
     * @param valueDto the value dto
     * @return the user property value 
     */
    public static UserPropertyValue convertValueDTO(final UserPropertyDTO valueDto) {

        if (valueDto == null) {
            return null;
        }

        final UserProperty property = new UserProperty();
        property.setName(valueDto.getName());
        property.setDisplayName(valueDto.getDisplayName());
        property.setId(valueDto.getId());

        final UserPropertyValue value = new UserPropertyValue();
        value.setProperty(property);
        value.setValue(valueDto.getValue());

        return value;
    }

    /**
     * Convert property values dto to po.
     *
     * @param valueDtos the value dtos
     * @return the list
     */
    public static List<UserPropertyValue> convertPropertyDTOs(final List<UserPropertyDTO> valueDtos) {

        if (CollectionUtils.isEmpty(valueDtos)) {
            return Collections.emptyList();
        }

        return valueDtos.stream()
                .map(UserPropertyConverter::convertValueDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
