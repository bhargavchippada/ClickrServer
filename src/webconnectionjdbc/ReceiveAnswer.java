package webconnectionjdbc;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import datahandler.ClassRoom;
import datahandler.Question;

public class ReceiveAnswer extends JSONHttpServlet{
	private static final long serialVersionUID = 5627352069489872384L;
	String classname = "ReceiveAnswer";

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
		
		JsonObject output = new JsonObject();
		/*
		if(ClassRoom.users_responsemap.containsKey(input.get("uid").getAsString())){
			output.addProperty("status",2);
			return output;
		}

		output.addProperty("status",1);
		int correct;
		JsonArray answer_array = input.get("answer").getAsJsonArray();
		if(Question.answers.get(0).equals(answer_array.get(0).getAsString())){
			correct=1;
		}else{
			correct=0;
		}
		output.addProperty("correct", correct);
		Gson gson = new Gson();
		output.add("answer", gson.toJsonTree(Question.answers).getAsJsonArray());
		Type ArrayListType = new TypeToken<ArrayList<String>>() {
		}.getType();
		ClassRoom.addResponse(input.get("uid").getAsString(), correct, (ArrayList<String>)gson.fromJson(answer_array,ArrayListType));
		*/
		
		return output;
	}
	
	@Override
	public String getClassname() {
		return classname;
	}
}
