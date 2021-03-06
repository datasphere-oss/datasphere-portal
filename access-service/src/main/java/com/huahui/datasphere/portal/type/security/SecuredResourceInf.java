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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.huahui.datasphere.portal.type.security;

/**
 * @author Denis Kostovarov
 */
public interface SecuredResourceInf {
    /**
     * The name of the resource.
     * @return name
     */
    String getName();
    /**
     * The display name of the resource.
     * @return display name
     */
    String getDisplayName();
    /**
     * The type of the resource. One of the {@link SecuredResourceTypeInf}.
     * @return type
     */
    SecuredResourceTypeInf getType();
    /**
     * The category of the resource. One of the {@link SecuredResourceCategoryInf}.
     * @return category
     */
    SecuredResourceCategoryInf getCategory();
}
