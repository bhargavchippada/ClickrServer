package webinterfacehandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.ServerSettings;
import support.Utils;
import datahandler.AdminProfile;
import datahandler.ClassRoom;

public class ServerState extends HttpServlet{
	String classname = "ServerState";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String server_state = request.getParameter("server_state");
		
		HttpSession mySession = request.getSession(true);
		
		if(server_state.equals("1")){
			Utils.logv(classname, "Server is online!");
			ServerSettings.setServerStatus(1);
			mySession.setAttribute("server-state", 1);
			AdminProfile.createClassroom(ServerSettings.clsnm, ServerSettings.userlist);
			ClassRoom.print();
		}else{
			Utils.logv(classname, "Server is offline");
			ServerSettings.setServerStatus(0);
			ClassRoom.clear();
			mySession.setAttribute("server-state", 0);
		}
	}
}
