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
import com.google.gson.JsonParser;

import datahandler.AdminProfile;
import datahandler.Question;
import datahandler.ServerSettings;

public class SaveQuestion extends HttpServlet{
	private static final long serialVersionUID = -1171321495906154882L;
	String classname = "SaveQuestion";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "servlet task - start");
		long startTime = System.currentTimeMillis();

		JsonObject responseJson = new JsonObject();
		response.setContentType(ServerSettings.JSON_TYPE);
		response.setHeader("Cache-Control", "nocache");

		HttpSession mySession = request.getSession(false);
		if(mySession == null){
			responseJson.addProperty("status",-1); // no session
		}else{
			Object username = mySession.getAttribute("username");
			Object password = mySession.getAttribute("password");
			if(username!=null && password!=null && AdminProfile.adminAuthentication((String)username, (String)password)){
				//so the user is authenticated
				//fetch true means send data from server else update server information
				boolean fetch = (boolean) Boolean.valueOf(request.getParameter("fetch"));
				if(fetch){
					responseJson.addProperty("status",1); // success
					if(Question.savedquiz){
						responseJson.addProperty("savedquiz",Question.savedquiz);
						responseJson.addProperty("startquiztoggle",Question.startquiz);
						responseJson.addProperty("title",Question.title);
						responseJson.addProperty("question",Question.question);
						responseJson.addProperty("type",Question.type);
						responseJson.addProperty("options",Question.options.getAsString());
						responseJson.addProperty("answers",Question.answers.getAsString());
						responseJson.addProperty("feedback",Question.feedback);
						responseJson.addProperty("timed",Question.timed);
						responseJson.addProperty("time",Question.time);
					}else{
						responseJson.addProperty("savedquiz",Question.savedquiz);
						responseJson.addProperty("startquiztoggle",Question.startquiz);
					}
					Utils.logv(classname, "fetch: true"+" "+Question.savedquiz);
				}else{
					Utils.logv(classname, "fetch: false"+" "+request.getParameter("options"));
					int action = Integer.parseInt(request.getParameter("action"));
					if(action==0){
						//save question
						Question.savedquiz = true;
						Question.startquiz = Boolean.valueOf(request.getParameter("startquiztoggle"));
						Question.title = request.getParameter("title");
						Question.question = request.getParameter("question");
						Question.type = Integer.parseInt(request.getParameter("type"));
						Question.options = new JsonParser().parse(request.getParameter("options")).getAsJsonArray();
						Question.answers = new JsonParser().parse(request.getParameter("answers")).getAsJsonArray();
						Question.feedback = Boolean.valueOf(request.getParameter("feedback"));
						Question.timed = Boolean.valueOf(request.getParameter("timed"));
						Question.time = Integer.parseInt(request.getParameter("time"));

						Question.print();
					}
					responseJson.addProperty("status",1); // success
				}
			}else{
				responseJson.addProperty("status",-1); // authentication failure
				Utils.logv(classname, "authentication failure");
			}
		}

		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		Utils.logv(classname, "response: "+responseJson.toString());
		out.flush();
		out.close();

		Utils.logv(classname,"servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		Utils.logv(classname,elapsedTime+"ms");

	}
}
