package br.com.fantinel.jboss.as.controller.values;

public enum Flush {
	
	ALL("flush-all-connection-in-pool"),
	IDLE("flush-idle-connection-in-pool"),
	INVALID("flush-invalid-connection-in-pool"),
	GRACEFULLY("flush-gracefully-connection-in-pool");	
	
	public final String op;
	private Flush(String value) {
		this.op = value;
	}
}
