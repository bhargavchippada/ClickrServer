package webinterfacehandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import support.ServerSettings;
import support.Utils;

import com.google.gson.JsonObject;

public class ServerState extends HttpServlet{
	String classname = "ServerState";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String server_state = request.getParameter("server_state");
		
		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");
		
		if(server_state.equals("1")){
			Utils.logv(classname, "Server is online!");
			ServerSettings.setServerStatus(1);
			responseJson.addProperty("status",true);
		}else{
			Utils.logv(classname, "Server is offline");
			ServerSettings.setServerStatus(0);
			responseJson.addProperty("status",false);
		}
		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
	}
}
