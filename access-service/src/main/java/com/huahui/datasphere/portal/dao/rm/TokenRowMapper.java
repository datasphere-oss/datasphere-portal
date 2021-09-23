

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.BaseToken;
import com.huahui.datasphere.portal.security.po.Token;

public class TokenRowMapper implements RowMapper<BaseToken> {

	@Override
	public BaseToken mapRow(ResultSet rs, int rowNum) throws SQLException {
		Token result = new Token();
		result.setId(rs.getInt(Token.Fields.ID));
		result.setToken(rs.getString(Token.Fields.TOKEN));
		result.setCreatedAt(rs.getTimestamp(Token.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Token.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Token.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Token.Fields.UPDATED_BY));
		return result;
	}

}
