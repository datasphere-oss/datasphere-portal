

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.RoleProperty;
import com.huahui.datasphere.portal.security.po.RoleProperty.FieldColumns;

/**
 * Role ORM Mapper
 *
 * @author theseusyang
 */
public class RolePropertyRowMapper implements RowMapper<RoleProperty> {

    @Override
    public RoleProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
        RoleProperty result = new RoleProperty();

        result.setId(rs.getLong(FieldColumns.ID.name()));
        result.setRequired(rs.getBoolean(FieldColumns.REQUIRED.name()));
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));
        result.setReadOnly(rs.getBoolean(FieldColumns.READ_ONLY.name()));
        result.setFieldType(rs.getString(FieldColumns.FIELD_TYPE.name()));
        return result;
    }
}
