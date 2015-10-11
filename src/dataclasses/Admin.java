package dataclasses;

public class Admin {
	
	private String username;
	private Integer adminid;
	
	public String classname = null;
	public Integer classid = null;
	public String classtype = "classonly";
	public Boolean serverstate = false;
	
	Admin(String name, Integer id){
		username = name;
		adminid = id;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Integer getAdminId(){
		return adminid;
	}
	
	public void setClassSettings(String classname, Integer classid, String classtype, Boolean serverstate){
		this.classname = classname;
		this.classid = classid;
		this.classtype = classtype;
		this.serverstate = serverstate;
	}

}
