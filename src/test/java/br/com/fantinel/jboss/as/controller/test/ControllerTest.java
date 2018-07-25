package br.com.fantinel.jboss.as.controller.test;

import static br.com.fantinel.jboss.as.controller.values.Driver.PostgreSql;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.IOException;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;

import br.com.fantinel.jboss.as.controller.JbossAsController;
import br.com.fantinel.jboss.as.controller.exceptions.DatasourceDisabledException;
import br.com.fantinel.jboss.as.controller.model.JndiDatasource;
import br.com.fantinel.jboss.as.controller.model.ProductInfo;

@FixMethodOrder(NAME_ASCENDING)
public class ControllerTest {
	JbossAsController ctrl = new JbossAsController("localhost", 9990);
	
	final String DSNAME = "Um Teste";
	private JndiDatasource ds;
	
	public ControllerTest() {
		ctrl.setUsername("admin");
		ctrl.setPassword("123");
		
		ds = new JndiDatasource();
		ds.setPoolName(DSNAME);
		ds.putConnectionProperty("description", "Uma Descrical");
		ds.setTag("NORMAL");
		ds.setEnabled(true);
		ds.setDriver(PostgreSql);
		ds.setJndiName("java:jboss/teste");
		ds.setUrl("localhost");
		ds.setPort(5432);
		ds.setDatabaseName("postgres");
		ds.setUserName("postgres");
		ds.setPassword("142536");
		ds.setBackgroundValidation(true);
		ds.setCheckValidConnectionSql("SELECT 1");
		ds.setBackgroundValidationMilis(60*1000);
		ds.setInitialPoolSize(1);
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(100);
		ds.setPrefill(false);
		ds.setJta(false);
		ds.setUseJavaContext(false);
	}
	
	@Test
	public void testA_Connection1() {
		JbossAsController ctrl = new JbossAsController("localhost", 9990);
		boolean result = ctrl.testConnection();
		assertTrue(result);
	}
	
	@Test
	public void testB_Connection2() {
		JbossAsController ctrl = new JbossAsController("localhost2", 9990);
		boolean result = ctrl.testConnection();
		assertFalse(result);
	}

	@Test
	public void testC_AddDriver() throws IOException {
		boolean exist = ctrl.isDriverExists(PostgreSql);
		if (exist) {
			ctrl.setDatasourceEnable(DSNAME, false);
			ctrl.reloadConfiguration(true);
			ctrl.removeDriver(PostgreSql); 
		}
		ctrl.addDriver(PostgreSql);
		if (exist) {
			ctrl.setDatasourceEnable(DSNAME, true);
			ctrl.reloadConfiguration(true);
		}
	}
	
	@Test
	public void testD_AddDatasouce_NORMAL() throws IOException, DatasourceDisabledException {
		boolean exist = ctrl.isDatasourceExists(DSNAME);
		if (exist) ctrl.removeDatasource(DSNAME);
		
		ctrl.addDataSources(ds, DSNAME);
		exist = ctrl.isDatasourceExists(DSNAME);
		assertTrue(exist);
		
		ds.setJndiName("java:jboss/novo");
		ds.setMinPoolSize(10);
		ds.setMaxPoolSize(1000);
		ds.setCheckValidConnectionSql("SELECT 2");
		ds.setUserName("sa");
		ds.setPassword("123");
		ds.setTag("TAG2");
		ds.putConnectionProperty("description", null);
		ds.putConnectionProperty("newProperty", "The Value");
		ctrl.updateDataSources(ds, DSNAME);
		JndiDatasource tmp = ctrl.getDatasourceByName(DSNAME);
		assertEquals(ds, tmp);
		
		exist = ctrl.isDatasourceExists("Fake");
		assertFalse(exist);
		
//		ctrl.reloadConfiguration(true);
//		if (ds.isEnabled()) {
//			boolean check = ctrl.testConnection(DSNAME);
//			assertTrue(check);
//		}
	}
	
	@Test
	public void testE_GetDatasouce_NORMAL() throws IOException {
		JndiDatasource ds2 = ctrl.getDatasourceByName(DSNAME);
		assertNotNull(ds2);
		assertEquals("getDatasourceByName",ds, ds2);

		List<JndiDatasource> list = ctrl.getDatasources(null, "NORMAL");
		assertNotNull(list);
		assertEquals("getDatasources", ds, list.get(0));
	}	

	@Test
	public void testF_ConnectionEnable_NORMAL() throws IOException, DatasourceDisabledException {
		ctrl.setDatasourceEnable(DSNAME, false);
		boolean enable = ctrl.isDatasourceEnabled(DSNAME);
		assertFalse(enable);
		
		ctrl.setDatasourceEnable(DSNAME, true);
		enable = ctrl.isDatasourceEnabled(DSNAME);
		assertTrue(enable);
	}
	
	@Test
	public void testG_TesteConnection_NORMAL() throws IOException, DatasourceDisabledException {
		boolean check = ctrl.testConnection(DSNAME);
		assertTrue(check);
	}
	
	@Test
	public void testH_RemoveDatasource_NORMAL() throws IOException, DatasourceDisabledException {
		boolean exist = ctrl.isDatasourceExists(DSNAME);
		if (exist) ctrl.removeDatasource(DSNAME);
	}
	
	@Test
	public void testI_ProductInfo_NORMAL() throws IOException, DatasourceDisabledException {
		ProductInfo info = ctrl.getProductInfo();
		assertNotNull(info);
	}
	
	
	
}
