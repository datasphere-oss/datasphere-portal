

package com.huahui.datasphere.portal.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.huahui.datasphere.portal.security.po.Password;
import com.huahui.datasphere.portal.security.po.User;


public class PasswordWithUserRowMapper extends PasswordRowMapper {
    @Override
    public Password mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Password passwordPO = super.mapRow(rs, rowNum);
        final User user = new User();
        user.setId(rs.getInt("userId"));
        user.setLogin(rs.getString(User.Fields.LOGIN));
        user.setEmail(rs.getString(User.Fields.EMAIL));
        user.setEmailNotification(rs.getBoolean(User.Fields.EMAIL_NOTIFICATION));
        user.setLocale(rs.getString(User.Fields.LOCALE));
        user.setAdmin(rs.getBoolean(User.Fields.ADMIN));

        passwordPO.setUser(user);
        return passwordPO;
    }
}
