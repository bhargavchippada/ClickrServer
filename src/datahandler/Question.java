package datahandler;

import support.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Question{
	
	public static String classname = "Question";
	
	public static String ID;
	
	public static boolean savedquiz=false; // whether or not a quiz is saved

	public static String title;
	public static String question;
	public static int type;
	public static JsonArray options = new JsonArray();
	public static JsonArray answers = new JsonArray();
	public static boolean feedback;
	public static boolean timed;
	public static int time;
	
	public static boolean startquiz = false; // whether or not started quiz
	
	public static JsonArray option_stat = new JsonArray();
	public static int num_attempts=0;
	public static int num_correct=0;
	public static int num_wrong=0;
	
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
	
	public synchronized static void incrementNumAttempts(){
		num_attempts++;
	}
	
	public synchronized static void incrementNumCorrects(){
		num_correct++;
	}
	
	public synchronized static void incrementNumWrongs(){
		num_wrong++;
	}
	
	public synchronized static void updateOptionStats(int pos){
		option_stat.set(pos,Utils.gson.toJsonTree(option_stat.get(pos).getAsInt()+1));
	}
	
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
