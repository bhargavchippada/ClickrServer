package webconnectionjdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.MIMETypeConstantsIF;
import support.ServerSettings;
import support.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Tells whether the 
public class Ping extends HttpServlet{
	private static final long serialVersionUID = 1L;
	String classname = "Ping";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "Protocol: "+request.getProtocol());
		Utils.logv(classname, "Ip: "+request.getRemoteHost());
		Utils.logv(classname, "port: "+request.getRemotePort());
		
		HttpSession mySession = request.getSession(true);
		Cookie[] cookies = request.getCookies();
		HashMap<String, Object> cookies_map = new HashMap<String, Object>();
		if(cookies!=null){
			for(int i=0; i<cookies.length; i++){
				cookies_map.put(cookies[i].getName(), cookies[i].getValue());
			}
			Utils.logv(classname, "cookie: "+cookies_map.toString());
		}
		
		JsonObject output = null;
		try {
			Utils.logv(classname, "trying to extract json object from request");
			JsonElement jelement = new JsonParser().parse(request.getReader());
			JsonObject  input = jelement.getAsJsonObject();
			Utils.logv(classname, "got the request from client:" + input.toString());

			output = _processInput(input);
			Utils.logv(classname, "created response, sending it back...");
		}catch (Exception e) {
			Utils.logv(classname,"Error processing response");
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		
		Cookie cookie = new Cookie("msg", "server has started");
		if (output != null) {
			response.addCookie(cookie);
			response.setContentType(MIMETypeConstantsIF.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}else {
			output = new JsonObject();
			output.addProperty("ping",0);
			response.addCookie(cookie);
			response.setContentType(MIMETypeConstantsIF.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}

		out.flush();
		out.close();
	}

	// put ping = 1 meaning the server is responding
	private JsonObject _processInput(JsonObject input) {
		JsonObject output = new JsonObject();
		if(ServerSettings.serveronline == 1){
			output.addProperty("ping", 1);
		}else{
			output.addProperty("ping",0);
		}
		return output;
	}
}
