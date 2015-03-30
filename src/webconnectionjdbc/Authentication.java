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

import support.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import datahandler.ClassRoom;
import datahandler.UserProfile;

// Tells whether the 
public class Authentication extends HttpServlet{
	private static final long serialVersionUID = -340324876755957651L;
	String classname = "Authentication";
	HttpSession mySession;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "Protocol: "+request.getProtocol());
		Utils.logv(classname, "Ip: "+request.getRemoteHost());
		Utils.logv(classname, "port: "+request.getRemotePort());
		
		mySession = request.getSession(true);
		Cookie[] cookies = request.getCookies();
		HashMap<String, Object> cookies_map = new HashMap<String, Object>();
		if(cookies!=null){
			for(int i=0; i<cookies.length; i++){
				cookies_map.put(cookies[i].getName(), cookies[i].getValue());
			}
			Utils.logv(classname, "cookie: "+cookies_map);
		}
		
		JsonObject output = null;
		try {
			Utils.logv(classname, "trying to extract json object from request");
			JsonElement jelement = new JsonParser().parse(request.getReader());
			JsonObject  input = jelement.getAsJsonObject();
			Utils.logv(classname, "got the request from client:" + input.toString());

			output = _processInput(input, request, response);
			Utils.logv(classname, "created response, sending it back...");
		}catch (Exception e) {
			Utils.logv(classname,"Error processing response");
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		
		if (output != null) {
			response.setContentType(Utils.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}else {
			output = new JsonObject();
			output.addProperty("status",3); // error processing request
			response.setContentType(Utils.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}

		out.flush();
		out.close();
	}

	private JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
		JsonObject output = new JsonObject();
		String uid = input.get("uid").getAsString();
		String pwd = input.get("pwd").getAsString();
		if(ClassRoom.serveronline){
			UserProfile user = ClassRoom.users_map.get(uid);
			if(user!=null && user.password.equals(pwd)){
		    	output.addProperty("status",2); // authentication success
		    	Cookie cookie = new Cookie("uid", uid);
		    	response.addCookie(cookie);
		    	Utils.logv(classname, "New cookie added: "+cookie);
		    	output.addProperty("name", user.name);
		    	output.addProperty("clsnm", ClassRoom.clsnm);
		    }else{
		    	output.addProperty("status", 1); // authentication failed
		    }
		}else{
			output.addProperty("status",0); //server is not ready
		}
		return output;
	}
}
