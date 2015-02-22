package webinterfacehandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.ServerSettings;
import support.Utils;

import com.google.gson.JsonObject;

import datahandler.AdminProfile;
import datahandler.ClassRoom;
import datahandler.Question;

public class AdminLogin extends HttpServlet{

	String classname = "AdminLogin";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("user_username");
		String password = request.getParameter("user_password");

		if(Utils.adminAuthentication(username, password)){
			Utils.logv(classname, "Admin log in success!");
			HttpSession mySession = request.getSession(true);
			AdminProfile.updateSESSIONID(mySession.getId());
			mySession.setAttribute("username", username);
			Utils.logv(username, "SESSIONID: "+mySession.getId());
			Utils.logv(username, "Username: "+mySession.getAttribute("username"));
			ServerSettings.setServerStatus(1);
			AdminProfile.createClassroom(ServerSettings.clsnm, ServerSettings.userlist);
			ClassRoom.print();
			makeQuestion();
			Question.print();
			response.sendRedirect("/ClickerServer/hello.html");
		}else{
			RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
        	request.setAttribute("error_msg","Oops! Authentication error...");
			Utils.logv(classname, "Admin log in failed! "+username+" "+password);
			rd.forward(request, response);
		}
	}

	void makeQuestion(){
		Question.addQuestionContent("Who is the present chief minister of Delhi?");
		Question.addOption("Modi");
		Question.addOption("Kiran Bedi");
		Question.addOption("Arvind Kejriwal");
		Question.addOption("Rahul Gandhi");
		Question.addOption("You");
		Question.addOption("All the above");
		Question.addAnswer("3");
	}
}
