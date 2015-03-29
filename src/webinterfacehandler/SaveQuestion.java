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

import datahandler.Question;
import datahandler.ServerSettings;

public class SaveQuestion extends HttpServlet{
	String classname = "SaveQuestion";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");
		
		String questionContent = request.getParameter("questionContent");
		
		JsonElement qtype_jelement = new JsonParser().parse(request.getParameter("quesType"));
		
		int quesType = qtype_jelement.getAsInt();
		
		JsonElement options_jelement = new JsonParser().parse(request.getParameter("optionsList"));
		JsonArray options_array = options_jelement.getAsJsonArray();
		
		JsonElement answers_jelement = new JsonParser().parse(request.getParameter("answersList"));
		JsonArray answers_array = answers_jelement.getAsJsonArray();
		
		HttpSession mySession = request.getSession(true);
		/*
		Utils.logv(classname, questionContent);
		Utils.logv(classname, options_array.toString());
		Utils.logv(classname, answers_array.toString());
		*/
		Question.updateQuestion(questionContent, quesType, options_array, answers_array);
		Question.print();
		
		PrintWriter out = response.getWriter();
		out.print("hello");
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();
		
	}
}
