package datahandler;

import java.util.ArrayList;

public class UserResponse {
	public int correct;
	public ArrayList<String> answers = new ArrayList<String>();
	
	public String responseString(){
		String output;
		if(correct==1) output="correct: ";
		else output="wrong: ";
		for(int i=0; i<answers.size();i++){
			output+=answers.get(i)+",";
		}
		return output;
	}
}
