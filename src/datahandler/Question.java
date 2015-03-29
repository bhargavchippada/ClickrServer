package datahandler;

import java.util.ArrayList;

import com.google.gson.JsonArray;

import support.Utils;

public class Question{
	
	public static String classname = "Question";
	
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
	
	public static ArrayList<Integer> option_stat = new ArrayList<Integer>();
	public static int num_attempts=0;
	public static int num_correct=0;
	public static int num_wrong=0;
	
	public synchronized static void clear(){
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
		
		option_stat.clear();
		num_attempts=0;
		num_correct=0;
		num_wrong=0;
	}
	
	public static void print(){
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
		option_stat.set(pos,option_stat.get(pos)+1);
	}
}
