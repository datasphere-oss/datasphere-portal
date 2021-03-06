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

/**
 * Date: 29.04.2016
 */

package com.huahui.datasphere.portal.dto;

import java.util.List;

/**
 * Common class used to return one page with overall items count.
 *
 * @author amagdenko
 */
public abstract class PaginatedResultDTO<T> {
    /**
     * The page - data portion.
     */
    private List<T> page;
    /**
     * Total count of the items.
     */
    private int totalCount;
    /**
     * Gets current page.
     * @return 'page' list
     */
    public List<T> getPage() {
        return page;
    }
    /**
     * Sets the page.
     * @param list the page to set
     */
    public void setPage(List<T> list) {
        this.page = list;
    }
    /**
     * Gets total items count.
     * @return total count
     */
    public int getTotalCount() {
        return totalCount;
    }
    /**
     * Sets total items count.
     * @param overallCount the total count to set
     */
    public void setTotalCount(int overallCount) {
        this.totalCount = overallCount;
    }
}
