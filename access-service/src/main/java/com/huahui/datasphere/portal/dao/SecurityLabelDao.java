

package com.huahui.datasphere.portal.dao;

import java.util.List;
import java.util.Map;

import com.huahui.datasphere.portal.security.po.LabelAttributeValue;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;


public interface SecurityLabelDao {
    void saveLabelsForObject(int objectId, List<SecurityLabelInf> securityLabels);

    List<LabelAttributeValue> findLabelsAttributesValuesForObject(int objectId);

    /**
     * Clean users' labels values where label don't assigned to role
     *
     * @param roleName role name
     */
    void cleanUsersLabels(String roleName);

    Map<Integer, List<LabelAttributeValue>> fetchObjectsSecurityLabelsValues();
}
