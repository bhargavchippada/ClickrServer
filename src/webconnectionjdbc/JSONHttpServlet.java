package webconnectionjdbc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import support.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class JSONHttpServlet extends HttpServlet {
	private static final long serialVersionUID = 3774283076682999070L;
	private String classname = "JSONHttpServlet";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(getClassname(), "servlet task - start");
		long startTime = System.currentTimeMillis();

		JsonObject output = null;
		try {
			Utils.logv(getClassname(), "trying to extract json object from request");
			JsonElement jelement = new JsonParser().parse(request.getReader());
			JsonObject  input = jelement.getAsJsonObject();
			Utils.logv(getClassname(), "got the request from client:" + input.toString());

			output = _processInput(input, request, response);
			Utils.logv(getClassname(), "created response, sending it back...");
		}catch (Exception e) {
			Utils.logv(getClassname(),"Error processing response");
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();

		if (output != null) {
			response.setContentType(Utils.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(getClassname(), "response: "+output.toString());
		}else {
			output = new JsonObject();
			output.addProperty("status",-1); // error processing request
			response.setContentType(Utils.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(getClassname(), "response: "+output.toString());
		}

		out.flush();
		out.close();

		Utils.logv(getClassname(),"servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		Utils.logv(getClassname(),elapsedTime+"ms");
	}

	public String getClassname(){
		return classname;
	}
	
	protected abstract JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response);

}
