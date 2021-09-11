package me.davethecamper.cashshop.api.info;

public class PlayerInfo {
	
	public PlayerInfo(String name, String last_name, String email) {
		this.name = name;
		this.last_name = last_name;
		this.email = email;
	}
	
	
	private String name, last_name, email;
	

	public String getName() {
		return name;
	}

	public String getLastName() {
		return last_name;
	}

	public String getEmail() {
		return email;
	}

}
