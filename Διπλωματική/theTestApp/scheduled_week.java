package theTestApp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test.scheduled_week")
public class scheduled_week {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="scheduled_week")
	private int scheduled_week;

	public int getScheduled_week() {
		return scheduled_week;
	}

	public void setScheduled_week(int scheduled_week) {
		this.scheduled_week = scheduled_week;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
