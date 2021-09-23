

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.Label;

/**
 * Row mapper for the LabelPO object.
 * @author theseusyang
 */
public class LabelRowMapper implements RowMapper<Label> {

    /**
     * RM singleton.
     */
    public static final LabelRowMapper DEFAULT_LABEL_ROW_MAPPER = new LabelRowMapper();

    /**
     * Default disabled constructor.
     */
    private LabelRowMapper() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
     * int)
     */
    @Override
    public Label mapRow(ResultSet rs, int rowNum) throws SQLException {

        Label result = new Label();
        result.setId(rs.getInt(Label.Fields.ID));
        result.setName(rs.getString(Label.Fields.NAME));
        result.setDisplayName(rs.getString(Label.Fields.DISPLAY_NAME));
        result.setDescription(rs.getString(Label.Fields.DESCRIPTION));
        result.setCreatedAt(rs.getTimestamp(Label.Fields.CREATED_AT));
        result.setUpdatedAt(rs.getTimestamp(Label.Fields.UPDATED_AT));
        result.setCreatedBy(rs.getString(Label.Fields.CREATED_BY));
        result.setUpdatedBy(rs.getString(Label.Fields.UPDATED_BY));

        return result;
    }

}
