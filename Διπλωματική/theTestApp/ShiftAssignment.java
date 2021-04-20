package theTestApp;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class ShiftAssignment {
	
	private Integer employee;
	private Shift shift;
	

	@PlanningVariable(valueRangeProviderRefs = {"availableEmployees"})
	public Integer getEmployee() {
		return employee;
	}
	
	public void setEmployee(Integer employee) {
		this.employee = employee;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}
}
