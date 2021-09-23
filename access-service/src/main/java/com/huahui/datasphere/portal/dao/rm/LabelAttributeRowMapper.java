

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Label;
import com.huahui.datasphere.portal.security.po.LabelAttribute;

/**
 * Row mapper for the Label object.
 * @author theseusyang
 */
public class LabelAttributeRowMapper implements RowMapper<LabelAttribute> {

    /**
     * Mapper singleton.
     */
    public static final LabelAttributeRowMapper DEFAULT_ROW_MAPPER = new LabelAttributeRowMapper();

    /**
     * Constructor.
     */
    private LabelAttributeRowMapper() {
        super();
    }
	
	@Override
	public LabelAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
		LabelAttribute result = new LabelAttribute();
		result.setId(rs.getInt(LabelAttribute.Fields.ID));
		result.setName(rs.getString(LabelAttribute.Fields.NAME));
		result.setValue(rs.getString(LabelAttribute.Fields.VALUE));
		result.setPath(rs.getString(LabelAttribute.Fields.PATH));
		result.setDescription(rs.getString(LabelAttribute.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(LabelAttribute.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(LabelAttribute.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(LabelAttribute.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(LabelAttribute.Fields.UPDATED_BY));
		result.setLabel(new Label(rs.getInt(LabelAttribute.Fields.S_LABEL_ID)));
		return result;
	}

}
