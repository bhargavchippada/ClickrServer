package webconnectionjdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import datahandler.ClassRoom;
import datahandler.Question;
import datahandler.UserProfile;
import datahandler.UserResponse;

public class PushQuiz extends JSONHttpServlet{
	private static final long serialVersionUID = 3622456660944045304L;
	String classname = "PushQuiz";

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
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

					UserResponse userresp = ClassRoom.users_responsemap.get((String)username);
					
					if(userresp==null || (Question.savedquiz && userresp.QID!=Question.ID)){
						output.addProperty("status",2); //user quiz can start
						user.status = 2;
						output.addProperty("qid", Question.ID);
						output.addProperty("title", Question.title);
						output.addProperty("question", Question.question);
						output.addProperty("type", Question.type);
						output.add("options", Question.options);
						output.addProperty("feedback", Question.feedback);
						output.addProperty("timed", Question.timed);
						output.addProperty("time", Question.time);
					}else{
						// this user has already attempted the quiz
						output.addProperty("status",3); //user already attempted
					}
				}else{
					output.addProperty("status",0); //not authorized
				}
			}
		}

		return output;
	}

	@Override
	public String getClassname() {
		return classname;
	}
}
