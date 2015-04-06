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
import datahandler.ClassRoom;
import datahandler.Question;

public class SaveQuestion extends HttpServlet{
	private static final long serialVersionUID = -1171321495906154882L;
	String classname = "SaveQuestion";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utils.logv(classname, "servlet task - start");
		long startTime = System.currentTimeMillis();

		JsonObject responseJson = new JsonObject();
		response.setContentType(Utils.JSON_TYPE);
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
						responseJson.addProperty("options",Question.options.toString());
						responseJson.addProperty("answers",Question.answers.toString());
						responseJson.addProperty("feedback",Question.feedback);
						responseJson.addProperty("timed",Question.timed);
						responseJson.addProperty("time",Question.time);
					}else{
						responseJson.addProperty("savedquiz",Question.savedquiz);
						responseJson.addProperty("startquiztoggle",Question.startquiz);
					}
					responseJson.addProperty("fetch",true);
					Utils.logv(classname, "fetch: true"+" "+Question.savedquiz);
				}else{
					int action = Integer.parseInt(request.getParameter("action"));
					if(action==0){
						//save question
						Question.ID = Utils.nextSessionId();
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
						
						responseJson.addProperty("action",0);
						Question.quizreset();
						Question.print();
					}else if(action==1){
						boolean squiz = Boolean.valueOf(request.getParameter("startquiztoggle"));
						if(!ClassRoom.serveronline){
							responseJson.addProperty("startquiztoggle",false);
							responseJson.addProperty("error",2); // failed since server was not started
						}else if(!Question.savedquiz && squiz){
							responseJson.addProperty("startquiztoggle",false);
							responseJson.addProperty("error",1); // failed since there is no saved quiz
						}else{
							Question.startquiz = Boolean.valueOf(request.getParameter("startquiztoggle"));
							if(Question.startquiz){
								//started quiz, classroom has to be reset
								ClassRoom.quizreset();
								Question.quizreset();
								Utils.logv(classname, "Classroom reset!");								
							}
							responseJson.addProperty("startquiztoggle",Question.startquiz);
							responseJson.addProperty("error",0); //no error
						}
						responseJson.addProperty("action",1);
					}
					responseJson.addProperty("status",1); // success
					responseJson.addProperty("fetch",false);
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
