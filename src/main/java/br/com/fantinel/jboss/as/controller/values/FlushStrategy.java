package br.com.fantinel.jboss.as.controller.values;

public enum FlushStrategy {
	/**DEFAULT*/
	FailingConnectionOnly,
	IdleConnections,
	EntirePool
}
