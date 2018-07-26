package br.com.fantinel.jboss.as.controller.values;

import java.io.File;

public interface IDriver {

	int getId();
	
	String name();

	String getJdbcUrlPattern();

	String getModule();

	String getClassName();

	int getDefaultPort();

	String getUrl(String url, String database);

	String getUrl(String url, int port, String database);

	String[] parseUrl(String url);

	File getJdbcModulePath(File jbossRoot);

}