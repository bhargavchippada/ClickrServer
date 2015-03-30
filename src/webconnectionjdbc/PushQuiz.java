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
import datahandler.Question;
import datahandler.UserProfile;

public class PushQuiz extends HttpServlet{
	private static final long serialVersionUID = 3622456660944045304L;
	String classname = "PushQuiz";
	HttpSession mySession;

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
		HttpSession mySession = request.getSession(false);

		String uid = input.get("uid").getAsString();

		JsonObject output = new JsonObject();
		if(mySession==null || !(ClassRoom.serveronline)){
			output.addProperty("status",0); //not authorized
		}else if(!Question.startquiz || !Question.savedquiz){
			output.addProperty("status",1); //quiz hasn't started yet
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");

			if(username!=null && password!=null){
				UserProfile user = ClassRoom.users_map.get((String)username);
				if(user!=null && user.password.equals((String) password) && uid.equals((String) username)){
					output.addProperty("status",2); //user quiz can start
					user.status = 2;
					output.addProperty("title", Question.title);
					output.addProperty("question", Question.question);
					output.addProperty("type", Question.type);
					output.add("options", Question.options);
					output.addProperty("feedback", Question.feedback);
					output.addProperty("timed", Question.timed);
					output.addProperty("time", Question.time);
				}else{
					output.addProperty("status",0); //not authorized
				}
			}
		}

		return output;
	}
}
