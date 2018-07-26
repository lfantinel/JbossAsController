package br.com.fantinel.jboss.as.controller.model;

import static br.com.fantinel.jboss.as.controller.util.Util.findByName;
import static br.com.fantinel.jboss.as.controller.util.Util.getBoolean;
import static br.com.fantinel.jboss.as.controller.util.Util.getInteger;
import static br.com.fantinel.jboss.as.controller.util.Util.getString;
import static br.com.fantinel.jboss.as.controller.util.Util.isDefined;
import static org.jboss.as.controller.client.helpers.ClientConstants.VALUE;
import static org.jboss.as.controller.client.helpers.Operations.createUndefineAttributeOperation;
import static org.jboss.as.controller.client.helpers.Operations.createWriteAttributeOperation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import br.com.fantinel.jboss.as.controller.values.Driver;
import br.com.fantinel.jboss.as.controller.values.FlushStrategy;
import br.com.fantinel.jboss.as.controller.values.IDriver;
import br.com.fantinel.jboss.as.controller.values.IsolationLevel;

public class JndiDatasourceHelper implements JndiDataSourceProperties {
	private JndiDatasourceHelper() {}

	public static void setNodeValues(ModelNode node, JndiDatasource ds) {
		
		node.get(POOL_NAME).set(ds.getPoolName());
		node.get(JNDI_NAME).set(ds.getJndiName());
		node.get(ENABLED).set(ds.isEnabled());
		node.get(JTA).set(ds.isJta());
		node.get(USE_JAVA_CONTEXT).set(ds.isUseJavaContext());
		
		String connectionUrl = ds.getConnectionUrl();
		if (isDefined(connectionUrl)) node.get(CONNECTION_URL).set(connectionUrl);

		IDriver driver = ds.getDriver();
		if (isDefined(driver)) node.get(DRIVER_NAME).set(driver.name());

		IsolationLevel level = ds.getIsolationLevel();
		if (isDefined(level)) node.get(TRANSACTION_ISOLATION).set(level.name());
		
		FlushStrategy flushStrategy = ds.getFlushStrategy();
		if (isDefined(flushStrategy)) node.get(FLUSH_STRATEGY).set(flushStrategy.name());

		String checkValidConnectionSql = ds.getCheckValidConnectionSql();
		if (isDefined(checkValidConnectionSql)) {
			node.get(CHECK_VALID_CONNECTION_SQL).set(checkValidConnectionSql);
			node.get(BACKGROUND_VALIDATION).set(ds.isBackgroundValidation());
			Integer milis = ds.getBackgroundValidationMilis();
			if (isDefined(milis)) {
				node.get(BACKGROUND_VALIDATION_MILIS).set(milis);
			}
		}

		node.get(POOL_PREFIL).set(ds.isPrefill());
		
		Integer initialPoolSize = ds.getInitialPoolSize();
		if (isDefined(initialPoolSize)) node.get(INITIAL_POOL_SIZE).set(initialPoolSize);
		
		Integer maxPoolSize = ds.getMaxPoolSize();
		if (isDefined(maxPoolSize)) node.get(MAX_POOL_SIZE).set(maxPoolSize);
		
		Integer minPoolSize = ds.getMinPoolSize();
		if (isDefined(minPoolSize)) node.get(MIN_POOL_SIZE).set(minPoolSize);

		String securityDomain = ds.getSecurityDomain();
		if (isDefined(securityDomain)) {
			node.get(SECURITY_DOMAIN).set(securityDomain);
		} else {
			String userName = ds.getUserName();
			if (isDefined(userName)) node.get(USER_NAME).set(userName);
			String password = ds.getPassword();
			if (isDefined(password)) node.get(PASSWORD).set(password);
		}

		Integer pstmtCacheSize = ds.getPstmtCacheSize();
		if (isDefined(pstmtCacheSize)) {
			node.get(PREPARED_STATEMENTS_CACHE_SIZE).set(pstmtCacheSize);
			node.get(SHARE_PREPARED_STATEMENTS).set(ds.isSharePstmt());
		}

		Integer idleTimeoutMinutes = ds.getIdleTimeoutMinutes();
		if (isDefined(idleTimeoutMinutes)) node.get(IDLE_TIMEOUT_MINUTES).set(idleTimeoutMinutes);
		
		Integer queryTimeout = ds.getQueryTimeout();
		if (isDefined(queryTimeout)) node.get(QUERY_TIMEOUT).set(queryTimeout);
		
		Integer blockingTimeoutMililis = ds.getBlockingTimeoutMililis();
		if (isDefined(blockingTimeoutMililis)) node.get(BLOCKING_TIMEOUT_MILLIS).set(blockingTimeoutMililis);
		
	}
	
	public static JndiDatasource getDatasource(ModelNode node) {
		JndiDatasource ds = new JndiDatasource();
		
		boolean enabled = getBoolean(node, ENABLED, true);
		ds.setEnabled(enabled);
		
		boolean jta = getBoolean(node, JTA, true);
		ds.setJta(jta);
		
		ModelNode properties = node.get(CONNECTION_PROPERTIES);
		if (properties.isDefined()) {			
			List<ModelNode> list = properties.asList();
			for (ModelNode propertyNode : list) {
				final String name = propertyNode.asProperty().getName();
				final Object value;
				final ModelNode property = propertyNode.get(0);
				final ModelType type = property.getType();
				switch (type) {
				case BOOLEAN:
					value = property.get(VALUE).asBoolean();
					break;
				case DOUBLE:
					value = property.get(VALUE).asDouble();
					break;
				case INT:
					value = property.get(VALUE).asInt();
					break;
				case UNDEFINED:
					value = null;
					break;
				case STRING:					
				default:
					value = property.get(VALUE).asString();
					break;
				}
				ds.putConnectionProperty(name, value);
			}
		}
		
		boolean useJavaContext = getBoolean(node, USE_JAVA_CONTEXT, true);
		ds.setUseJavaContext(useJavaContext);
		
		String jndiName = getString(node, JNDI_NAME, "");
		if (isDefined(jndiName)) ds.setJndiName(jndiName);
		
		final String driverName = getString(node, DRIVER_NAME);
		final IDriver driver = findByName(driverName, Driver.class);
		if (isDefined(driver)) ds.setDriver(driver);

		String connectionUrl = getString(node, CONNECTION_URL, "");
		if (isDefined(connectionUrl)) ds.setConnectionUrl(connectionUrl);

		boolean backgroundValidation = getBoolean(node, BACKGROUND_VALIDATION, true);
		ds.setBackgroundValidation(backgroundValidation);
		
		Integer backgroundValidationMilis = getInteger(node, BACKGROUND_VALIDATION_MILIS, 60 * 1000);
		if (isDefined(backgroundValidationMilis)) ds.setBackgroundValidationMilis(backgroundValidationMilis);
		
		String checkValidConnectionSql = getString(node, CHECK_VALID_CONNECTION_SQL, "SELECT 1").toUpperCase();
		if (isDefined(checkValidConnectionSql)) ds.setCheckValidConnectionSql(checkValidConnectionSql);

		boolean prefill = getBoolean(node, POOL_PREFIL, true);
		ds.setPrefill(prefill);
		
		Integer initialPoolSize = getInteger(node, INITIAL_POOL_SIZE, 1);
		if (isDefined(initialPoolSize)) ds.setInitialPoolSize(initialPoolSize);
		
		Integer maxPoolSize = getInteger(node, MAX_POOL_SIZE, 100);
		if (isDefined(maxPoolSize)) ds.setMaxPoolSize(maxPoolSize);
		
		Integer minPoolSize = getInteger(node, MIN_POOL_SIZE, 10);
		if (isDefined(minPoolSize)) ds.setMinPoolSize(minPoolSize);
		
		String userName = getString(node, USER_NAME);
		if (isDefined(userName)) ds.setUserName(userName);
		
		String password = getString(node, PASSWORD);
		if (isDefined(password)) ds.setPassword(password);
		
		String securityDomain = getString(node, SECURITY_DOMAIN);
		if (isDefined(securityDomain)) ds.setSecurityDomain(securityDomain);

		Integer pstmtCacheSize = getInteger(node, PREPARED_STATEMENTS_CACHE_SIZE);
		if (isDefined(pstmtCacheSize)) ds.setPstmtCacheSize(pstmtCacheSize);
		
		boolean sharePstmt = getBoolean(node, SHARE_PREPARED_STATEMENTS, false);
		if (isDefined(sharePstmt)) ds.setSharePstmt(sharePstmt);
		
		Integer idleTimeoutMinutes = getInteger(node, IDLE_TIMEOUT_MINUTES);
		if (isDefined(idleTimeoutMinutes)) ds.setIdleTimeoutMinutes(idleTimeoutMinutes);
		
		Integer queryTimeout = getInteger(node, QUERY_TIMEOUT);
		if (isDefined(queryTimeout)) ds.setQueryTimeout(queryTimeout);
		
		Integer blockingTimeoutMililis = getInteger(node, BLOCKING_TIMEOUT_MILLIS);
		if (isDefined(blockingTimeoutMililis)) ds.setBlockingTimeoutMililis(blockingTimeoutMililis);
		
		String transactionIsolation = getString(node, TRANSACTION_ISOLATION);
		IsolationLevel isolationLevel = findByName(transactionIsolation, IsolationLevel.class);
		if (isDefined(isolationLevel)) ds.setIsolationLevel(isolationLevel);
		
		String flushStrategy = getString(node, FLUSH_STRATEGY);
		FlushStrategy strategy = findByName(flushStrategy, FlushStrategy.class);
		if (isDefined(strategy)) ds.setFlushStrategy(strategy);
		
		return ds;		
	}

	public static List<ModelNode> merge(final ModelNode address, final JndiDatasource orig, final JndiDatasource ds) {
		Field[] fields = JndiDataSourceProperties.class.getDeclaredFields();
		List<ModelNode> operations = new ArrayList<ModelNode>(fields.length);
//		_merge(operations, address, POOL_NAME, orig.getPoolName(), ds.getPoolName());
		_merge(operations, address, JNDI_NAME, orig.getJndiName(), ds.getJndiName());
		_merge(operations, address, ENABLED, orig.isEnabled(), ds.isEnabled());
		_merge(operations, address, JTA, orig.isJta(), ds.isJta());
		_merge(operations, address, USE_JAVA_CONTEXT, orig.isUseJavaContext(), ds.isUseJavaContext());
		_merge(operations, address, CONNECTION_URL, orig.getConnectionUrl(), ds.getConnectionUrl());
		_merge(operations, address, DRIVER_NAME, orig.getDriver(), ds.getDriver());
		_merge(operations, address, TRANSACTION_ISOLATION, orig.getIsolationLevel(), ds.getIsolationLevel());
		_merge(operations, address, FLUSH_STRATEGY, orig.getFlushStrategy(), ds.getFlushStrategy());
		_merge(operations, address, CHECK_VALID_CONNECTION_SQL, orig.getCheckValidConnectionSql(), ds.getCheckValidConnectionSql());
		_merge(operations, address, BACKGROUND_VALIDATION, orig.isBackgroundValidation(), ds.isBackgroundValidation());
		_merge(operations, address, BACKGROUND_VALIDATION_MILIS, orig.getBackgroundValidationMilis(), ds.getBackgroundValidationMilis());
		_merge(operations, address, POOL_PREFIL, orig.isPrefill(), ds.isPrefill());
		_merge(operations, address, INITIAL_POOL_SIZE, orig.getInitialPoolSize(), ds.getInitialPoolSize());
		_merge(operations, address, MAX_POOL_SIZE, orig.getMaxPoolSize(), ds.getMaxPoolSize());
		_merge(operations, address, MIN_POOL_SIZE, orig.getMinPoolSize(), ds.getMinPoolSize());
		_merge(operations, address, SECURITY_DOMAIN, orig.getSecurityDomain(), ds.getSecurityDomain());
		_merge(operations, address, USER_NAME, orig.getUserName(), ds.getUserName());
		_merge(operations, address, PASSWORD, orig.getPassword(), ds.getPassword());
		_merge(operations, address, PREPARED_STATEMENTS_CACHE_SIZE, orig.getPstmtCacheSize(), ds.getPstmtCacheSize());
		_merge(operations, address, SHARE_PREPARED_STATEMENTS, orig.isSharePstmt(), ds.isSharePstmt());
		_merge(operations, address, IDLE_TIMEOUT_MINUTES, orig.getIdleTimeoutMinutes(), ds.getIdleTimeoutMinutes());
		_merge(operations, address, QUERY_TIMEOUT, orig.getQueryTimeout(), ds.getQueryTimeout());
		_merge(operations, address, BLOCKING_TIMEOUT_MILLIS, orig.getBackgroundValidationMilis(), ds.getBackgroundValidationMilis());
		return operations;
	}
	
	private static void _merge(List<ModelNode> operations, ModelNode address, String name, Boolean origValue, Boolean newValue) {
		if (!Objects.equals(origValue, newValue)) {
			ModelNode op;
			if (isDefined(newValue)) {
				op = createWriteAttributeOperation(address, name, newValue);
			} else {
				op = createUndefineAttributeOperation(address, name);
			}
			operations.add(op);
		}
	}
	
	private static void _merge(List<ModelNode> operations, ModelNode address, String name, String origValue, String newValue) {
		if (!Objects.equals(origValue, newValue)) {
			ModelNode op;
			if (isDefined(newValue)) {
				op = createWriteAttributeOperation(address, name, newValue);
			} else {
				op = createUndefineAttributeOperation(address, name);
			}
			operations.add(op);
		}
	}
	
//	private static void _merge(List<ModelNode> operations, ModelNode address, String name, Long origValue, Long newValue) {
//		if (!Objects.equals(origValue, newValue)) {
//			ModelNode op;
//			if (isDefined(newValue)) {
//				op = createWriteAttributeOperation(address, name, newValue);
//			} else {
//				op = createUndefineAttributeOperation(address, name);
//			}
//			operations.add(op);
//		}
//	}
	
	private static void _merge(List<ModelNode> operations, ModelNode address, String name, Integer origValue, Integer newValue) {
		if (!Objects.equals(origValue, newValue)) {
			ModelNode op;
			if (isDefined(newValue)) {
				op = createWriteAttributeOperation(address, name, newValue);
			} else {
				op = createUndefineAttributeOperation(address, name);
			}
			operations.add(op);
		}
	}
	
	private static void _merge(List<ModelNode> operations, ModelNode address, String name, Enum<?> origValue, Enum<?> newValue) {
		if (!Objects.equals(origValue, newValue)) {
			ModelNode op;
			if (isDefined(newValue)) {
				op = createWriteAttributeOperation(address, name, newValue.name());
			} else {
				op = createUndefineAttributeOperation(address, name);
			}
			operations.add(op);
		}
	}
	
	private static void _merge(List<ModelNode> operations, ModelNode address, String name, IDriver origValue, IDriver newValue) {
		if (!Objects.equals(origValue, newValue)) {
			ModelNode op;
			if (isDefined(newValue)) {
				op = createWriteAttributeOperation(address, name, newValue.name());
			} else {
				op = createUndefineAttributeOperation(address, name);
			}
			operations.add(op);
		}
	}
}
