package datahandler;

import support.Utils;

import com.google.gson.JsonArray;

public class UserResponse {
	
	private static String classname = "UserResponse";
	
	public String username="NULL";
	public String QID="NULL";
	public Boolean correct=false;
	public long startTime=-1;
	public long timeTook=-1; // in secs
	public long submitTime=-1;
	public JsonArray answers = new JsonArray();

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
	
	public void print(){
		Utils.logv(classname, "username: "+username);
		Utils.logv(classname, "QID: "+QID);
		Utils.logv(classname, "correct: "+correct);
		Utils.logv(classname, "startTime: "+startTime);
		Utils.logv(classname, "submitTime: "+submitTime);
		Utils.logv(classname, "timeTook: "+timeTook);
		Utils.logv(classname, "answers: "+answers);
	}
	
	public String getAnswer(){
		String answer = "";
		int type = Question.type;
		if(type==0){
			for(int i=0;i<answers.size();i++){
				if(answers.get(i).getAsBoolean()){
					answer += (i+1)+",";
				}
			}
			if(answer.length()!=0) answer = answer.substring(0, answer.length()-1);
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
