package br.com.fantinel.jboss.as.controller.values;

import java.io.File;

public enum Driver implements IDriver {
	FireBird   (1, 3050, "org.firebirdsql","org.firebirdsql.jdbc.FBDriver"               ,"jdbc:firebirdsql:{url}/{port}:{database}"),
	
	IBM_DB2    (2, 50000,"com.ibm"        ,"com.ibm.db2.jdbc.app.DB2Driver"              ,"jdbc:db2://{url}:{port}/{database}"),
	
	MsSqlServer(3, 1433, "com.microsoft"  ,"com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://{url}:{port};DatabaseName={database}"),
	
	MySQL      (4, 3306, "com.mysql"      ,"com.mysql.jdbc.Driver"                       ,"jdbc:mysql://{url}:{port}/{database}"),
	
	Oracle     (5, 1521, "com.oracle"     ,"oracle.jdbc.driver.OracleDriver"             ,"jdbc:oracle:thin:@{url}:{port}:{database}"),
	
	PostgreSql (6, 5432, "org.postgresql" ,"org.postgresql.Driver"                       ,"jdbc:postgresql://{url}:{port}/{database}"),
	
	Progress   (7, 3001, "com.progress"   ,"com.progress.sql.jdbc.JdbcProgressDriver"    ,"jdbc:jdbcprogress:T:{url}:{port}:{database}"),
	
	Sybase     (8, 5000, "com.sybase"     ,"com.sybase.jdbc.SybDriver"                   ,"jdbc:sybase:Tds:{url}:{port}/{database}?JCONNECT_VERSION=6"),
	
	Custom     (-1, 0, null, null, null);
	
	private final int id;
	private final String jdbcUrlPattern;
	private final String module, className;
	private final int defaultPort;
	
	private Driver(int id, int defaultPort, String module, String className, String jdbUrlPattern) {
		this.id = id;
		this.jdbcUrlPattern = jdbUrlPattern;
		this.module = module;
		this.className = className;
		this.defaultPort = defaultPort;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getJdbcUrlPattern() {
		return jdbcUrlPattern;
	}

	@Override
	public String getModule() {
		return module;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public int getDefaultPort() {
		return defaultPort;
	}

	@Override
	public String getUrl(String url, String database) {
		return this.getUrl(url, defaultPort, database);
	}
	
	@Override
	public String getUrl(String url, int port, String database) {
		return jdbcUrlPattern
				.replace("{url}", url)
				.replace("{port}", port+"")
				.replace("{database}", database);
	}
	
	@Override
	public String[] parseUrl(String url) {
		try {
			String[] p = jdbcUrlPattern.split("\\{url\\}|\\{port\\}|\\{database\\}");
			String pattern = "";
			
			boolean isIPv6 = url.toUpperCase().matches(".*\\[([0-9A-F]{0,4}:){1,7}[0-9A-F]{0,4}\\].*");
			
			String ipv6 = null;
			if (isIPv6) {
				ipv6 = url.substring(url.indexOf("["), url.indexOf("]")+1);
				url = url.replace(ipv6, "ipv6");
			}
			for (int i = 1; i < p.length; i++) {
				String s = p[i];
				if(i > 1) pattern += "|";
				pattern += s;
			}
			pattern = pattern.replaceAll("\\?", "\\\\?");
			p = url.replaceFirst(p[0], "").split(pattern);
			
			if (isIPv6) p[0] = ipv6;
			
			return new String[] { isIPv6 ? ipv6 : p[0], p[1], p[2] };
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public File getJdbcModulePath(File jbossRoot) {
		return new File(jbossRoot, module.replaceAll("\\.", "/")+"/main");
	}

	public static IDriver findByid(int id) {
		for (Driver v : values()) {
			if (v.id == id) return v;
		}
		return null;
	}
	
}
