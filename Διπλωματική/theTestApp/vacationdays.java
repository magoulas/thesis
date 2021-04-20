package theTestApp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test.vacationdays")
public class vacationdays {
	@Id
	@Column(name="vacation_id")
	private int vacation_id;
	@Column(name="user_id")
	private int user_id;
	@Column(name="starting_date")
	private String starting_date;
	@Column(name="ending_date")
	private String ending_date;
	@Column(name="type")
	private String type;
	@Column(name="status")
	private int status;
	@Column(name="week_num_start")
	private int week_num_start;
	@Column(name="week_num_end")
	private int week_num_end;
	
	
	public int getVaction_id() {
		return vacation_id;
	}
	
	public void setVacation_id(int vacation_id) {
		this.vacation_id=vacation_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getStarting_date() {
		return starting_date;
	}

	public void setStarting_date(String starting_date) {
		this.starting_date = starting_date;
	}

	public String getEnding_date() {
		return ending_date;
	}

	public void setEnding_date(String ending_date) {
		this.ending_date = ending_date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWeek_num_start() {
		return week_num_start;
	}

	public void setWeek_num_start(int week_num) {
		this.week_num_start = week_num;
	}

	public int getWeek_num_end() {
		return week_num_end;
	}

	public void setWeek_num_end(int week_num_end) {
		this.week_num_end = week_num_end;
	}
	
	
	
	
	
}
