package webconnectionjdbc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
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

public class Authentication extends HttpServlet{
	private static final long serialVersionUID = 8842827877500138557L;
	private static String classname = "Authentication";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "servlet task - start");
		long startTime = System.currentTimeMillis();

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
			output.addProperty("status",-1); // error processing request
			response.setContentType(Utils.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}

		out.flush();
		out.close();

		Utils.logv(classname,"servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		Utils.logv(classname,elapsedTime+"ms");
	}

	private JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
		HttpSession mySession = request.getSession(true);

		Utils.logv(classname, "Protocol: "+request.getProtocol());
		Utils.logv(classname, "Ip: "+request.getRemoteHost());
		Utils.logv(classname, "port: "+request.getRemotePort());

		JsonObject output = new JsonObject();
		String uid = input.get("uid").getAsString();
		String pwd = input.get("pwd").getAsString();
		if(ClassRoom.serveronline){
			UserProfile user = ClassRoom.users_map.get(uid);
			if(user!=null && user.password.equals(pwd)){
				mySession.setAttribute("username", uid);
				mySession.setAttribute("password", pwd);

				user.ipaddress = request.getRemoteHost();
				user.SESSIONID = mySession.getId();
				user.status = 1; // logged in

				output.addProperty("status",2); // authentication success
				output.addProperty("name", user.name);
				output.addProperty("clsnm", ClassRoom.clsnm);
				output.addProperty("ip", user.ipaddress);
			}else{
				output.addProperty("status", 1); // authentication failed
			}
		}else{
			output.addProperty("status",0); //server is not ready
		}
		return output;
	}
}
