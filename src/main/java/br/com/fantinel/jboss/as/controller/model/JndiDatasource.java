package br.com.fantinel.jboss.as.controller.model;

import static br.com.fantinel.jboss.as.controller.model.JndiDataSourceProperties.TAG;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.fantinel.jboss.as.controller.values.FlushStrategy;
import br.com.fantinel.jboss.as.controller.values.IDriver;
import br.com.fantinel.jboss.as.controller.values.IsolationLevel;

public class JndiDatasource implements Comparable<JndiDatasource> {

	private String poolName;
	private int position;
	private boolean enabled;
	private IDriver driver;
	private String jndiName;
	private String url;
	private Integer port;
	private String databaseName;
	private String userName;
	private String password;
	private boolean backgroundValidation;
	private String checkValidConnectionSql;
	private Integer backgroundValidationMilis;
	private Integer initialPoolSize;
	private Integer minPoolSize;
	private Integer maxPoolSize;
	/** Tenta preencher o pool com {@link #minPoolSize} */
	private boolean prefill;
	private String securityDomain;
	private boolean jta = true;
	private boolean useJavaContext = true;
	/**
	 * Number of prepared statements per connection to be kept open and reused in
	 * subsequent requests.
	 */
	private Integer pstmtCacheSize = 32;
	/**
	 * Whether two requests in the same transaction should return the same statement
	 */
	private boolean sharePstmt = true;
	/** Tempo máx. em MINUTOS que uma conexão pode estar ociosa. (Padrão 15) */
	private Integer idleTimeoutMinutes = 5; // 5 minutos
	/** Tempo máx. em SEGUNDOS antes de uma consulta expirar */
	private Integer queryTimeout = 60 * 60; // 1 hora
	/**
	 * Tempo em para aguardar a disponibilidade de uma conexão quando todas estão em
	 * uso. (Padrão 30000)
	 */
	private Integer blockingTimeoutMililis = 60 * 1000; // 1 minutos
	private IsolationLevel isolationLevel;
	private FlushStrategy flushStrategy;
	
	private LinkedHashMap<String, Object> connectionProperties;

	public JndiDatasource() {
	}

	public String getPoolName() {
		return poolName;
	}
	
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final IDriver getDriver() {
		return driver;
	}

	public final void setDriver(IDriver driver) {
		this.driver = driver;
	}

	public final String getJndiName() {
		return jndiName;
	}

	public final void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final Integer getPort() {
		return port;
	}

	public final void setPort(Integer port) {
		this.port = port;
	}

	public final String getDatabaseName() {
		return databaseName;
	}

	public final void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public final String getUserName() {
		return userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	public final boolean isBackgroundValidation() {
		return backgroundValidation;
	}

	public final void setBackgroundValidation(boolean backgroundValidation) {
		this.backgroundValidation = backgroundValidation;
	}

	public final String getCheckValidConnectionSql() {
		return checkValidConnectionSql;
	}

	public final void setCheckValidConnectionSql(String checkValidConnectionSql) {
		this.checkValidConnectionSql = checkValidConnectionSql;
	}

	public final Integer getBackgroundValidationMilis() {
		return backgroundValidationMilis;
	}

	public final void setBackgroundValidationMilis(Integer backgroundValidationMilis) {
		this.backgroundValidationMilis = backgroundValidationMilis;
	}

	public final Integer getInitialPoolSize() {
		return initialPoolSize;
	}

	public final void setInitialPoolSize(Integer initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public final Integer getMinPoolSize() {
		return minPoolSize;
	}

	public final void setMinPoolSize(Integer minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public final Integer getMaxPoolSize() {
		return maxPoolSize;
	}

	public final void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public final boolean isPrefill() {
		return prefill;
	}

	public final void setPrefill(boolean prefill) {
		this.prefill = prefill;
	}

	public final String getSecurityDomain() {
		return securityDomain;
	}

	public final void setSecurityDomain(String securityDomain) {
		this.securityDomain = securityDomain;
	}

	public final boolean isJta() {
		return jta;
	}

	public final void setJta(boolean jta) {
		this.jta = jta;
	}

	public final boolean isUseJavaContext() {
		return useJavaContext;
	}

	public final void setUseJavaContext(boolean useJavaContext) {
		this.useJavaContext = useJavaContext;
	}

	public final Integer getPstmtCacheSize() {
		return pstmtCacheSize;
	}

	public final void setPstmtCacheSize(Integer pstmtCacheSize) {
		this.pstmtCacheSize = pstmtCacheSize;
	}

	public final boolean isSharePstmt() {
		return sharePstmt;
	}

	public final void setSharePstmt(boolean sharePstmt) {
		this.sharePstmt = sharePstmt;
	}

	public final Integer getIdleTimeoutMinutes() {
		return idleTimeoutMinutes;
	}

	public final void setIdleTimeoutMinutes(Integer idleTimeoutMinutes) {
		this.idleTimeoutMinutes = idleTimeoutMinutes;
	}

	public final Integer getQueryTimeout() {
		return queryTimeout;
	}

	public final void setQueryTimeout(Integer queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public final Integer getBlockingTimeoutMililis() {
		return blockingTimeoutMililis;
	}

	public final void setBlockingTimeoutMililis(Integer blockingTimeoutMililis) {
		this.blockingTimeoutMililis = blockingTimeoutMililis;
	}
	
	public IsolationLevel getIsolationLevel() {
		return isolationLevel;
	}
	
	public void setIsolationLevel(IsolationLevel isolationLevel) {
		this.isolationLevel = isolationLevel;
	}
	
	public FlushStrategy getFlushStrategy() {
		return flushStrategy;
	}
	
	public void setFlushStrategy(FlushStrategy flushStrategy) {
		this.flushStrategy = flushStrategy;
	}
	
	public void putConnectionProperty(String name, Object value) {
		if (value == null) {
			this.removeConnectionProperty(name);
		} else {			
			if (connectionProperties == null) connectionProperties = new LinkedHashMap<String, Object>(1);
			connectionProperties.put(name, value);
		}
	}
	
	public Object getConnectionProperty(String name) {
		if (connectionProperties == null) return null;
		return connectionProperties.get(name);
	}
	
	public void removeConnectionProperty(String name) {
		if (connectionProperties != null) connectionProperties.remove(name);
	}
	
	public Map<String, Object> getConnectionProperties() {
		return connectionProperties;
	}
	
	public String getTag() {
		Object tag = getConnectionProperty(TAG);
		return (String) (tag != null ? tag : null);
	}
	
	public void setTag(String tag) {
		this.putConnectionProperty(TAG, tag);
	}
	
	public String getConnectionUrl() {
		if (driver != null) {
			return driver.getUrl(url, port, databaseName);
		}
		return "";
	}

	public void setConnectionUrl(String connectionUrl) {
		if (driver != null) {			
			String[] v = driver.parseUrl(connectionUrl);
			if (v != null && v.length >= 3) {			
				this.url = v[0];
				this.port = Integer.valueOf(v[1]);
				this.databaseName = v[2];
			}
		}
	}
	
	public int compareTo(JndiDatasource o) {
		return this.toString().compareToIgnoreCase(o.toString());
	}
	
	@Override
	public String toString() {
		return poolName  + "(" + jndiName + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (backgroundValidation ? 1231 : 1237);
		result = prime * result + ((backgroundValidationMilis == null) ? 0 : backgroundValidationMilis.hashCode());
		result = prime * result + ((blockingTimeoutMililis == null) ? 0 : blockingTimeoutMililis.hashCode());
		result = prime * result + ((checkValidConnectionSql == null) ? 0 : checkValidConnectionSql.hashCode());
		result = prime * result + ((connectionProperties == null) ? 0 : connectionProperties.hashCode());
		result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((flushStrategy == null) ? 0 : flushStrategy.hashCode());
		result = prime * result + ((idleTimeoutMinutes == null) ? 0 : idleTimeoutMinutes.hashCode());
		result = prime * result + ((initialPoolSize == null) ? 0 : initialPoolSize.hashCode());
		result = prime * result + ((isolationLevel == null) ? 0 : isolationLevel.hashCode());
		result = prime * result + ((jndiName == null) ? 0 : jndiName.hashCode());
		result = prime * result + (jta ? 1231 : 1237);
		result = prime * result + ((maxPoolSize == null) ? 0 : maxPoolSize.hashCode());
		result = prime * result + ((minPoolSize == null) ? 0 : minPoolSize.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((poolName == null) ? 0 : poolName.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + position;
		result = prime * result + (prefill ? 1231 : 1237);
		result = prime * result + ((pstmtCacheSize == null) ? 0 : pstmtCacheSize.hashCode());
		result = prime * result + ((queryTimeout == null) ? 0 : queryTimeout.hashCode());
		result = prime * result + ((securityDomain == null) ? 0 : securityDomain.hashCode());
		result = prime * result + (sharePstmt ? 1231 : 1237);
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + (useJavaContext ? 1231 : 1237);
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JndiDatasource other = (JndiDatasource) obj;
		if (backgroundValidation != other.backgroundValidation)
			return false;
		if (backgroundValidationMilis == null) {
			if (other.backgroundValidationMilis != null)
				return false;
		} else if (!backgroundValidationMilis.equals(other.backgroundValidationMilis))
			return false;
		if (blockingTimeoutMililis == null) {
			if (other.blockingTimeoutMililis != null)
				return false;
		} else if (!blockingTimeoutMililis.equals(other.blockingTimeoutMililis))
			return false;
		if (checkValidConnectionSql == null) {
			if (other.checkValidConnectionSql != null)
				return false;
		} else if (!checkValidConnectionSql.equals(other.checkValidConnectionSql))
			return false;
		if (connectionProperties == null) {
			if (other.connectionProperties != null)
				return false;
		} else if (!connectionProperties.equals(other.connectionProperties))
			return false;
		if (databaseName == null) {
			if (other.databaseName != null)
				return false;
		} else if (!databaseName.equals(other.databaseName))
			return false;
		if (driver != other.driver)
			return false;
		if (enabled != other.enabled)
			return false;
		if (flushStrategy != other.flushStrategy)
			return false;
		if (idleTimeoutMinutes == null) {
			if (other.idleTimeoutMinutes != null)
				return false;
		} else if (!idleTimeoutMinutes.equals(other.idleTimeoutMinutes))
			return false;
		if (initialPoolSize == null) {
			if (other.initialPoolSize != null)
				return false;
		} else if (!initialPoolSize.equals(other.initialPoolSize))
			return false;
		if (isolationLevel != other.isolationLevel)
			return false;
		if (jndiName == null) {
			if (other.jndiName != null)
				return false;
		} else if (!jndiName.equals(other.jndiName))
			return false;
		if (jta != other.jta)
			return false;
		if (maxPoolSize == null) {
			if (other.maxPoolSize != null)
				return false;
		} else if (!maxPoolSize.equals(other.maxPoolSize))
			return false;
		if (minPoolSize == null) {
			if (other.minPoolSize != null)
				return false;
		} else if (!minPoolSize.equals(other.minPoolSize))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (poolName == null) {
			if (other.poolName != null)
				return false;
		} else if (!poolName.equals(other.poolName))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (prefill != other.prefill)
			return false;
		if (pstmtCacheSize == null) {
			if (other.pstmtCacheSize != null)
				return false;
		} else if (!pstmtCacheSize.equals(other.pstmtCacheSize))
			return false;
		if (queryTimeout == null) {
			if (other.queryTimeout != null)
				return false;
		} else if (!queryTimeout.equals(other.queryTimeout))
			return false;
		if (securityDomain == null) {
			if (other.securityDomain != null)
				return false;
		} else if (!securityDomain.equals(other.securityDomain))
			return false;
		if (sharePstmt != other.sharePstmt)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (useJavaContext != other.useJavaContext)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
