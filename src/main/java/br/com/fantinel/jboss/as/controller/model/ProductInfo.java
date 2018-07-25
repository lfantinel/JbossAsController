package br.com.fantinel.jboss.as.controller.model;

import static br.com.fantinel.jboss.as.controller.util.Util.getString;

import org.jboss.dmr.ModelNode;

public class ProductInfo {
	public final String productName, productVersion, operatingSystem, jvmVersion, jvmVendor;

	public ProductInfo(ModelNode node) {
		productName = getString(node, "product-name", "JBoss");
		productVersion = getString(node, "product-version", "unknown");
		operatingSystem = getString(node, "host-operating-system");

		ModelNode jvm = node.require("jvm");
		jvmVersion = getString(jvm, "jvm-version");
		jvmVendor = getString(jvm, "jvm-vendor");
	}

	@Override
	public String toString() {
		return "Product Name = " + productName + ", " 
				+ "\nProduct Version = " + productVersion + ", "
				+ "\nOperating System = " + operatingSystem + ", " 
				+ "\nJvm Version = " + jvmVersion + ", "
				+ "\nJvm Vendor = " + jvmVendor;
	}

}