package webconnectionjdbc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import datahandler.ClassRoom;
import datahandler.Question;
import datahandler.UserProfile;
import datahandler.UserResponse;

public class ReceiveAnswer extends JSONHttpServlet{
	private static final long serialVersionUID = 5627352069489872384L;
	String classname = "ReceiveAnswer";

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {

		HttpSession mySession = request.getSession(false);

		String uid = input.get("uid").getAsString();

		JsonObject output = new JsonObject();
		if(mySession==null){
			output.addProperty("status",0); //not authorized
		}else if(!Question.startquiz || !Question.savedquiz || !(ClassRoom.serveronline)){
			output.addProperty("status",1); //something happened to quiz
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");

			if(username!=null && password!=null){
				UserProfile user = ClassRoom.users_map.get((String)username);
				if(user!=null && user.password.equals((String) password) && uid.equals((String) username)){

					UserResponse userresp = ClassRoom.users_responsemap.get((String)username);

					if(userresp!=null){
						output.addProperty("status",2); // already attempted the quiz
						return output;
					}else{
						userresp = new UserResponse();
						userresp.username = (String) username;
						userresp.answers = input.get("myanswer").getAsJsonArray();
						userresp.correct = Question.verify(userresp.answers);
						ClassRoom.users_responsemap.put(userresp.username, userresp);
						user.status = 3; //finished quiz

						output.addProperty("status",3); //response added
						output.addProperty("feedback", userresp.responseString()+Question.getAnswer());
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
