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

import datahandler.ServerSettings;

public class QuizState extends HttpServlet{
	String classname = "QuizState";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");
		
		HttpSession mySession = request.getSession(true);
		int quiz_state = (int)mySession.getAttribute("quiz-state");

		if(quiz_state==0){
			Utils.logv(classname, "Quiz is online!");
			ServerSettings.setQuizsStatus(1);
			mySession.setAttribute("quiz-state", 1);
			responseJson.addProperty("quizstate",1);
		}else{
			Utils.logv(classname, "Quiz is offline");
			ServerSettings.setQuizsStatus(0);
			mySession.setAttribute("quiz-state", 0);
			responseJson.addProperty("quizstate",0);
		}
		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
	}
}
