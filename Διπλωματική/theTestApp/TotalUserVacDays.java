package theTestApp;

import java.util.List;

public class TotalUserVacDays {
	private int userID;
	private int sum;
	private int id;
	private List<String> vacDays;  
	
	TotalUserVacDays(){
		sum=0;
		userID=-1;
		id=0;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}

	public List<String> getVacDays() {
		return vacDays;
	}

	public void setVacDays(List<String> vacDays) {
		this.vacDays = vacDays;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
