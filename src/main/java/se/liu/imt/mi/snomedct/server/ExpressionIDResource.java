package se.liu.imt.mi.snomedct.server;

import java.io.UnsupportedEncodingException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.apache.log4j.Logger;

import se.liu.imt.mi.snomedct.expressionrepository.api.ExpressionRepository;
import se.liu.imt.mi.snomedct.expressionrepository.api.NonExistingIdException;
import se.liu.imt.mi.snomedct.expressionrepository.datatypes.ExpressionId;

/**
 * REST resource for getting expression IDs from an expression. The request syntax is:
 * http://host:port/getExpressionID?exp=<expression> 
 * Example:
 * http://localhost:8183/getExpressionID?exp=...
 * 
 * @author Daniel Karlsson, daniel.karlsson@liu.se
 *
 */
public class ExpressionIDResource extends ServerResource {
	
	private static Logger log = Logger.getLogger(ExpressionIDResource.class);
	
	@Get
	public String getExpressionID()
			throws UnsupportedEncodingException {
		String expression = getQuery().getValues("exp");
		
		log.debug("expression = " + expression);
		
		ExpressionId result = null;
	
		try {
			ExpressionRepository repo = (ExpressionRepository) getContext()
					.getAttributes().get("ExpressionRepository");

			if (expression != null) {
				result = repo.getExpressionID(expression);
			}
		} catch (NonExistingIdException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return null;
		}
		
		if(result != null)
			return result.getId().toString();
		
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
		
	}

}
