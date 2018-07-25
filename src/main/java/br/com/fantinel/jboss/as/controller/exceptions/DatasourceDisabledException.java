package br.com.fantinel.jboss.as.controller.exceptions;

public class DatasourceDisabledException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatasourceDisabledException(String name) {
		super("Datasource " + name + " is disabled!");
	}
}
