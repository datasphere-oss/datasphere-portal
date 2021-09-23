

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Resource;

/**
 * Row mapper for the ResourcePO object.
 * @author theseusyang
 */
public class ResourceRowMapper implements RowMapper<Resource>{

    /**
     * Default row mapper.
     */
    public static final ResourceRowMapper DEFAULT_ROW_MAPPER
        = new ResourceRowMapper();

    /**
     * Constructor.
     */
    private ResourceRowMapper() {
        super();
    }

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
		Resource result = new Resource();
		result.setId(rs.getInt(Resource.Fields.ID));
		result.setParentId(rs.getInt(Resource.Fields.PARENT_ID) == 0 ? null : rs.getInt(Resource.Fields.PARENT_ID));
		result.setName(rs.getString(Resource.Fields.NAME));
		result.setRType(rs.getString(Resource.Fields.R_TYPE));
		result.setCategory(rs.getString(Resource.Fields.CATEGORY));
		result.setDisplayName(rs.getString(Resource.Fields.DISPLAY_NAME));
		result.setCreatedAt(rs.getTimestamp(Resource.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(Resource.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(Resource.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(Resource.Fields.UPDATED_BY));
		return result;
	}

}
