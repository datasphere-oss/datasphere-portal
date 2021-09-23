

package com.huahui.datasphere.portal.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import com.huahui.datasphere.portal.dao.SecurityLabelDao;
import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;
import com.huahui.datasphere.portal.type.security.SecurityLabelAttribute;
import com.huahui.datasphere.portal.type.security.SecurityLabelInf;


public class SecurityLabelDaoImpl extends BaseDAOImpl implements SecurityLabelDao {

    private final String selectSecurityLabelsByObject;
    private final String deleteAllObjectSecurityLabelsAttributes;
    private final String addSecurityLabelsAttributeToObject;
    private final String cleanUsersLabelsHavingRole;
    private final String selectObjectsSecurityLabels;


    public SecurityLabelDaoImpl(final String connectionTable, final DataSource dataSource, final Properties sql) {
        super(dataSource);


        selectSecurityLabelsByObject =
                String.format(sql.getProperty("SELECT_SECURITY_LABELS_BY_OBJECT"), connectionTable);
        deleteAllObjectSecurityLabelsAttributes =
                String.format(sql.getProperty("DELETE_ALL_OBJECT_S_LABEL_ATTRIBUTES"), connectionTable);
        addSecurityLabelsAttributeToObject =
                String.format(sql.getProperty("ADD_S_LABEL_ATTRIBUTE_TO_OBJECT"), connectionTable);
        cleanUsersLabelsHavingRole = sql.getProperty("CLEAN_USERS_LABELS_HAVING_ROLE");
        selectObjectsSecurityLabels = String.format(sql.getProperty("SELECT_OBJECTS_LABELS_VALUES"), connectionTable);
    }

    public void saveLabelsForObject(final int objectId, final List<SecurityLabelInf> securityLabels) {
        jdbcTemplate.update(deleteAllObjectSecurityLabelsAttributes, objectId);
        if (CollectionUtils.isEmpty(securityLabels)) {
            return;
        }
        int group = 0;
        for (final SecurityLabelInf securityLabel : securityLabels) {
            group++;
            final List<SecurityLabelAttribute> slas = securityLabel.getAttributes();
            if (CollectionUtils.isEmpty(slas)) {
                continue;
            }
            for (final SecurityLabelAttribute sla : slas) {
                final Map<String, Object> params = new HashMap<>();
                params.put("id", objectId);
                params.put("value", sla.getValue());
                params.put("attributeName", sla.getName());
                params.put("labelName", securityLabel.getName());
                params.put("labelGroup", group);
                namedJdbcTemplate.update(addSecurityLabelsAttributeToObject, params);
            }
        }
    }

    public List<LabelAttributeValue> findLabelsAttributesValuesForObject(final int objectId) {
        final List<Map<String, Object>> rows = namedJdbcTemplate.query(
                selectSecurityLabelsByObject,
                Collections.singletonMap("objectId", objectId),
                (rs, rowNum) -> {
                    final Map<String, Object> row = new HashMap<>();
                    row.put("labelId", rs.getInt("labelId"));
                    row.put("labelName", rs.getString("labelName"));
                    row.put("labelDisplayName", rs.getString("labelDisplayName"));
                    row.put("labelDescription", rs.getString("labelDescription"));
                    row.put("labelAttributeId", rs.getInt("labelAttributeId"));
                    row.put("labelAttributeName", rs.getString("labelAttributeName"));
                    row.put("labelAttributePath", rs.getString("labelAttributePath"));
                    row.put("labelAttributeDescription", rs.getString("labelAttributeDescription"));
                    row.put("labelAttributeValue", new LabelAttributeValue(
                            rs.getInt("labelAttributeValueId"),
                            rs.getString("labelAttributeValueValue"),
                            rs.getInt("labelAttributeValueGroup"),
                            null
                    ));
                    return row;
                }
        );
        final Map<Integer, LabelAttribute> las = new HashMap<>();
        final Map<Integer, Label> ls = new HashMap<>();
        for (final Map<String, Object> row : rows) {
            final LabelAttributeValue labelAttributeValuePO = (LabelAttributeValue) row.get("labelAttributeValue");
            final Integer labelAttributeId = (Integer) row.get("labelAttributeId");
            if (!las.containsKey(labelAttributeId)) {
                final LabelAttribute labelAttributePO = new LabelAttribute(
                        labelAttributeId,
                        (String) row.get("labelAttributeName"),
                        (String) row.get("labelAttributePath"),
                        (String) row.get("labelAttributeDescription")
                );
                las.put(labelAttributeId, labelAttributePO);
                final Integer labelId = (Integer) row.get("labelId");
                if (!ls.containsKey(labelId)) {
                    final Label labelPO = new Label(labelId);
                    labelPO.setName((String) row.get("labelName"));
                    labelPO.setDisplayName((String) row.get("labelDisplayName"));
                    labelPO.setDescription((String) row.get("labelDescription"));
                    ls.put(labelId, labelPO);
                }
                final Label label = ls.get(labelId);
                labelAttributePO.setLabel(label);
                label.addLabelAttribute(labelAttributePO);
            }
            final LabelAttribute labelAttributePO = las.get(labelAttributeId);
            labelAttributeValuePO.setLabelAttribute(labelAttributePO);
            labelAttributePO.addLabelAttributeValue(labelAttributeValuePO);
        }
        return rows.stream().map(r -> (LabelAttributeValue) r.get("labelAttributeValue")).collect(Collectors.toList());
    }

    @Override
    public void cleanUsersLabels(String roleName) {
        namedJdbcTemplate.update(cleanUsersLabelsHavingRole, Collections.singletonMap("roleName", roleName));
    }

    @Override
    public Map<Integer, List<LabelAttributeValue>> fetchObjectsSecurityLabelsValues() {
        return jdbcTemplate.query(selectObjectsSecurityLabels, rs -> {
            final Map<Integer, List<LabelAttributeValue>> result = new HashMap<>();
            while (rs.next()) {
                final int oId = rs.getInt("objectId");
                result.computeIfAbsent(oId, (objectId) -> new ArrayList<>());
                final Label label = new Label();
                label.setName(rs.getString("labelName"));
                final LabelAttribute labelAttribute = new LabelAttribute();
                labelAttribute.setId(rs.getInt("labelAttributeId"));
                labelAttribute.setName(rs.getString("labelAttributeName"));
                labelAttribute.setLabel(label);
                final LabelAttributeValue labelAttributeValuePO = new LabelAttributeValue(
                        null,
                        rs.getString("labelAttributeValue"),
                        rs.getInt("labelAttributeValueGroup"),
                        labelAttribute
                );
                result.get(oId).add(labelAttributeValuePO);
            }
            return result;
        });
    }
}
