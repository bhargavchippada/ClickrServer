package datahandler;

import java.util.ArrayList;

import com.google.gson.JsonArray;

import support.Utils;

public class Question{
	
	public static String classname = "Question";
	
	public static String questionContent;
	public static int quesType=-1;
	public static ArrayList<String> options = new ArrayList<String>();
	public static ArrayList<String> answers = new ArrayList<String>();
	public static ArrayList<Integer> option_stat = new ArrayList<Integer>();
	public static int num_attempts=0;
	public static int num_correct=0;
	public static int num_wrong=0;
	
	public static void addQuestionContent(String content){
		content = content.trim();
		questionContent = content;
	}
	
	public synchronized static void updateQuestion(String content, int jqtype, JsonArray joptions, 
			JsonArray janswers){
		clear();
		questionContent = content;
		quesType = jqtype;
		for(int i=0;i<joptions.size();i++){
			addOption(joptions.get(i).getAsString());
		}
		for(int i=0;i<janswers.size();i++){
			addAnswer(janswers.get(i).getAsString());
		}
	}
	
	public synchronized static void clear(){
		questionContent=null;
		quesType=-1;
		options.clear();
		answers.clear();
		option_stat.clear();
		num_attempts=0;
		num_correct=0;
		num_wrong=0;
	}
	
	public static void addOption(String op){
		op = op.trim();
		if(op.length()!=0) {
			options.add(op);
			option_stat.add(0);
		}
	}
	
	public static void addAnswer(String op){
		op = op.trim();
		if(op.length()!=0) answers.add(op);
	}
	
	public static void addOptions(ArrayList<String> ops){
		for(int i=0; i<ops.size();i++) addOption(ops.get(i));
	}
	
	public static void print(){
		Utils.logv(classname, "content: "+questionContent);
		Utils.logv(classname, "Options: ");
		for(int i=0; i<options.size();i++) Utils.logv(classname, options.get(i));
		Utils.logv(classname, "Answers: ");
		for(int i=0; i<answers.size();i++) Utils.logv(classname, answers.get(i));
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
