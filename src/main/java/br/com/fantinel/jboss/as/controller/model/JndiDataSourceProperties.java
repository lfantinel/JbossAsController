package br.com.fantinel.jboss.as.controller.model;

public interface JndiDataSourceProperties {
	public static final String
	POOL_NAME = "pool-name",
	ENABLED = "enabled",
	JTA = "jta",
	USE_JAVA_CONTEXT = "use-java-context",
	JNDI_NAME = "jndi-name",
	CONNECTION_URL = "connection-url",
	DRIVER_NAME = "driver-name",
	CHECK_VALID_CONNECTION_SQL = "check-valid-connection-sql",
	BACKGROUND_VALIDATION = "backgroud-validation",
	BACKGROUND_VALIDATION_MILIS = "background-validation-milis",
	POOL_PREFIL = "pool-prefill",
	INITIAL_POOL_SIZE = "initial-pool-size",
	MAX_POOL_SIZE = "max-pool-size",
	MIN_POOL_SIZE = "min-pool-size",
	SECURITY_DOMAIN = "security-domain",
	USER_NAME = "user-name",
	PASSWORD = "password",
	PREPARED_STATEMENTS_CACHE_SIZE = "prepared-statements-cache-size",
	SHARE_PREPARED_STATEMENTS = "share-prepared-statements",
	IDLE_TIMEOUT_MINUTES = "idle-timeout-minutes",
	QUERY_TIMEOUT = "query-timeout",
	BLOCKING_TIMEOUT_MILLIS = "blocking-timeout-millis",
	TRANSACTION_ISOLATION = "transaction-isolation",
	FLUSH_STRATEGY = "flush-strategy",

	/**datasource-properties*/
	CONNECTION_PROPERTIES = "connection-properties",
	TAG = "tag";
}
