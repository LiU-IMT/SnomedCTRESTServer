package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import se.liu.imt.mi.snomedct.expressionrepository.ExpressionRepositoryImpl;
import se.liu.imt.mi.snomedct.server.SnomedCTServerApplication;

public class TestSnomedCTServerApplication {

	private static final Logger log = Logger
			.getLogger(ExpressionRepositoryImpl.class);
	
	private static SnomedCTServerApplication app = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		app = new SnomedCTServerApplication();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testSnomedCTServerApplication() throws ResourceException,
			IOException {

		BufferedReader testCaseReader = new BufferedReader(new FileReader(
				"src/test/resources/test_cases.txt"));

		String strLine = null;
		while ((strLine = testCaseReader.readLine()) != null) {

			log.debug("Test case: " + strLine);
			MediaType mimeType = MediaType.TEXT_CSV;
			ClientResource uriResource = new ClientResource(strLine);
			for (int i = 0; i < 2; i++) {
				uriResource.getClientInfo().getAcceptedMediaTypes()
						.add(new Preference<MediaType>(mimeType));

				String result = null;
				uriResource.get();
				if (uriResource.getStatus().isSuccess()
						&& uriResource.getResponseEntity().isAvailable()) {
					result = uriResource.getResponseEntity().getText();
				}

				assertNotNull(result);

				log.debug("Result (" + mimeType.toString() + ") : " + result);

				if (mimeType == MediaType.TEXT_CSV)
					mimeType = MediaType.APPLICATION_JSON;
				else
					mimeType = MediaType.TEXT_CSV;
			}

		}
		testCaseReader.close();
	}

}
