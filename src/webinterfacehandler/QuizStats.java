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

import datahandler.AdminProfile;
import datahandler.Question;

public class QuizStats extends HttpServlet{

	private static final long serialVersionUID = -3553473332196774462L;
	String classname = "QuizStats";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Utils.logv(classname, "servlet task - start");
		long startTime = System.currentTimeMillis();

		JsonObject responseJson = new JsonObject();
		response.setContentType(Utils.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");

		HttpSession mySession = request.getSession(false);
		if(mySession == null){
			if(Question.startquiz) responseJson.addProperty("status",-1); // no session
			else{
				//so the user is authenticated
				//fetch true means send data from server else update server information
				boolean fetch = (boolean) Boolean.valueOf(request.getParameter("fetch"));
				if(fetch){
					responseJson.addProperty("status",1); // success
					responseJson.addProperty("quizstats",Question.getStatsJson().toString());
					//Utils.logv(classname, "fetch: true");
				}else{
					// no actions
				}
			}
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");
			if(username!=null && password!=null && AdminProfile.adminAuthentication((String)username, (String)password)){
				//so the user is authenticated
				//fetch true means send data from server else update server information
				boolean fetch = (boolean) Boolean.valueOf(request.getParameter("fetch"));
				if(fetch){
					responseJson.addProperty("status",1); // success
					responseJson.addProperty("quizstats",Question.getStatsJson().toString());
					//Utils.logv(classname, "fetch: true");
				}else{
					// no actions
				}
			}else{
				responseJson.addProperty("status",-1); // authentication failure
				//Utils.logv(classname, "authentication failure");
			}
		}

		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		//Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();

		//Utils.logv(classname,"servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		//Utils.logv(classname,elapsedTime+"ms");
	}
}

