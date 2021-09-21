package com.huahui.datasphere.portal.config;


public interface DSSConstants
{
    public static final String metadbUserName = "metadbUserName";
    public static final String metadbPassword = "metadbPassword";
    public static final String metaDBConnectionOptions = "metadb_connection_options";
    public static final String CATEGORICAL_TYPE = "categoricalType";
    public static final String NUM_CATEGORIES = "numCategories";
    public static final String CATEGORIES = "categories";
    public static final String CATEGORY_FREQUENCIES = "categoryFrequencies";
    public static final String csv = "csv";
    public static final String name = "name";

    public static final String accesslogs = "access_logs";
    public static final String users = "users";
    public static final String profile = "profile";
    public static final String systemusername = "systemusername";
    public static final String USER_PROFILE_KERBEROS_PRINCIPAL = "kerberos_principal";
    public static final String USER_PROFILE_KERBEROS_KEYTAB_FILE = "kerberos_keytab_file";
    public static final String logs_adhoc = "logs_adhoc";

    public static final String NOTIFICATION_TEMPLATES = "notification_templates";
    public static final String NOTIFICATION_CONFIGURATIONS = "notification_configurations";
    public static final String NOTIFICATION_RULES = "notification_rules";

    public static final String COLLECTION_AUTH_CONFIGS = "auth_configs";
    public static final String _F_KEY = "_fkey";
    public static final String _P_KEY = "_pkey";

    public static final String password = "password";
    public static final String pp_key = "pp_key";
    public static final String LOG_ENTITY_LEVEL = "id-name";
    public static final String ID = "id";
    public static final String IDNAME = "id-name";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PORT = "port";
    public static final String HOST = "host";
    public static final String AUTH_TYPE = "auth_type";
    public static final String PRIVATE_KEY_PATH = "private_key_path";
    public static final String PRIVATE_KEY_PASS_PHRASE = "private_key_passphrase";
    public static final String HOST_TYPE = "host_type";
    public static final String FILE_ID = "fileId";

    public static final String role = "role";
    public static final String DSS_SECURITY_IMPERSONATE = "dss_security_impersonate";
    public static final String DSS_SECURITY_RANGER_ENABLED = "dss_security_ranger_enabled";
    public static final String DSS_SECURITY_RANGER_IP = "dss_security_ranger_ip";
    public static final String DSS_SECURITY_RANGER_PORT = "dss_security_ranger_port";
    public static final String DSS_SECURITY_RANGER_ADMIN_USER = "dss_security_ranger_admin_user";
    public static final String DSS_SECURITY_RANGER_ADMIN_PASSWD = "dss_security_ranger_admin_passwd";
    public static final String DSS_SECURITY_RANGER_POLICY_POLL_INTERVAL_SECS = "dss_security_ranger_policy_poll_interval_secs";
    public static final String DSS_SECURITY_RANGER_HDFS_REPO = "dss_security_ranger_hdfs_repo";
    public static final String DSS_SECURITY_RANGER_HIVE_REPO = "dss_security_ranger_hive_repo";
    public static final String DSS_SECURITY_KERBEROS_ENABLED = "dss_security_kerberos_enabled";
    public static final String DSS_SECURITY_DEFAULT_KERBEROS_PRINCIPAL = "dss_security_kerberos_default_principal";
    public static final String DSS_SECURITY_DEFAULT_KERBEROS_KEYTAB_FILE = "dss_security_kerberos_default_keytab_file";
    public static final String DSS_SECURITY_HIVESERVER_PRINCIPAL = "dss_security_kerberos_hiveserver_principal";
    public static final String DSS_SECURITY_CUBEENGINE_KERBEROS_LOGIN_INTERVAL = "dss_security_cubeengine_kerberos_login_interval";
    public static final String DSS_SECURITY_IMPALASERVER_PRINCIPAL = "dss_security_kerberos_impalaserver_principal";
    public static final String DSS_SECURITY_SENTRY_ENABLED = "dss_security_sentry_enabled";
    public static final String DSS_SECURITY_SENTRY_DEFAULT_ROLE_NAME = "dss_security_sentry_default_role_name";
    public static final String DSS_SECURITY_SENTRY_DEFAULT_GROUP = "dss_security_sentry_default_group";
    public static final String DSS_SECURITY_SENTRY_DEFAULT_HIVE_SERVER_NAME = "dss_security_sentry_default_hive_server_name";
    
    public static final String DSS_HIVE_SSL_ENABLED = "dss_hive_ssl_enabled";
    public static final String DSS_HIVE_SSL_TRUSTSTORE_PATH = "dss_hive_ssl_truststore_path";
    public static final String DSS_HIVE_SSL_TRUSTSTORE_PASSWD = "dss_hive_ssl_truststore_passwd";
    public static final String ENABLE_ENCRYPTION_LOGS_KEY = "enable_encryption_logs";
    public static final Boolean ENABLE_ENCRYPTION_LOGS = false;

    public static final String ORCHESTRATOR_HOST_KEY = "orchestrator_host";
    public static final String ORCHESTRATOR_PORT_KEY = "orchestrator_port";
    public static final String ORCHESTRATOR_SSL_ENABLED_KEY = "orchestrator_https_enabled";
    public static final String ORCHESTRATOR_HOST = "localhost";
    public static final int ORCHESTRATOR_PORT = 3009;
    public static final boolean ORCHESTRATOR_SSL_ENABLED = false;

    public static final String IS_WIDE_TABLE_KEY = "is_wide_table";
    public static final String JWT_EXPIRE_TIME = "jwt_expire_time";
    public static final String JWT_ISSUER = "jwt_issuer";
    public static final String JWT_AUDIENCE = "jwt_audience";
    public static final String JWT_KEYSTORE_TYPE = "keystore_type";
    public static final String JWT_KEYSTORE_PATH = "keystore_path";
    public static final String JWT_PRIVATE_KEY_PATH = "privatekey_path";
    public static final String JWT_PUBLIC_KEY_PATH = "publickey_path";
    public static final String JWT_KEYSTORE_ENTRY_ALIAS = "keystore_entry_alias";
    public static final String JWT_KEYSTORE_PASSWORD = "keystore_password";
    public static final String JWT_KEY_PASSWORD = "keystore_key_password";
    public static final String REST_API_HOST = "rest_api_host";
    public static final String REST_API_PORT = "rest_api_port";

    
}
