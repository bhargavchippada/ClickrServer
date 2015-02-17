package webinterfacehandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import support.ServerSettings;
import support.Utils;
import datahandler.AdminProfile;
import datahandler.ClassRoom;
import datahandler.Question;

public class AdminLogin extends HttpServlet{
	
	String classname = "AdminLogin";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String username = req.getParameter("user_username");
		String password = req.getParameter("user_password");
		if(Utils.adminAuthentication(username, password)){
			Utils.logi(classname, "Admin log in success!");
			ServerSettings.serveronline = 1;
			AdminProfile.createClassroom(ServerSettings.clsnm, ServerSettings.userlist);
			ClassRoom.print();
			makeQuestion();
			Question.print();
			resp.sendRedirect("/ClickerServer/hello.html");
		}else{
			Utils.logi(classname, "Admin log in failed! "+username+" "+password);
			resp.sendRedirect("/ClickerServer/index.html");
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
