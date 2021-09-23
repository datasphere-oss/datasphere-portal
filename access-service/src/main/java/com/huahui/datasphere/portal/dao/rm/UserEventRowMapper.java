
package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.huahui.datasphere.portal.security.po.UserEvent;


/**
 * @author theseusyang
 *
 */
public class UserEventRowMapper implements RowMapper<UserEvent> {

    /**
     * Default row mapper.
     */
    public static final RowMapper<UserEvent> DEFAULT_ROW_MAPPER = new UserEventRowMapper();
    /**
     * Default extractor.
     */
    public static final ResultSetExtractor<UserEvent> DEFAULT_RESULT_SET_EXTRACTOR
        = new ResultSetExtractor<UserEvent>() {

        @Override
        public UserEvent extractData(ResultSet rs) throws SQLException {
            return rs != null && rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;
        }
    };
    /**
     * Constructor.
     */
    private UserEventRowMapper() {
        super();
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent po = new UserEvent();

        po.setId(rs.getString(UserEvent.FIELD_ID));
        po.setUserId(rs.getInt(UserEvent.FIELD_USER_ID));
        po.setBinaryDataId(rs.getString(UserEvent.FIELD_BINARY_DATA_ID));
        po.setCharacterDataId(rs.getString(UserEvent.FIELD_CHARACTER_DATA_ID));
        po.setContent(rs.getString(UserEvent.FIELD_CONTENT));
        po.setDetails(rs.getString(UserEvent.FIELD_DETAILS));
        po.setType(rs.getString(UserEvent.FIELD_TYPE));
        po.setCreateDate(rs.getTimestamp(UserEvent.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(UserEvent.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getTimestamp(UserEvent.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(UserEvent.FIELD_UPDATED_BY));

        return po;
    }

}
