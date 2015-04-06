package webconnectionjdbc;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.Utils;

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
		if(mySession==null || !(ClassRoom.serveronline)){
			output.addProperty("status",0); //not authorized
		}else if(!Question.startquiz || !Question.savedquiz){
			output.addProperty("status",1); //something happened to quiz, its over
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");

			if(username!=null && password!=null){
				UserProfile user = ClassRoom.users_map.get((String)username);
				user.print();
				if(user!=null && user.password.equals((String) password) && uid.equals((String) username)){

					if(user.status==3 || user.status==4){
						output.addProperty("status",2); // already attempted the quiz
						return output;
					}else{
						String qid = input.get("qid").getAsString();
						
						if(!qid.equals(Question.ID)){
							output.addProperty("status",1); //the question ids are not matching
						}else{
							UserResponse userresp = new UserResponse();
							userresp.username = (String) username;
							userresp.answers = input.get("myanswer").getAsJsonArray();
							userresp.correct = Question.verify(userresp.answers);
							userresp.QID = input.get("qid").getAsString();
							userresp.startTime = input.get("starttime").getAsLong();
							userresp.submitTime = input.get("submittime").getAsLong();
							userresp.timeTook = input.get("timetook").getAsLong();
							
							ClassRoom.addResponse(userresp);
							if(user.status==2) {
								user.status = 3; //finished quiz
								user.updateTime = Utils.timeformat.format(new Date());
							}
	
							output.addProperty("status",3); //response added
							output.addProperty("feedback", userresp.responseString());
							
							userresp.print();
							
							Utils.logv(classname, "Table: "+ClassRoom.getClassroomJson().toString());
						}
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
