

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.LabelAttribute;
import com.huahui.datasphere.portal.security.po.LabelAttributeValue;

/**
 * Row mapper for the LabelPO object.
 * @author theseusyang
 */
public class LabelAttributeValueRowMapper implements RowMapper<LabelAttributeValue> {

    /**
     * Instance.
     */
    public static final LabelAttributeValueRowMapper DEFAULT_ROW_MAPPER
        = new LabelAttributeValueRowMapper();

    /**
     * Constructor.
     */
    private LabelAttributeValueRowMapper() {
        super();
    }
	
	@Override
	public LabelAttributeValue mapRow(ResultSet rs, int rowNum) throws SQLException {
		LabelAttributeValue result = new LabelAttributeValue();
		result.setId(rs.getInt(LabelAttributeValue.Fields.ID));
		result.setValue(rs.getString(LabelAttributeValue.Fields.VALUE));
		result.setCreatedAt(rs.getTimestamp(LabelAttributeValue.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(LabelAttributeValue.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(LabelAttributeValue.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(LabelAttributeValue.Fields.UPDATED_BY));
		result.setGroup(rs.getInt(LabelAttributeValue.Fields.S_LABEL_GROUP));
		result.setLabelAttribute(new LabelAttribute(rs.getInt(LabelAttributeValue.Fields.S_LABEL_ATTRIBUTE_ID)));
		return result;
	}

}
