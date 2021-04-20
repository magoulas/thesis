package theTestApp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class user {
	@Id
	private int iduser;
	private String name;
	private String surname;
	private int standardShift;
	private int nightShift;
	private int holidayNightShift;
	private int holidayShift;
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public int getStandardShift() {
		return standardShift;
	}
	public void setStandardShift(int standardShift) {
		this.standardShift = standardShift;
	}
	public int getNightShift() {
		return nightShift;
	}
	public void setNightShift(int nightShift) {
		this.nightShift = nightShift;
	}
	public int getHolidayNightShift() {
		return holidayNightShift;
	}
	public void setHolidayNightShift(int holidayNightShift) {
		this.holidayNightShift = holidayNightShift;
	}
	public int getHolidayShift() {
		return holidayShift;
	}
	public void setHolidayShift(int holidayShift) {
		this.holidayShift = holidayShift;
	}
	
	
	
	
	
	

}
