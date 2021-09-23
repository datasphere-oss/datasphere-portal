

package com.huahui.datasphere.portal.type.security;

public interface RightInf {
    SecuredResourceInf getSecuredResource();

    boolean isCreate();

    boolean isUpdate();

    boolean isDelete();

    boolean isRead();
}
