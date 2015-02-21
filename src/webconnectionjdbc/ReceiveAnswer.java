package webconnectionjdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.MIMETypeConstantsIF;
import support.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import datahandler.ClassRoom;
import datahandler.Question;

public class ReceiveAnswer extends HttpServlet{
	private static final long serialVersionUID = 5627352069489872384L;
	String classname = "ReceiveAnswer";
	HttpSession mySession;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "Protocol: "+request.getProtocol());
		Utils.logv(classname, "Ip: "+request.getRemoteHost());
		Utils.logv(classname, "port: "+request.getRemotePort());

		mySession = request.getSession(true);
		Cookie[] cookies = request.getCookies();
		HashMap<String, Object> cookies_map = new HashMap<String, Object>();
		if(cookies!=null){
			for(int i=0; i<cookies.length; i++){
				cookies_map.put(cookies[i].getName(), cookies[i].getValue());
			}
			Utils.logv(classname, "cookie: "+cookies_map);
		}

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
			response.setContentType(MIMETypeConstantsIF.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}else {
			output = new JsonObject();
			output.addProperty("status",3); // error processing request
			response.setContentType(MIMETypeConstantsIF.JSON_TYPE);
			response.setContentLength(output.toString().getBytes().length);
			out.print(output);
			Utils.logv(classname, "response: "+output.toString());
		}

		out.flush();
		out.close();
	}

	private JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
		JsonObject output = new JsonObject();
		if(ClassRoom.users_responsemap.containsKey(input.get("uid").getAsString())){
			output.addProperty("status",0);
			return output;
		}

		output.addProperty("status",1);
		int correct;
		JsonArray answer_array = input.get("answer").getAsJsonArray();
		if(Question.answer.get(0).equals(answer_array.get(0).getAsString())){
			correct=1;
		}else{
			correct=0;
		}
		output.addProperty("correct", correct);
		Gson gson = new Gson();
		output.add("answer", gson.toJsonTree(Question.answer).getAsJsonArray());
		Type ArrayListType = new TypeToken<ArrayList<String>>() {
		}.getType();
		ClassRoom.addResponse(input.get("uid").getAsString(), correct, (ArrayList<String>)gson.fromJson(answer_array,ArrayListType));
		return output;
	}
}
