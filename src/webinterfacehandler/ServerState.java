package webinterfacehandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import support.ServerSettings;
import support.Utils;
import datahandler.AdminProfile;
import datahandler.ClassRoom;

public class ServerState extends HttpServlet{
	String classname = "ServerState";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");
		
		HttpSession mySession = request.getSession(true);
		int server_state = (int)mySession.getAttribute("server-state");

		if(server_state==0){
			Utils.logv(classname, "Server is online!");
			ServerSettings.setServerStatus(1);
			mySession.setAttribute("server-state", 1);
			AdminProfile.createClassroom(ServerSettings.clsnm, ServerSettings.userlist);
			ClassRoom.print();
			responseJson.addProperty("serverstate",1);
		}else{
			Utils.logv(classname, "Server is offline");
			ServerSettings.setServerStatus(0);
			ClassRoom.clear();
			mySession.setAttribute("server-state", 0);
			responseJson.addProperty("serverstate",0);
		}
		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
	}
}
