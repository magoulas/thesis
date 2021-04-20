package theTestApp;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "test.calendarevent")
public class calendarevent {
	@Id
	private int idCalendarEvent;
	private int day;
	private int month;
	private int year;
	private int shift;
	private int idUser;
	private int building;
	
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getShift() {
		return shift;
	}
	public void setShift(int shift) {
		this.shift = shift;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public int getBuilding() {
		return building;
	}
	public void setBuilding(int building) {
		this.building = building;
	}
	public int getIdCalendarEvent() {
		return idCalendarEvent;
	}
	public void setIdCalendarEvent(int idCalendarEvent) {
		this.idCalendarEvent = idCalendarEvent;
	}

}
