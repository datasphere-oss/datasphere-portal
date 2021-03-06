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

import java.io.Serializable;

/**
 * @author Denis Kostovarov
 */
public abstract class AbstractRight implements RightInf, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5229596309091717180L;
    private SecuredResourceInf securedResourceInf;
    private boolean create;
    private boolean update;
    private boolean delete;
    private boolean read;
    @Override
    public SecuredResourceInf getSecuredResource() {
        return securedResourceInf;
    }

    public void setSecuredResource(SecuredResourceInf securedResourceInf) {
        this.securedResourceInf = securedResourceInf;
    }

    @Override
    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    @Override
    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    @Override
    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
