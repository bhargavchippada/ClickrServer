package datahandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import support.Utils;

public class ClassRoom {
	static String classname = "ClassRoom";
	
	public static String clsnm="RnD Class";
	public static int userslist = 0; //0 means users file, 1 means anyone can join, -1 means none
	public static boolean serveronline = false;
	
	//<username, userprofile> pair
	public static ConcurrentHashMap<String, UserProfile> users_map = new ConcurrentHashMap<String, UserProfile>();
	//<username, userresponse> pair
	public static ConcurrentHashMap<String, UserResponse> users_responsemap = new ConcurrentHashMap<String, UserResponse>();

	public static void addResponse(String uid, int correct, ArrayList<String> answers){
		UserResponse ur = users_responsemap.get(uid);
		if(ur==null){
			ur = new UserResponse();
			ur.correct = correct;
			ur.answers = answers;
			int pos;
			for(int i=0; i<answers.size(); i++){
				pos=Integer.parseInt(answers.get(i))-1;
				Question.updateOptionStats(pos);
			}
			Question.incrementNumAttempts();
			if(correct==1){
				Question.incrementNumCorrects();
			}else{
				Question.incrementNumWrongs();
			}
			users_responsemap.put(uid, ur);
		}
	}

	public static void print(){
		synchronized (users_responsemap) {
			Iterator it = users_responsemap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				String key = (String)pairs.getKey();
				UserProfile user = (UserProfile)pairs.getValue();
				user.print();
			}
		}
	}
	
	public static void printClass(){
		synchronized (users_map) {
			Iterator it = users_map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				String key = (String)pairs.getKey();
				UserProfile user = (UserProfile)pairs.getValue();
				user.print();
			}
		}
	}

	public static void printStats(){
		synchronized (Question.class) {
			System.out.println("General Stats:");
			System.out.println("Number of attempts: "+Question.num_attempts);
			System.out.println("Number of corrects: "+Question.num_correct);
			System.out.println("Number of wrongs: "+Question.num_wrong);

			System.out.println("Option-wise Stats:");
			for(int i=0; i<Question.options.size(); i++){
				System.out.println((i+1)+": "+Question.options.get(i)+"("+Question.option_stat.get(i)+")");
			}
		}

		synchronized (users_responsemap) {
			Iterator it = users_responsemap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				String key = (String)pairs.getKey();
				UserResponse user = (UserResponse)pairs.getValue();
				System.out.println(key+" "+user.responseString());
			}
		}
	}
	
	public synchronized static void clear(){
		Utils.logv(classname, "ClassRoom is cleared");
		users_map.clear();
		users_responsemap.clear();
	}
}
