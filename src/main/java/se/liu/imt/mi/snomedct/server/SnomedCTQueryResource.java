package se.liu.imt.mi.snomedct.server;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import org.apache.log4j.Logger;

import se.liu.imt.mi.snomedct.expressionrepository.api.ExpressionRepository;
import se.liu.imt.mi.snomedct.expressionrepository.api.NonExistingIdException;
import se.liu.imt.mi.snomedct.expressionrepository.datatypes.ExpressionId;

/**
 * REST resource for getting results of SNOMED CT query specification queries. The request syntax is:
 * http://host:port/query?q=<query expression> 
 * Example:
 * http://localhost:8183/query?q=...
 * 
 * @author Daniel Karlsson, daniel.karlsson@liu.se
 *
 */
public class SnomedCTQueryResource extends ServerResource {
	
	private static Logger log = Logger.getLogger(ExpressionIDResource.class);

	@Get
	public Representation query() throws UnsupportedEncodingException {
		StringBuilder output = new StringBuilder();

		String queryExpression = getQuery().getValues("q");
		
		log.debug("query = " + queryExpression);

		List<Preference<MediaType>> format = null;

		// exp = form.getFirstValue("exp"); // an expression might be passed
		// expid = form.getFirstValue("id"); // and expression ID might be
		// passed
		format = this.getRequest().getClientInfo().getAcceptedMediaTypes();

		// both exp and expid might not be empty
		// if (exp == null && expid == null)
		// throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		// currently only CSV and JSON are supported, JSON has preference over
		// CSV if both acceptable
		boolean json = false;
		boolean csv = false;
		for (Preference<MediaType> p : format) {
			if (p.getMetadata() == MediaType.TEXT_CSV)
				csv = true;
			if (p.getMetadata() == MediaType.APPLICATION_JSON)
				json = true;
		}
		if (!json && !csv)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);

		if (json)
			output.append("[");

		try {
			ExpressionRepository repo = (ExpressionRepository) getContext()
					.getAttributes().get("ExpressionRepository");

			Collection<ExpressionId> result = null;

			if (queryExpression != null) {
				result = repo.getSCTQueryResult(queryExpression);
			}

			if (result != null) {
				Iterator<ExpressionId> i = result.iterator();
				if (i.hasNext()) {
					ExpressionId e = i.next();
					if (e != null)
						output.append(e.getId().toString());
					while (i.hasNext()) {
						e = i.next();
						output.append(", " + e.getId().toString());
					}
				}
			}
		} catch (NonExistingIdException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		} catch (Exception e) {

			setStatus(Status.SERVER_ERROR_INTERNAL);
			return null;
		}

		MediaType mimeType = null;
		if (json) {
			output.append("]");
			mimeType = MediaType.APPLICATION_JSON;
		} else if (csv) 
			mimeType = MediaType.TEXT_CSV;
		
		Representation result = new StringRepresentation(output.toString(), mimeType);

		return result;
	}

}
