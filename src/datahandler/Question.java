package datahandler;

import java.util.ArrayList;

import support.Utils;

public class Question{
	
	public static String classname = "Question";
	
	public static String questionContent="";
	public static int quesType=0;
	public static ArrayList<String> options = new ArrayList<String>();
	public static ArrayList<String> answer = new ArrayList<String>();
	public static ArrayList<Integer> option_stat = new ArrayList<Integer>();
	public static int num_attempts=0;
	public static int num_correct=0;
	public static int num_wrong=0;
	
	public static void addQuestionContent(String content){
		content = content.trim();
		questionContent = content;
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
		if(op.length()!=0) answer.add(op);
	}
	
	public static void addOptions(ArrayList<String> ops){
		for(int i=0; i<ops.size();i++) addOption(ops.get(i));
	}
	
	public static void print(){
		Utils.logi(classname, questionContent);
		for(int i=0; i<options.size();i++) Utils.logi(classname, options.get(i));
		for(int i=0; i<answer.size();i++) Utils.logi(classname, answer.get(i));
	}
}
