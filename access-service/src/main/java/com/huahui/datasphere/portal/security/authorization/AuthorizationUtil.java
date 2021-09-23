package com.huahui.datasphere.portal.security.authorization;

import java.io.IOException;


public class AuthorizationUtil
{
    public static boolean isAuthEnabled() {
        return isRangerEnabled() || isSentryEnabled();
    }
    
    public static boolean isRangerEnabled() {
        return Boolean.valueOf("false");
    }
    
    public static boolean isSentryEnabled() {
        return Boolean.valueOf("false");
    }
    
    public static PolicyUtilsAPI getPolicyUtils(final String jobId) throws IOException {
        if (isRangerEnabled()) {
            return new PolicyUtils(jobId);
        }
        if (isSentryEnabled()) {
            return new infoworks.tools.security.authorization.sentry.PolicyUtils(jobId);
        }
        throw new IOException("Error: Unable to determine authorization policy manager");
    }
}
