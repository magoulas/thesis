package theTestApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class ScoreCalculator implements EasyScoreCalculator<ShiftSchedule> {

	@Override
	public Score calculateScore(ShiftSchedule shiftSchedule) {
		int hardScore = 0;
		int softScore = 0;
		//HashSet<String> occupiedEmployees = new HashSet<>();
		
		int[][] occupiedEmployeess = new int[ScheduleGenerator.available_workers][3]; // employee  , shift;

		for(int s = 0; s<3;s++) {
			for(int e =0;e<ScheduleGenerator.available_workers;e++) {
				occupiedEmployeess[e][s]=0;
			}	
		}

		int[] totalDailyShifts = new int[ScheduleGenerator.available_workers];
		for(int i =0; i<ScheduleGenerator.available_workers;i++) {
			totalDailyShifts[i]=0;
		}

		int[] totalWeeklyShifts = new int[ScheduleGenerator.available_workers];
		for(int i =0; i<ScheduleGenerator.available_workers;i++) {
			totalWeeklyShifts[i]=0;
		}

		int[][] nightShifts = new int[21][ScheduleGenerator.available_workers];
		
		for (ShiftAssignment shift : shiftSchedule.getShiftListAssignment()) {
			if(shift.getEmployee()!=null ) {

				int s = shift.getShift().getShift();
				int originalShift= s;
				int nightShift=-1;
				if((s % 3)==0 ) {
					nightShift=s;
				}
				while(s>2) {
					s += -3;
				}

				int e = shift.getEmployee();

				if(occupiedEmployeess[e][s]>=1) {
					hardScore += -5;
				} else {
					occupiedEmployeess[e][s]++;
					if(nightShift > -1) {
						nightShifts[nightShift][e]++;
					}
					totalDailyShifts[e]++;
					totalWeeklyShifts[e]++;
				}

				if(totalDailyShifts[e]>1) {
					hardScore += -100;
				} else if(totalDailyShifts[e]==1) {
					hardScore += 1;
				}

				if(totalWeeklyShifts[e]==5) {
					hardScore += 100;
				}else if(totalWeeklyShifts[e]>5) {
					hardScore += -100;
				}else if(totalWeeklyShifts[e]<5) {
					hardScore += -2;
				}
				

				if(s==2) {
					for(int t = 0; t<3;t++) {
						for(int i =0;i<ScheduleGenerator.available_workers;i++) {
							occupiedEmployeess[i][t]=0;
						}
					}
					for(int i =0; i<ScheduleGenerator.available_workers;i++) {
						totalDailyShifts[i]=0;
					}
				}

				if(s==ScheduleGenerator.sundayShift1|| s==ScheduleGenerator.sundayShift2|| s==ScheduleGenerator.sundayShift3) {
					if(e==ScheduleGenerator.FirstSundayShiftsMaxIndex) {
						hardScore += -5;
					}
					if(e==ScheduleGenerator.SecondSundayShiftsMaxIndex) {
						hardScore += -4;
					}
					if(e==ScheduleGenerator.ThirdSundayShiftsMaxIndex) {
						hardScore += -3;
					}
					if(e==ScheduleGenerator.FirstSundayShiftsMinIndex) {
						hardScore += -5;
					}
					if(e==ScheduleGenerator.SecondSundayShiftsMinIndex) {
						hardScore += -4;
					}
					if(e==ScheduleGenerator.ThirdSundayShiftsMinIndex) {
						hardScore += -3;
					}

				}
				int nextShift= nightShift++;
				if(originalShift == nextShift) {
					if(nightShift > -1) {
						if(nightShifts[nightShift][e]>=1) {
							hardScore += -4;
						}
					}
				}

				
				int sum=0;
				for (int h =0; h<21;h++) {
					sum+=nightShifts[h][e];
				}
				if(sum>3) {
					hardScore += -5;
				}

			} else {
				int s = shift.getShift().getShift();
				while(s>2) {
					s += -3;
				}
				int hasEmployee=0;
				for(int i = 0; i<ScheduleGenerator.available_workers; i++) {
					if(occupiedEmployeess[i][s]==1) {
						hasEmployee++;
					}
				}
				if(hasEmployee>0) {
					hardScore += -2;
				} else {
					hardScore += -100;
				}
			}
		}
		 

		return HardSoftScore.of(hardScore, softScore);
	}

}
/*
if(occupiedEmployees.contains(employeeAssigned) ) {
	hardScore += -1;
} else {
	occupiedEmployees.add(employeeAssigned);
}
*/