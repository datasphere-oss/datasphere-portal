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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
//import org.unidata.mdm.core.dto.RolePropertyDTO;
//import org.unidata.mdm.core.po.security.RoleProperty;
//import org.unidata.mdm.core.po.security.RolePropertyValue;
//import org.unidata.mdm.core.type.security.CustomProperty;

import com.huahui.datasphere.portal.dto.RolePropertyDTO;
import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RolePropertyValue;
import com.huahui.datasphere.portal.type.security.CustomProperty;

public class RolePropertyConverter {

    private RolePropertyConverter() {
        super();
    }

    private static RolePropertyDTO convertPropertyPoToDto(RoleProperty property) {
        if (property == null) {
            return null;
        }

        RolePropertyDTO dto = new RolePropertyDTO();

        dto.setId(property.getId());
        dto.setRequired(property.isRequired());
        dto.setName(property.getName());
        dto.setDisplayName(property.getDisplayName());
        dto.setReadOnly(property.isReadOnly());
        dto.setFieldType(property.getFieldType());

        return dto;
    }

    public static List<RolePropertyDTO> convertPropertiesPoToDto(List<RoleProperty> propertys) {
        if (propertys == null) {
            return new ArrayList<>();
        }
        final List<RolePropertyDTO> target = new ArrayList<>();
        propertys.forEach(s -> target.add(convertPropertyPoToDto(s)));
        return target;
    }

    public static RoleProperty convertPropertyDtoToPo(RolePropertyDTO propertyDTO) {
        if (propertyDTO == null) {
            return null;
        }

        RoleProperty po = new RoleProperty();
        po.setId(propertyDTO.getId());
        po.setName(StringUtils.trim(propertyDTO.getName()));
        po.setRequired(propertyDTO.isRequired());
        po.setReadOnly(propertyDTO.isReadOnly());
        po.setDisplayName(StringUtils.trim(propertyDTO.getDisplayName()));
        po.setFieldType(StringUtils.trim(propertyDTO.getFieldType()));

        return po;
    }

    public static List<RoleProperty> convertPropertiesDtoToPo(List<RolePropertyDTO> propertyDTOs) {
        if (propertyDTOs == null) {
            return new ArrayList<>();
        }

        final List<RoleProperty> target = new ArrayList<>();
        propertyDTOs.forEach(s -> target.add(convertPropertyDtoToPo(s)));
        return target;
    }


    public static RolePropertyValue convertPropertyValueDtoToPo(CustomProperty valueDto) {

        if (valueDto == null) {
            return null;
        }

        RolePropertyValue value = new RolePropertyValue();

        RoleProperty property = new RoleProperty();
        property.setId(valueDto instanceof RolePropertyDTO ? ((RolePropertyDTO) valueDto).getId() : Long.valueOf(0l));
        property.setName(valueDto.getName());
        property.setDisplayName(valueDto.getDisplayName());
        property.setFieldType(valueDto.getFieldType());

        if (valueDto instanceof RolePropertyDTO) {
            RolePropertyDTO rolePropertyDTO = (RolePropertyDTO) valueDto;

            // override ID
            property.setId(rolePropertyDTO.getId());

            property.setRequired(rolePropertyDTO.isRequired());
            property.setReadOnly(rolePropertyDTO.isReadOnly());
        }


        value.setProperty(property);
        value.setValue(valueDto.getValue());

        return value;
    }

    public static List<RolePropertyValue> convertPropertyValuesDtoToPo(List<CustomProperty> source) {

        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream()
                .map(RolePropertyConverter::convertPropertyValueDtoToPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<CustomProperty> convertPropertyValues(List<RolePropertyValue> values) {

        if (values == null) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(RolePropertyConverter::convertPropertyValue)
                .filter(Objects::nonNull)
                .map(v -> (CustomProperty) v)
                .collect(Collectors.toList());
    }

    /**
     * @param value
     * @return
     */
    public static RolePropertyDTO convertPropertyValue(RolePropertyValue value) {

        if (value == null) {
            return null;
        }

        RolePropertyDTO dto = new RolePropertyDTO();
        if (value.getProperty() != null) {
            dto.setId(value.getProperty().getId());
            dto.setName(value.getProperty().getName());
            dto.setDisplayName(value.getProperty().getDisplayName());
            dto.setRequired(value.getProperty().isRequired());
            dto.setReadOnly(value.getProperty().isReadOnly());
        }

        dto.setValue(value.getValue());
        return dto;
    }


}
