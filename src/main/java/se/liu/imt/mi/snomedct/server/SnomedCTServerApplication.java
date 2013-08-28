package se.liu.imt.mi.snomedct.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import se.liu.imt.mi.snomedct.expressionrepository.ExpressionRepositoryImpl;
import se.liu.imt.mi.snomedct.expressionrepository.api.ExpressionRepository;

/**
 * @author Daniel Karlsson, daniel.karlsson@liu.se
 * 
 */
public class SnomedCTServerApplication extends Application {

	private static final Logger log = Logger
			.getLogger(ExpressionRepositoryImpl.class);
	static Configuration config = null;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SnomedCTServerApplication app = new SnomedCTServerApplication();
	}

	public SnomedCTServerApplication() throws Exception {
		super();
		// initialize configuration
		try {
			config = new XMLConfiguration("config.xml");
			log.debug("Configuration in 'config.xml' loaded");
		} catch (Exception e) {
			log.debug("Exception", e);
			throw e;
		}

		// Create a new component
		Component component = new Component();

		int portNumber = config.getInt("server.port");
		// Create the HTTP server on specified port
		component.getServers().add(Protocol.HTTP, portNumber);

		log.debug("SNOMED CT server started on port " + portNumber);

		// Create a new context and attach it to the component
		Context sharedContext = component.getContext().createChildContext();

		// Create a new expression repository object and attach it to the
		// context
		log.debug("Creating expression repository object");
		ExpressionRepository repo = new ExpressionRepositoryImpl();
		sharedContext.getAttributes().put("ExpressionRepository", repo);

		SnomedCTServerApplication application = new SnomedCTServerApplication(
				sharedContext);

		component.getDefaultHost().attach(application);
		component.start();

	}

	public SnomedCTServerApplication(Context context) {
		super(context);
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());
		router.attach("/query", SnomedCTQueryResource.class);
		router.attach("/getExpressionID", ExpressionIDResource.class);

		return router;
	}

}
