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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.huahui.datasphere.portal.dto.RightDTO;
import com.huahui.datasphere.portal.dto.SecuredResourceDTO;
import com.huahui.datasphere.portal.security.po.Resource;
import com.huahui.datasphere.portal.security.po.ResourceRight;
import com.huahui.datasphere.portal.security.po.Right;
import com.huahui.datasphere.portal.type.security.SecuredResourceCategoryInf;
import com.huahui.datasphere.portal.type.security.SecuredResourceTypeInf;

/**
 * todo: JavaDoc
 *
 * @author theseusyang
 * @since 31.05.2019
 */
public class RightConverter {


    /**
     * Create.
     */
    public static final String CREATE_LABEL = "CREATE";
    /**
     * Update.
     */
    public static final String UPDATE_LABEL = "UPDATE";
    /**
     * Delete.
     */
    public static final String DELETE_LABEL = "DELETE";
    /**
     * Read.
     */
    public static final String READ_LABEL = "READ";
    /**
     * Convert rights.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<Right> convertRightsPoToDto(List<ResourceRight> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<Right> target = new ArrayList<>();
        Map<Resource, List<Right>> map = new HashMap<>();
        for (ResourceRight rr : source) {
            if (map.containsKey(rr.getResource())) {
                map.get(rr.getResource()).add((Right) rr.getRight());
            } else {
                List<Right> list = new ArrayList<>();
                list.add((Right) rr.getRight());
                map.put(rr.getResource(), list);
            }
        }

        Set<Resource> pos = map.keySet();
        for (Resource po : pos) {
            RightDTO dto = new RightDTO();
            SecuredResourceDTO ssd = new SecuredResourceDTO();
            ssd.setName(po.getName());
            ssd.setDisplayName(po.getDisplayName());
            ssd.setType(SecuredResourceTypeInf.valueOf(po.getRType()));
            ssd.setCategory(SecuredResourceCategoryInf.valueOf(po.getCategory()));
            dto.setSecuredResource(ssd);
            List<com.huahui.datasphere.portal.security.po.Right> list = map.get(po);
            for (com.huahui.datasphere.portal.security.po.Right right : list) {
                if (CREATE_LABEL.equals(right.getName())) {
                    dto.setCreate(true);
                } else if (READ_LABEL.equals(right.getName())) {
                    dto.setRead(true);
                } else if (DELETE_LABEL.equals(right.getName())) {
                    dto.setDelete(true);
                } else if (UPDATE_LABEL.equals(right.getName())) {
                    dto.setUpdate(true);
                }
            }
            target.add(dto);
        }

        return target;
    }



}
