package br.com.fantinel.jboss.as.controller.util;

import org.jboss.dmr.ModelNode;

public final class Util {
	private Util() {}
	
	public static boolean isDefined(Object obj) {
		return obj != null && !obj.toString().trim().isEmpty();
	}
	
	public static Integer getInteger(ModelNode node, String key, Integer defVal) {
		return (!node.get(key.split("\\.")).isDefined()) ? (Integer) defVal : node.get(key.split("\\.")).asInt(defVal);
	}

	public static Integer getInteger(ModelNode node, String key) {
		return (!node.get(key.split("\\.")).isDefined()) ? null : node.get(key.split("\\.")).asInt();
	}

	public static String getString(ModelNode node, String key, String defVal) {
		return (!node.get(key.split("\\.")).isDefined()) ? (String) defVal : node.get(key.split("\\.")).asString();
	}

	public static String getString(ModelNode node, String key) {
		return (!node.get(key.split("\\.")).isDefined()) ? null : node.get(key.split("\\.")).asString();
	}

	public static boolean getBoolean(ModelNode node, String key, boolean defVal) {
		return (!node.get(key.split("\\.")).isDefined()) ? defVal : node.get(key.split("\\.")).asBoolean(defVal);
	}
	
	public static <T extends Enum<T>> T findByName(String name, Class<T> enumType) {
		for (T c : enumType.getEnumConstants()) {
			if (c.name().equals(name)) return c;
		}
		return null;
	}
}
