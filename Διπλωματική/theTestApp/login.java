package theTestApp;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class login {
	@Id
	private String username;
	private String password;
	private int id;
	private boolean admin;
		
	
	public String getUsername(){
		return this.username;
	}
	public String getPassword(){
		return this.password;
	}
	
	public void setUsername(String name){
		this.username=name;
	}
	public void setPassword(String pass){
		this.password=pass;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
