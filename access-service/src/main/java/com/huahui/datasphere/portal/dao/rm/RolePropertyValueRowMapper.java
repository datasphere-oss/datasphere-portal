

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RolePropertyValue;
import com.huahui.datasphere.portal.security.po.RolePropertyValue.FieldColumns;

/**
 * Row Mapper of RolePropertyValue
 *
 * @author theseusyang
 */
public class RolePropertyValueRowMapper implements RowMapper<RolePropertyValue> {
    @Override
    public RolePropertyValue mapRow(ResultSet rs, int rowNum) throws SQLException {

        RolePropertyValue result = new RolePropertyValue();

        long id = rs.getLong(FieldColumns.ID.name());
        result.setId(rs.wasNull() ? null : id);

        result.setRoleId(rs.getLong(FieldColumns.ROLE_ID.name()));
        result.setValue(rs.getString(FieldColumns.VALUE.name()));

        long propertyId = rs.getLong(FieldColumns.PROPERTY_ID.name());

        RoleProperty property = new RoleProperty();
        property.setId(propertyId);
        property.setName(rs.getString(RoleProperty.FieldColumns.NAME.name()));
        property.setDisplayName(rs.getString(RoleProperty.FieldColumns.DISPLAY_NAME.name()));

        result.setProperty(property);
        return result;
    }
}
