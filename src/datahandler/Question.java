package datahandler;

import support.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**Singleton class to store the quiz question
 * @author bhargav
 *
 */
/**
 * @author bhargav
 *
 */
/**
 * @author bhargav
 *
 */
public class Question{
	
	public static String classname = "Question";
	
	public static String ID; //unique question id
	
	public static boolean savedquiz=false; // whether or not a quiz is saved

	public static String title; //question title
	public static String question; //question content
	/**Type of the quiz<br>
	 * 0 => single mcq
	 * 1 => mutiple mcq
	 * 2 => true or false
	 * 3 => word answer
	 * 4 => short answer
	 */
	public static int type;
	public static JsonArray options = new JsonArray(); //options of the quiz
	public static JsonArray answers = new JsonArray(); //answer of the quiz
	public static boolean feedback; //whether or not the quiz gived immediate feedback
	public static boolean timed; //whether or not the quiz is timed
	public static int time; // this is the time is the quiz is timed
	
	public static boolean startquiz = false; // whether or not started quiz
	
	public static JsonArray option_stat = new JsonArray(); //option wise stats
	public static int num_attempts=0; // number of users who attempted the quiz
	public static int num_correct=0; // number of correct attempts
	public static int num_wrong=0; //number of wrong attempts
	
	/**Clear the question details
	 * 
	 */
	public synchronized static void clear(){
		ID=null;
		
		savedquiz = false;
		
		title=null;
		question=null;
		type=-1;
		options=new JsonArray();
		answers=new JsonArray();
		feedback = false;
		timed = false;
		time = -1;
		
		startquiz = false;
		
		option_stat=new JsonArray();
		num_attempts=0;
		num_correct=0;
		num_wrong=0;
	}
	
	/**Clear the question stats
	 * 
	 */
	public synchronized static void quizreset(){
		option_stat=new JsonArray();
		num_attempts=0;
		num_correct=0;
		num_wrong=0;
		
		if(type==0 || type==1){
			for(int i=0;i<answers.size();i++){
				option_stat.add(Utils.gson.toJsonTree(0));
			}
		}else if(type==2){
			option_stat.add(Utils.gson.toJsonTree(0));
			option_stat.add(Utils.gson.toJsonTree(0));
		}
	}
	
	/**Print the question
	 * 
	 */
	public static void print(){
		Utils.logv(classname, "Question ID: "+ID);
		Utils.logv(classname, "savedquiz: "+savedquiz);
		Utils.logv(classname, "title: "+title);
		Utils.logv(classname, "question: "+question);
		Utils.logv(classname, "type: "+type);
		Utils.logv(classname, "Options: ");
		for(int i=0; i<options.size();i++) Utils.logv(classname, options.get(i).toString());
		Utils.logv(classname, "Answers: ");
		for(int i=0; i<answers.size();i++) Utils.logv(classname, answers.get(i).toString());
		Utils.logv(classname, "feedback: "+feedback);
		Utils.logv(classname, "timed: "+timed);
		Utils.logv(classname, "time: "+time);
		Utils.logv(classname, "startquiz: "+startquiz);
	}
	
	/**Synchronized way of incrementing the number of attempts
	 * 
	 */
	public synchronized static void incrementNumAttempts(){
		num_attempts++;
	}
	
	/**Synchronized way of incrementing the number of corrects
	 * 
	 */
	public synchronized static void incrementNumCorrects(){
		num_correct++;
	}
	
	/**Synchronized way of incrementing the number of wrongs
	 * 
	 */
	public synchronized static void incrementNumWrongs(){
		num_wrong++;
	}
	
	/**Synchronized way updating the options stats (number of people has chosen a particular option)
	 * 
	 */
	public synchronized static void updateOptionStats(int pos){
		option_stat.set(pos,Utils.gson.toJsonTree(option_stat.get(pos).getAsInt()+1));
	}
	
	/**Synchronized way of incrementing the options stats (number of people has chosen a particular option)
	 * 
	 */
	public synchronized static void incrementOptionStats(JsonArray myanswers){
		if(myanswers.size()==0) return;
		
		if(type==0){
			int ind = myanswers.getAsInt();
			option_stat.set(ind, Utils.gson.toJsonTree(option_stat.get(ind).getAsInt()+1));
		}else if(type==1){
			for(int i=0;i<myanswers.size();i++){
				if(myanswers.get(i).getAsBoolean()){
					option_stat.set(i, Utils.gson.toJsonTree(option_stat.get(i).getAsInt()+1));
				}
			}
		}else if(type==2){
			if(!myanswers.getAsBoolean()){
				option_stat.set(0, Utils.gson.toJsonTree(option_stat.get(0).getAsInt()+1));
			}else if(myanswers.getAsBoolean()){
				option_stat.set(1, Utils.gson.toJsonTree(option_stat.get(1).getAsInt()+1));
			}
		}else if(type==3){
			return;
		}else if(type==4){
			return;
		}else {
			return;
		}
	}
	
	/**Verify the provided answer with correct answer according to the type of the 
	 * question.
	 * @param myanswers
	 * @return
	 */
	public static boolean verify(JsonArray myanswers){
		
		if(myanswers.size()==0) return false;
		
		if(type==0){
			int ind = myanswers.getAsInt();
			return answers.get(ind).getAsBoolean();
		}else if(type==1){
			return myanswers.equals(answers);
		}else if(type==2){
			return (answers.getAsBoolean() == myanswers.getAsBoolean());
		}else if(type==3){
			return answers.getAsString().toLowerCase().equals(myanswers.getAsString().toLowerCase());
		}else if(type==4){
			return true;
		}
		
		return false;
	}
	
	
	/**Answer for the question
	 * @return String denoting the answer
	 */
	public static String getAnswer(){
		String answer = "Answer:\n";
		if(type==0){
			for(int i=0;i<answers.size();i++){
				if(answers.get(i).getAsBoolean()){
					answer += (i+1)+") "+options.get(i).toString();
				}
			}
		}else if(type==1){
			for(int i=0;i<answers.size();i++){
				if(answers.get(i).getAsBoolean()){
					answer += (i+1)+") "+options.get(i).toString()+"\n";
				}
			}
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
		
		Utils.logv(classname, answer);
		
		return answer;
	}
	
	/**Synchronized way of returning the jsonarray object of stats
	 * @return jsonarray
	 */
	public synchronized static JsonArray getStatsJson(){
		JsonArray jarray = new JsonArray();
		
		JsonObject jgraph = new JsonObject();
		jgraph.addProperty("color", "#5bc0de");
		int totalusers = ClassRoom.users_map.size();
		int value=0;
		if(totalusers!=0) value = (100*num_attempts/totalusers);
		jgraph.addProperty("title", "Attempts "+num_attempts+"/"+totalusers+" ");
		jgraph.addProperty("value", value);
		jarray.add(jgraph);
		
		int totalresponses = ClassRoom.users_responsemap.size();
		jgraph = new JsonObject();
		value=0;
		jgraph.addProperty("color", "#5CB85C");
		if(totalresponses!=0) value = (100*num_correct/totalresponses);
		jgraph.addProperty("title", "Corrects "+num_correct+"/"+totalresponses+" ");
		jgraph.addProperty("value", value);
		jarray.add(jgraph);
		
		jgraph = new JsonObject();
		value=0;
		jgraph.addProperty("color", "#D9534F");
		if(totalresponses!=0) value = (100*num_wrong/totalresponses);
		jgraph.addProperty("title", "Wrongs "+num_wrong+"/"+totalresponses+" ");
		jgraph.addProperty("value", value);
		jarray.add(jgraph);
		
		jarray.addAll(getOptionStatsJson());
		
		return jarray;
	}
	
	/**Synchronized way of returning the jsonarray object of optionstats depending on
	 * the type of the question
	 * @return jsonarray
	 */
	public synchronized static JsonArray getOptionStatsJson(){
		
		JsonArray jarray = new JsonArray();
		int totalresponses = ClassRoom.users_responsemap.size();
		int value = 0;
		int count = 0;
		JsonObject jgraph;
		if(type==0 || type==1){
			for(int i=0;i<option_stat.size();i++){
				value = 0;
				jgraph  = new JsonObject();
				count = option_stat.get(i).getAsInt();
				if(totalresponses!=0) value = (100*count/totalresponses);
				jgraph.addProperty("color", "#337ab7");
				jgraph.addProperty("title", (i+1)+") "+options.get(i).getAsString()+" "+count+"/"+totalresponses+" ");
				jgraph.addProperty("value", value);
				jarray.add(jgraph);
			}
		}else if(type==2){
			//true
			value = 0;
			jgraph  = new JsonObject();
			count = option_stat.get(1).getAsInt();
			if(totalresponses!=0) value = (100*count/totalresponses);
			jgraph.addProperty("color", "#337ab7");
			jgraph.addProperty("title", "True "+count+"/"+totalresponses+" ");
			jgraph.addProperty("value", value);
			jarray.add(jgraph);
			
			
			//false
			value = 0;
			jgraph  = new JsonObject();
			count = option_stat.get(0).getAsInt();
			if(totalresponses!=0) value = (100*count/totalresponses);
			jgraph.addProperty("color", "#337ab7");
			jgraph.addProperty("title", "False "+count+"/"+totalresponses+" ");
			jgraph.addProperty("value", value);
			jarray.add(jgraph);
		}
		
		return jarray;
	}
}
