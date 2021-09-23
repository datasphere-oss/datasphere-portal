

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Right;

/**
 * Row mapper for the RightPO class.
 * @author theseusyang
 */
public class RightRowMapper implements RowMapper<Right> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public Right mapRow(ResultSet rs, int rowNum) throws SQLException {
		Right result = new Right();
		result.setId(rs.getInt(Right.Fields.ID));
		result.setName(rs.getString(Right.Fields.NAME));
		result.setDescription(rs.getString(Right.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(Right.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Right.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Right.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Right.Fields.UPDATED_BY));
		return result;
	}

}
