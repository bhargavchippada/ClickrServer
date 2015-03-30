package webinterfacehandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.Utils;

import com.google.gson.JsonObject;

import datahandler.AdminProfile;

public class AdminLogin extends HttpServlet{

	private static final long serialVersionUID = 8316579451970438895L;
	String classname = "AdminLogin";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("user_username");
		String password = request.getParameter("user_password");

		JsonObject responseJson = new JsonObject();
		response.setContentType(Utils.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");

		if(AdminProfile.adminAuthentication(username, password)){
			Utils.logv(classname, "Admin log in success!");
			HttpSession mySession = request.getSession(true);
			mySession.setMaxInactiveInterval(-1);
			mySession.setAttribute("username", username);
			mySession.setAttribute("password", password);

			Utils.logv(username, "SESSIONID: "+mySession.getId());
			Utils.logv(username, "Username: "+mySession.getAttribute("username"));

			responseJson.addProperty("url","/ClickrServer/home.html");
			responseJson.addProperty("redirectURL",true);
		}else{
			Utils.logv(classname, "Admin log in failed! "+username+" "+password);
			responseJson.addProperty("error_msg","Username/Password doesn't match!");
			responseJson.addProperty("redirectURL",false);
		}
		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
	}
}