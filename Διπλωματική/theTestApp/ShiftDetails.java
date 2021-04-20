package theTestApp;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class ShiftDetails {
	@Id
	private int id;
	private String date;
	private char building;
	private int shift;
	private boolean completed;
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public char getBuilding() {
		return building;
	}
	public void setBuilding(char building) {
		this.building = building;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getShift() {
		return shift;
	}
	public void setShift(int shift) {
		this.shift = shift;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
