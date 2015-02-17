package datahandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassRoom {
	public static String clsnm;
	public static ConcurrentHashMap<String, UserProfile> users_map = new ConcurrentHashMap<String, UserProfile>();
	public static ConcurrentHashMap<String, UserResponse> users_responsemap = new ConcurrentHashMap<String, UserResponse>();
	
	
	public static void addUser(UserProfile up){
		users_map.put(up.rollnumber, up);
	}
	
	public static void addResponse(String uid, int correct, ArrayList<String> answer){
		UserResponse ur = users_responsemap.get(uid);
		if(ur==null){
			ur = new UserResponse();
			ur.correct = correct;
			ur.answer = answer;
			int pos;
			for(int i=0; i<answer.size(); i++){
				pos=Integer.parseInt(answer.get(i))-1;
				Question.option_stat.set(pos,Question.option_stat.get(pos)+1);
			}
			Question.num_attempts++;
			if(correct==1){
				Question.num_correct++;
			}else{
				Question.num_wrong++;
			}
			users_responsemap.put(uid, ur);
		}
	}
	
	public static void print(){
		Iterator it = users_responsemap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String key = (String)pairs.getKey();
	        UserProfile user = (UserProfile)pairs.getValue();
	        user.print();
	    }
	}
	
	public static void printStats(){
	    System.out.println("General Stats:");
		System.out.println("Number of attempts: "+Question.num_attempts);
		System.out.println("Number of corrects: "+Question.num_correct);
		System.out.println("Number of wrongs: "+Question.num_wrong);
		
		System.out.println("Option-wise Stats:");
		for(int i=0; i<Question.options.size(); i++){
			System.out.println((i+1)+": "+Question.options.get(i)+"("+Question.option_stat.get(i)+")");
		}
		
		Iterator it = users_responsemap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String key = (String)pairs.getKey();
	        UserResponse user = (UserResponse)pairs.getValue();
	        System.out.println(key+" "+user.responseString());
	    }
	}
}
