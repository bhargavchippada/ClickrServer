package appconnection;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dataclasses.Admin;
import dataclasses.Admins;
import dataclasses.SQLConn;

/**
 * Abstract class for handling communication with mobile app
 * 
 * @author bhargav
 *
 */
public abstract class JSONHttpServlet extends SQLConn {
	private static final long serialVersionUID = 3774283076682999070L;

	protected static final int SUCCESS = 1;
	protected static final int FAIL = 0;

	/**
	 * This method receives data (json format) and calls _processInput method to
	 * process it and send response back
	 *
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("servlet task - start");
		long startTime = System.currentTimeMillis();

		JsonObject output = null;
		try {
			LOGGER.info("trying to extract json object from request");
			JsonElement jelement = new JsonParser().parse(request.getReader());
			JsonObject input = jelement.getAsJsonObject();
			LOGGER.info("got the request from client:" + input.toString());

			output = _processInput(input, request, response);
			LOGGER.info("created response, sending it back...");
		} catch (Exception e) {
			output = null;
			LOGGER.info("Error processing response");
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();

		if (output != null) {
			output.addProperty("status", SUCCESS);
		} else {
			output = new JsonObject();
			output.addProperty("status", FAIL); // error processing request
		}

		response.setContentType("application/json");
		response.setContentLength(output.toString().getBytes().length);
		out.print(output);
		LOGGER.info("response: " + output.toString());

		out.flush();
		out.close();

		LOGGER.info("servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		LOGGER.info(elapsedTime + "ms");
	}

	protected Admin getAdminProfile(String servername) {
		return Admins.getAdmin(servername);
	}

	/**
	 * This method has to be implemented by sub-classes
	 * 
	 * @param input
	 * @param request
	 * @param response
	 * @return
	 */
	protected abstract JsonObject _processInput(JsonObject input, HttpServletRequest request,
			HttpServletResponse response);

}
