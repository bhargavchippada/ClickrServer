package datahandler;

import java.util.ArrayList;

public class UserResponse {
	public int correct=-1; // hasn't checked yet, 0 means wrong, 1 means correct
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
