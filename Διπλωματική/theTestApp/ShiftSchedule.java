package theTestApp;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class ShiftSchedule {
	private List<Integer> employees;
	//private List<Integer> shifts;
	private List<Shift> shiftList;
	private List<ShiftAssignment> shiftListAssignment;
	private HardSoftScore score;
	
	
	public ShiftSchedule() {
		employees = new ArrayList<>();
		//setShifts(new ArrayList<>());

		shiftList = new ArrayList<>();
		shiftListAssignment = new ArrayList<>();
	}

	@ValueRangeProvider(id = "availableEmployees")
	@ProblemFactCollectionProperty
	public List<Integer> getEmployees() {
		return employees;
	}


	public void setEmployees(List<Integer> employees) {
		this.employees = employees;
	}


	@PlanningScore
	public HardSoftScore getScore() {
		return score;
	}


	public void setScore(HardSoftScore score) {
		this.score = score;
	}
	
	@ProblemFactCollectionProperty
	public List<Shift> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<Shift> shiftList) {
		this.shiftList = shiftList;
	}

	@PlanningEntityCollectionProperty
	public List<ShiftAssignment> getShiftListAssignment() {
		return shiftListAssignment;
	}

	public void setShiftListAssignment(List<ShiftAssignment> shiftListAssignment) {
		this.shiftListAssignment = shiftListAssignment;
	}
	

}
