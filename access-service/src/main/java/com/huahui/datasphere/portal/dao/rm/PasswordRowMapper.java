

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Password;

/**
 * Row mapper for the ResourcePO object.
 * @author theseusyang
 */
public class PasswordRowMapper implements RowMapper<Password>{

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public Password mapRow(ResultSet rs, int rowNum) throws SQLException {
        Password result = new Password();
        result.setId(rs.getInt(Password.Fields.ID));
        result.setActive(rs.getBoolean(Password.Fields.ACTIVE));
        result.setPasswordText(rs.getString(Password.Fields.PASSWORD_TEXT));
        result.setCreatedAt(rs.getTimestamp(Password.Fields.CREATED_AT));
        result.setUpdatedAt(rs.getTimestamp(Password.Fields.UPDATED_AT));
        result.setCreatedBy(rs.getString(Password.Fields.CREATED_BY));
        result.setUpdatedBy(rs.getString(Password.Fields.UPDATED_BY));
        return result;
    }

}
