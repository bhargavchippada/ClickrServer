package datahandler;

import support.Utils;

import com.google.gson.JsonArray;

/**Class storing a user's response
 * @author bhargav
 *
 */
public class UserResponse {
	
	private static String classname = "UserResponse";
	
	public String username="NULL";
	public String QID="NULL"; //unique question id
	public Boolean correct=false;
	public long startTime=-1;
	public long timeTook=-1; // in secs
	public long submitTime=-1;
	public JsonArray answers = new JsonArray(); //user's answer

	/**When feedback is enabled this method returns the feedback message
	 * @return String response
	 */
	public String responseString(){
		String output;
		if(Question.feedback && Question.type!=4){
			if(correct) output="correct answer!";
			else output="wrong answer!";
		}else{
			output="Answer submitted!";
		}
		
		return output;
	}
	
	/**Print the user response details
	 * 
	 */
	public void print(){
		Utils.logv(classname, "username: "+username);
		Utils.logv(classname, "QID: "+QID);
		Utils.logv(classname, "correct: "+correct);
		Utils.logv(classname, "startTime: "+startTime);
		Utils.logv(classname, "submitTime: "+submitTime);
		Utils.logv(classname, "timeTook: "+timeTook);
		Utils.logv(classname, "answers: "+answers);
	}
	
	/**
	 * @return String user's answer
	 */
	public String getAnswer(){
		String answer = "";
		if(answers.size()==0) return "";
		
		int type = Question.type;
		if(type==0){
			answer += answers.getAsInt()+1;
		}else if(type==1){
			for(int i=0;i<answers.size();i++){
				if(answers.get(i).getAsBoolean()){
					answer += (i+1)+",";
				}
			}
			if(answer.length()!=0) answer = answer.substring(0, answer.length()-1);
		}else if(type==2){
			if(!answers.getAsBoolean()){
				answer+="false";
			}else if(answers.getAsBoolean()){
				answer+="true";
			}
		}else if(type==3){
			answer += answers.getAsString();
		}else if(type==4){
			answer = answers.getAsString();
		}else {
			answer += "NULL";
		}

		return answer;
	}
}
