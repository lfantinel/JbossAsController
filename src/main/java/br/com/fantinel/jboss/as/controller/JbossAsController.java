package br.com.fantinel.jboss.as.controller;

import static br.com.fantinel.jboss.as.controller.model.JndiDataSourceProperties.CONNECTION_PROPERTIES;
import static br.com.fantinel.jboss.as.controller.model.JndiDataSourceProperties.ENABLED;
import static br.com.fantinel.jboss.as.controller.model.JndiDatasourceHelper.getDatasource;
import static br.com.fantinel.jboss.as.controller.model.JndiDatasourceHelper.setNodeValues;
import static java.util.Arrays.binarySearch;
import static org.jboss.as.controller.client.helpers.ClientConstants.ADD;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP;
import static org.jboss.as.controller.client.helpers.ClientConstants.STEPS;
import static org.jboss.as.controller.client.helpers.ClientConstants.SUBSYSTEM;
import static org.jboss.as.controller.client.helpers.Operations.createAddOperation;
import static org.jboss.as.controller.client.helpers.Operations.createOperation;
import static org.jboss.as.controller.client.helpers.Operations.createReadAttributeOperation;
import static org.jboss.as.controller.client.helpers.Operations.createReadResourceOperation;
import static org.jboss.as.controller.client.helpers.Operations.createRemoveOperation;
import static org.jboss.as.controller.client.helpers.Operations.createWriteAttributeOperation;
import static org.jboss.as.controller.client.helpers.Operations.getFailureDescription;
import static org.jboss.as.controller.client.helpers.Operations.isSuccessfulOutcome;
import static org.jboss.as.controller.client.helpers.Operations.readResult;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.security.auth.callback.CallbackHandler;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.impl.ClientConfigurationImpl;
import org.jboss.dmr.ModelNode;

import br.com.fantinel.jboss.as.controller.exceptions.DatasourceDisabledException;
import br.com.fantinel.jboss.as.controller.model.JndiDatasource;
import br.com.fantinel.jboss.as.controller.model.JndiDatasourceHelper;
import br.com.fantinel.jboss.as.controller.model.ProductInfo;
import br.com.fantinel.jboss.as.controller.values.IDriver;

/**
 * This control was inspired by:
 * https://github.com/techblue/jboss-controller-operation-executor
 * 
 * @author fantinel
 */
public class JbossAsController {

	private static final String DATASOURCES = "datasources";
	private static final String DATA_SOURCE = "data-source";
	private static final String VALUE = "value";
	private static final String PRODUCT_INFO = "product-info";
	private static final String RELOAD_OPERATION = "reload";
	
	private static final String DRIVER_NAME = "driver-name";
	private static final String DRIVER_MODULE_NAME = "driver-module-name";
	private static final String DRIVER_XA_DATASOURCE_CLASS_NAME = "driver-xa-datasource-class-name";
	private static final String JDBC_DRIVER = "jdbc-driver";
	
	private static final String TEST_CONNECTION_IN_POOL = "test-connection-in-pool";
	private static final String SUMMARY = "summary";

	private final String inetAddress;
	private final int admPort;
	private String username =  "", password = "";
	
	public JbossAsController(String address, int admPort) {
		this.inetAddress = address;
		this.admPort = admPort;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void addDataSources(JndiDatasource ds) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, ds.getPoolName());
		address.protect();
		final ModelNode addDataSouorceStep = createAddOperation(address);
		setNodeValues(addDataSouorceStep, ds);
		
		ModelNode cop = Operations.createCompositeOperation();
		cop.get(STEPS).add(addDataSouorceStep);
		
		Map<String, Object> connectionProperties = ds.getConnectionProperties();
		if (connectionProperties != null) {
			Set<Entry<String, Object>> set = connectionProperties.entrySet();
			for (Entry<String, Object> property : set) {
				if (property.getValue() != null) {
					ModelNode addPropertyStep = createAddPropertyOperation(address, property.getKey(), property.getValue());
					cop.get(STEPS).add(addPropertyStep);
				}
			}
		}
		
		executeController(cop);
	}
	
	public void updateDataSources(JndiDatasource ds) throws IOException {
		final JndiDatasource orig = getDatasourceByName(ds.getPoolName());
		
		if (!Objects.equals(orig, ds)) {
			final ModelNode address = new ModelNode();
			address.add(SUBSYSTEM, DATASOURCES);
			address.add(DATA_SOURCE, ds.getPoolName());
			address.protect();
			
			if (orig.getConnectionProperties() != null) {
				ModelNode cop = Operations.createCompositeOperation();
				ModelNode op;
				Set<String> set = orig.getConnectionProperties().keySet();
				for (String key: set) {
					op = createRemovePropertyOperation(address, key);
					cop.get(STEPS).add(op);
				}
				executeController(cop);
			}
			
			List<ModelNode> operations = JndiDatasourceHelper.merge(address, orig, ds);
			
			if (ds.getConnectionProperties() != null) {
				ModelNode op;
				Set<Entry<String, Object>> set = ds.getConnectionProperties().entrySet();
				for (Entry<String, Object> entry : set) {
					if (entry.getValue() != null) {						
						op = createAddPropertyOperation(address, entry.getKey(), entry.getValue());
						operations.add(op);
					}
				}
			}
			
			if (operations.size() > 0) {
				ModelNode cop = Operations.createCompositeOperation();
				for (ModelNode op : operations) {
					cop.get(STEPS).add(op);
				}
				executeController(cop);
			}
		}
	}
	
	public void storeDataSources(JndiDatasource ds) throws IOException {
		boolean exist = isDatasourceExists(ds.getPoolName());
		if (!exist) {
			this.addDataSources(ds);
		} else {
			this.updateDataSources(ds);
		}
	}
	
	public void addConnectionProperty(ModelNode datasourceAddress, String name, String value) throws IOException {
		datasourceAddress.add(CONNECTION_PROPERTIES, name);
		datasourceAddress.protect();
		final ModelNode operation = createAddOperation(datasourceAddress);
		operation.get(VALUE).set(value);
		executeController(operation);
	}

	private boolean filter(JndiDatasource ds, String... tags) {
		if (tags == null || tags.length == 0) return true;
		Arrays.sort(tags);
		return ds.getTag() != null && binarySearch(tags, ds.getTag()) >= 0;
	}

	private boolean filter(JndiDatasource ds, Boolean enable) {
		if (enable == null)	return true;
		try {
			if (enable) {
				return isDatasourceEnabled(ds.getPoolName());
			} else {
				return !isDatasourceEnabled(ds.getPoolName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<JndiDatasource> getDatasources(Boolean enable, String... tags) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.protect();
		
		final ModelNode operation = createReadResourceOperation(address, true);
		final ModelNode response = executeController(operation);
		
		int idx = 0;
		List<ModelNode> pre = response.get(DATA_SOURCE).asList();
		List<JndiDatasource> ret = new ArrayList<JndiDatasource>(pre.size());
		for (ModelNode node : pre) {
			String poolName = node.asProperty().getName();
			ModelNode child = node.get(0);
			JndiDatasource ds = JndiDatasourceHelper.getDatasource(child);
			ds.setPoolName(poolName);
			ds.setPosition(idx++);
			if (filter(ds, tags) && filter(ds, enable)) {
				ret.add(ds);
			}
		}
		return ret;
	}

	public void removeDatasource(String name) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, name);
		address.protect();
		final ModelNode operation = createRemoveOperation(address);
		executeController(operation);
	}

	public void setDatasourceEnable(String name, boolean enable) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, name);
		address.protect();
		final ModelNode operation = createWriteAttributeOperation(address, ENABLED, enable);
		executeController(operation);
	}

	public boolean isDatasourceEnabled(String name) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, name);
		address.protect();
		ModelNode operation = createReadAttributeOperation(address, ENABLED);
		final ModelNode response = executeController(operation);
		return response.asBoolean();
	}

	public boolean isDatasourceExists(String name) {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, name);
		address.protect();
		ModelNode operation = createReadResourceOperation(address, false);
		try {
			ModelNode response = executeController(operation);
			return response.isDefined();
		} catch (IOException e) {
			return false;
		}
	}

	public int indexOfDatasource(String name) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.protect();
		ModelNode operation = createReadResourceOperation(address, false);
		
		ModelNode responce = executeController(operation);
		ModelNode datasources = responce.get(DATA_SOURCE);
		if (datasources.isDefined()) {
			List<ModelNode> list = datasources.asList();
			for (int i = 0; i < list.size(); i++) {
				ModelNode dataSource = list.get(i);
				String _name = dataSource.asProperty().getName();
				if (_name.equals(name)) return i;
			}
		}
		return -1;
	}

	public JndiDatasource getDatasourceByName(String name) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(DATA_SOURCE, name);
		address.protect();
		final ModelNode operation = createReadResourceOperation(address, true);
		final ModelNode response = executeController(operation);
		if (response.isDefined()) {
			JndiDatasource ds = getDatasource(response);
			ds.setPoolName(name);
			return ds;
		} else {
			return null;
		}
	}

	public boolean isDriverExists(IDriver driver) {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(JDBC_DRIVER, driver.name());
		address.protect();
		ModelNode operation = createReadResourceOperation(address, false);
		try {
			ModelNode responce = executeController(operation);
			return responce.isDefined();
		} catch (IOException e) {
			return false;
		}
	}

	public boolean testConnection(String name) throws IOException, DatasourceDisabledException {
		if (!this.isDatasourceEnabled(name)) {
			throw new DatasourceDisabledException(name);
		} else {
			final ModelNode address = new ModelNode();
			address.add(SUBSYSTEM, DATASOURCES);
			address.add(DATA_SOURCE, name);
			address.protect();
			ModelNode operation = createOperation(TEST_CONNECTION_IN_POOL, address);
			ModelNode result = executeController(operation);
			return result.isDefined();
		}
	}

	public void reloadConfiguration(boolean wait) throws IOException {
		executeController(createOperation(RELOAD_OPERATION));
		while (wait && !testConnection()) {
			try {Thread.sleep(3000);
			} catch (InterruptedException e) {}
		}
	}

	public void addDriver(IDriver driver) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(JDBC_DRIVER, driver.name());
		address.protect();
		
		ModelNode operation = createAddOperation(address);
		operation.get(DRIVER_NAME).set(driver.name());
		operation.get(DRIVER_MODULE_NAME).set(driver.getModule());
		operation.get(DRIVER_XA_DATASOURCE_CLASS_NAME).set(driver.getClassName());
		
		executeController(operation);
	}
	
	public void removeDriver(IDriver driver) throws IOException {
		final ModelNode address = new ModelNode();
		address.add(SUBSYSTEM, DATASOURCES);
		address.add(JDBC_DRIVER, driver.name());
		address.protect();
		final ModelNode operation = createRemoveOperation(address);
		executeController(operation);
	}

	public boolean testConnection() {
		try {
			ModelNode result = executeController(createReadResourceOperation(new ModelNode().setEmptyList(),false));
			return result.isDefined();
		} catch (Exception e) {
			return false;
		}
	}

	public ProductInfo getProductInfo() {
		try {
			final ModelNode op = createOperation(PRODUCT_INFO);
			ModelNode result = executeController(op);
			ModelNode summary = result.asList().get(0).require(SUMMARY);
			return new ProductInfo(summary);			
		} catch (Exception e) {
			return null;
		}
	}
	
	
	private ModelNode createAddPropertyOperation(final ModelNode dsAddress, final String attributeName, final Object value) {
		ModelNode addr = dsAddress.clone();
		addr.add(CONNECTION_PROPERTIES, attributeName);
		addr.protect();
		final ModelNode op = createOperation(ADD, addr);
		if (value instanceof Boolean) {
			op.get(VALUE).set((Boolean) value);
		} else if (value instanceof Integer) {
			op.get(VALUE).set((Integer) value);
		} else if (value instanceof Long) {
			op.get(VALUE).set((Long) value);
		} else if (value instanceof ModelNode) {
			op.get(VALUE).set((ModelNode) value);
		} else {
			op.get(VALUE).set((String) value);
		}
		return op;
	}
	
	private ModelNode createRemovePropertyOperation(final ModelNode dsAddress, final String attributeName) {
		ModelNode addr = dsAddress.clone();
		addr.add(CONNECTION_PROPERTIES, attributeName);
		addr.protect();
		return Operations.createRemoveOperation(addr);
	}
	
	private ModelNode executeController(ModelNode request) throws IOException {
		ModelControllerClient client = null;
		try {
			
			CallbackHandler authHdl = new AuthenticationCallbackHandler(username, password);
			final ModelControllerClientConfiguration config = ClientConfigurationImpl.create(
                    inetAddress, admPort, authHdl);
			
			client = ModelControllerClient.Factory.create(config);
			
			final ModelNode response = client.execute(new OperationBuilder(request).build());
			if (isSuccessfulOutcome(response)) {
				ModelNode result = readResult(response);
				return result;
			} else {
				String msg;
				if (response.hasDefined(OP)) {
					msg = String.format("Operation '%s' failed: %s", response.get(OP), getFailureDescription(response));
				} else {
					msg = response.toString();
				}
				throw new IOException(msg);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			safeClose(client);
		}
	}

	private void safeClose(final Closeable closeable) {
		try {if (closeable != null) closeable.close();} catch (Exception e) {}
	}

}
