package webinterfacehandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import datahandler.AdminProfile;
import datahandler.ClassRoom;
import datahandler.ServerSettings;

public class UsersSettings extends HttpServlet{
	String classname = "UsersSettings";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");

		HttpSession mySession = request.getSession(false);
		if(mySession == null){
			responseJson.addProperty("status",-1); // no session
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");
			if(username!=null && password!=null && AdminProfile.adminAuthentication((String)username, (String)password)){
				//so the user is authenticated
				//fetch true means send data from server else update server information
				boolean fetch = (boolean) Boolean.valueOf(request.getParameter("fetch"));
				if(fetch){
					responseJson.addProperty("status",1); // success
					responseJson.addProperty("togglestate",ClassRoom.serveronline);
					responseJson.addProperty("radioselected",ClassRoom.userslist);
					Utils.logv(classname, "fetch: true");
				}else{
					ClassRoom.serveronline = Boolean.valueOf(request.getParameter("togglestate"));
					ClassRoom.userslist = Integer.parseInt(request.getParameter("radioselected"));
					if(ClassRoom.userslist == 0){
						Utils.logv(classname, "userslist: "+request.getParameter("userfile"));
						AdminProfile.createClassroom("test class", request.getParameter("userfile"));
						ClassRoom.printClass();
					}
					responseJson.addProperty("togglestate",ClassRoom.serveronline);
					responseJson.addProperty("radioselected",ClassRoom.userslist);
					responseJson.addProperty("status",1); // success
				}
			}else{
				responseJson.addProperty("status",-1); // authentication failure
				Utils.logv(classname, "authentication failure");
			}
		}

		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
	}
}
