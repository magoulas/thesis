package theTestApp;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.LoggerFactory;




public class ScheduleGenerator {
    static int req_employees=3;
    static int sundayShift1=18;
    static int sundayShift2=19;
    static int sundayShift3=20;

	static ShiftSchedule unsolvedEmployeeSchedule;
	
	static int FirstSundayShiftsMaxIndex=-1;
	static int SecondSundayShiftsMaxIndex=-1;
	static int ThirdSundayShiftsMaxIndex=-1;
	static int first=-1;
	static int second=-1;
	static int third=-1;
	static int FirstSundayShiftsMinIndex=Integer.MAX_VALUE;
	static int SecondSundayShiftsMinIndex=Integer.MAX_VALUE;
	static int ThirdSundayShiftsMinIndex=Integer.MAX_VALUE;
	static int firstMin=-1;
	static int secondMin=-1;
	static int thirdMin=-1;

    static Integer[] workers_range;
    static Integer[] workers_range2;
    static List<employeeMapping> mappingA;
    static List<employeeMapping> mappingB;

    static int available_workers;
    static int[] totalSundayShifts;
    static int[] totalSundayShiftsA;
    static int[] totalSundayShiftsB;

    static int[][] vacShift;


  public void schedule( int call) throws ParseException {	 
	 System.out.println("wolo");	 
	 if(call==2) {
		workers_range=workers_range2;
		available_workers=workers_range.length;
		ScheduleGenerator.totalSundayShifts=ScheduleGenerator.totalSundayShiftsB;
	}
	 
	 
	 vacShift = new int[available_workers][7];
	 vacMap(call);
	 
	 
	// brhskw tous min kai max gia ta holiday/sunday/night shifts. kai genika ta total twn shift kathe employee.  
  	StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
  	  
  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
  	Session session = factory.openSession();  
  	Transaction t = session.beginTransaction();   
  	
  	@SuppressWarnings("unchecked")
	NativeQuery<Object[]> query = session.createSQLQuery("select * from test.user");
  	query.setFirstResult(0);
  	query.setMaxResults(ScheduleGenerator.available_workers);
  	List<Object[]> rows = query.list();
  	int a =0;
  	if(rows.size()!=0) {
  		for(Object[] row:rows) {
  			totalSundayShifts[a]=Integer.parseInt(row[5].toString())+Integer.parseInt(row[6].toString());
  			a++;
  		}
  	
  	}
  	/*
  	String s1="02-06-2020";
  	String s2="02-05-2020";
  	System.out.println("str compare: "+s2.compareTo(s1));
  	*/
	
		for (int i =0;i<ScheduleGenerator.available_workers;i++) {
			if(first<ScheduleGenerator.totalSundayShifts[i]) {
				third=second;
				second=first;
				first=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMaxIndex=SecondSundayShiftsMaxIndex;
				SecondSundayShiftsMaxIndex=FirstSundayShiftsMaxIndex;
				FirstSundayShiftsMaxIndex=i;
			} else if (second<ScheduleGenerator.totalSundayShifts[i]) {
				third=second;
				second=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMaxIndex=SecondSundayShiftsMaxIndex;
				SecondSundayShiftsMaxIndex=i;
			} else if (third<ScheduleGenerator.totalSundayShifts[i]) {
				third=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMaxIndex=i;
			}
		}
		
		for (int i =0;i<ScheduleGenerator.available_workers;i++) {
			if(firstMin>ScheduleGenerator.totalSundayShifts[i]) {
				thirdMin=secondMin;
				secondMin=firstMin;
				firstMin=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMinIndex=SecondSundayShiftsMinIndex;
				SecondSundayShiftsMinIndex=FirstSundayShiftsMinIndex;
				FirstSundayShiftsMinIndex=i;
			} else if (secondMin>ScheduleGenerator.totalSundayShifts[i]) {
				thirdMin=secondMin;
				secondMin=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMinIndex=SecondSundayShiftsMinIndex;
				SecondSundayShiftsMinIndex=i;
			} else if (thirdMin>ScheduleGenerator.totalSundayShifts[i]) {
				thirdMin=ScheduleGenerator.totalSundayShifts[i];
				ThirdSundayShiftsMinIndex=i;
			}
		}
		
    try {     
    	unsolvedEmployeeSchedule = new ShiftSchedule();
    	int c = 0;
     for(int i = 0; i < 21; i++){
         unsolvedEmployeeSchedule.getShiftList().add(new Shift());
         unsolvedEmployeeSchedule.getShiftList().get(i).setShift(i);
         for(int j = 0; j <req_employees ; j++){
        	 unsolvedEmployeeSchedule.getShiftListAssignment().add(new ShiftAssignment());
        	 unsolvedEmployeeSchedule.getShiftListAssignment().get(c).setShift(new Shift());
        	 unsolvedEmployeeSchedule.getShiftListAssignment().get(c).getShift().setShift(i);

        	 c++;
         }
     }

     // pernw ena seed schedule.
     unsolvedEmployeeSchedule.getEmployees().addAll(Arrays.asList(workers_range));

     List<Integer> emptyShift= new LinkedList<Integer>();

     SolverFactory<ShiftSchedule> solverFactory = SolverFactory.createFromXmlResource("employeesScheduleSolver.xml");

     Solver<ShiftSchedule> solver = solverFactory.buildSolver();

     ShiftSchedule solvedShiftSchedule = solver.solve(unsolvedEmployeeSchedule);

     // to antigrafw se mia lista gia na to peirazw opws 8elw.
     List<finalShift> finalSchedule = new ArrayList<>();
     for (int i =0;i<63;i++ ) {
    	 finalShift finalShift = new finalShift();
    	 if((solvedShiftSchedule.getShiftListAssignment().get(i).getEmployee())==null) {
    		 finalShift.setEmployee("null");
    	 }else {
    		 finalShift.setEmployee(solvedShiftSchedule.getShiftListAssignment().get(i).getEmployee().toString());
    	 }

    	 finalShift.setShift((solvedShiftSchedule.getShiftListAssignment().get(i).getShift().getShift()));

    	 finalSchedule.add(finalShift);

     }
     int[] totalShifts = new int[ScheduleGenerator.available_workers];
     int[] improved_totalShifts = new int[ScheduleGenerator.available_workers];

     //blepw an uparxoun duplicate employeess sto idio shift.
     int x =0;
     for (int i=0;i<21;i++) {
    	 if(finalSchedule.get(x).getEmployee().compareTo(finalSchedule.get(x+1).getEmployee())==0) {
    		 finalSchedule.get(x).setEmployee("null");
    	 }
    	 if(finalSchedule.get(x+1).getEmployee().compareTo(finalSchedule.get(x+2).getEmployee())==0) {
    		 finalSchedule.get(x+1).setEmployee("null");
    	 }
    	 if(finalSchedule.get(x).getEmployee().compareTo(finalSchedule.get(x+2).getEmployee())==0) {
    		 finalSchedule.get(x).setEmployee("null");
    	 }
		    x +=3;
     }
     // metraw ta total shift ths bdomadas gia ka8e employee.
     for(int i=0;i<63;i++) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
    		 totalShifts[Integer.parseInt(finalSchedule.get(i).getEmployee())]++;
    	 }
     }


     // ftaxnw na exei o ka8e user mono 5 akribws shifts. 
     for(int i =0;i<available_workers;i++) { // worker
    	 int excessShifts=-1;
    	 int lessShifts=-1;
    	 if(totalShifts[i]>5) {
    		 excessShifts= totalShifts[i]-5;
    	 }
    	 if(totalShifts[i]<5) {
    		 lessShifts=5-totalShifts[i];
    	 }
    	 if(excessShifts!=-1) {
    		 for (int j=0;j<63;j=j+3) {
    			 if(finalSchedule.get(j).getEmployee().compareTo(Integer.toString(i))==0 && (finalSchedule.get(j+1).getEmployee().compareTo("null")!=0||finalSchedule.get(j+2).getEmployee().compareTo("null")!=0)) {
    				 finalSchedule.get(j).setEmployee("null");
    				 excessShifts--;

    			 }
    			 if(excessShifts==0) {
    				 break;
    			 }
    			 
    		 }
    		 if(excessShifts>0) {

        		 for (int j=0;j<63;j++) {
        			 if(finalSchedule.get(j).getEmployee().compareTo(Integer.toString(i))==0) {
        				 finalSchedule.get(j).setEmployee("null");
        				 excessShifts--;
        			 }

        			 if(excessShifts==0) {
        				 break;
        			 }
        			 
        		 }
    		 }
    	 }
    	 if(lessShifts!=-1) {
    		 int g=0;
    		 for (int j=0;j<7;j++) { //day
    			 boolean openSlot=true;
    			 int canEnter=-1;
    			 for(int k =g;k<(g+9);k++) { // shift in day
    				if(finalSchedule.get(k).getEmployee().compareTo("null")!=0 && i==Integer.parseInt(finalSchedule.get(k).getEmployee())) {
    					openSlot=false;
    				}
    				if(finalSchedule.get(k).getEmployee().compareTo("null")==0) {
    					canEnter=k;
    				}
    				 
    			 }
    			 if(openSlot && canEnter>-1) {
    				 finalSchedule.get(canEnter).setEmployee(Integer.toString(i));
    				 lessShifts--;
    			 }
    			 g+=9;
    			 if(lessShifts==0) {
    				 break;
    			 }
    			 
    		 }
    	 }
     }

     
     //ftiaxnw ola ta shifts na exoun toulaxiston ena emlpoyee an ftanoun oi employees.
     List<Integer> two_employee= new LinkedList<Integer>();
     List<Integer> three_employee= new LinkedList<Integer>();
     int g=0;
     for(int i=0;i<21;i++) {
    	 if(finalSchedule.get(g).getEmployee().compareTo("null")==0 && finalSchedule.get(g+1).getEmployee().compareTo("null")==0 && finalSchedule.get(g+2).getEmployee().compareTo("null")==0) {
    		 emptyShift.add(i);
    	 }
    	 if(finalSchedule.get(g).getEmployee().compareTo("null")!=0 && finalSchedule.get(g+1).getEmployee().compareTo("null")!=0 && finalSchedule.get(g+2).getEmployee().compareTo("null")!=0) {
    		 three_employee.add(i);
    	 }else if((finalSchedule.get(g).getEmployee().compareTo("null")!=0 && finalSchedule.get(g+1).getEmployee().compareTo("null")!=0) || (finalSchedule.get(g).getEmployee().compareTo("null")!=0 && finalSchedule.get(g+2).getEmployee().compareTo("null")!=0) || (finalSchedule.get(g+1).getEmployee().compareTo("null")!=0 && finalSchedule.get(g+2).getEmployee().compareTo("null")!=0)) {
    		 two_employee.add(i);

    	 }
		 g=g+3;

     }

     int out=0;
     
     
     
     //gia tous duplicate employees sto idio shift h idia hmera.
     int[] firstShift=new int[available_workers];
     int[] secondShift=new int[available_workers];
     int[] thirdShift=new int[available_workers];

     for(int i =0;i<63;i++) {
    	 if(i%9<3) {
	    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
	    		 firstShift[Integer.parseInt((finalSchedule.get(i).getEmployee()))]++;
	    	 }
    	 }else if(i%9>=3 && i%9<6) {
	    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
	    		 secondShift[Integer.parseInt((finalSchedule.get(i).getEmployee()))]++;
	    	 }
    	 }else if(i%9>=6 && i%9<=8) {
	    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
	    		 thirdShift[Integer.parseInt((finalSchedule.get(i).getEmployee()))]++;
	    	 }
    	 }
    	 if(i%9==8) {

    	     // koitazw ama sto idio shift exw duplicate employees.
    	     for(int j=0;j<available_workers;j++) {
    	    	 while(firstShift[j]>1) {
    	    		 for(int h=i-8;h<i-5;h++) {
    	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
    	    					 finalSchedule.get(h).setEmployee("null");
    	    					 firstShift[j]--;
    	    				 }
    	    			 }
    	    		 }
    	    	 }
    	    	 while(secondShift[j]>1) {
    	    		 for(int h=i-5;h<i-2;h++) {
    	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
    	    					 finalSchedule.get(h).setEmployee("null");
    	    					 secondShift[j]--;
    	    				 }
    	    			 }
    	    		 }
    	    	 }
    	    	 while(thirdShift[j]>1) {
    	    		 for(int h=i-2;h<=i;h++) {
    	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
    	    					 finalSchedule.get(h).setEmployee("null");
    	    					 thirdShift[j]--;
    	    				 }
    	    			 }
    	    		 }
    	    	 }
    	     }
    	     for(int j=0;j<available_workers;j++) {
    	    	 if(firstShift[j]==1 && secondShift[j]==1) {
    	    		 for(int h=i-5;h<i-2;h++) {
    	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
    	    					 finalSchedule.get(h).setEmployee("null");
    	    					 secondShift[j]--;
    	    				 }
    	    			 }
    	    		 }
    	    	 }
   	    	 	if(firstShift[j]==1 && thirdShift[j]==1) {
       	    		 for(int h=i-2;h<=i;h++) {
    	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
    	    					 finalSchedule.get(h).setEmployee("null");
    	    					 thirdShift[j]--;
    	    				 }
    	    			 }
    	    		 }
    	    	 }
   	    	 	if(secondShift[j]==1 && thirdShift[j]==1) {
   	    	 		for(int h=i-2;h<=i;h++) {
	   	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
	   	    				 if(Integer.parseInt(finalSchedule.get(h).getEmployee())==j) {
	   	    					 finalSchedule.get(h).setEmployee("null");
	   	    					 thirdShift[j]--;
	   	    				 }
	   	    			 }
   	    	 		}
   	    	 	}
    	     }
    	     firstShift=new int[available_workers];
    	     secondShift=new int[available_workers];
    	     thirdShift=new int[available_workers];
    	 }
     }

    
     
     int[] night = new int[available_workers];
     x=0;
     for(int i =0; i<21;i++) {
    	 if(i%3==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x).getEmployee())]++;
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+1).getEmployee())]++;
		    	}		    	
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+2).getEmployee())]++;
		    	}
		    }
    	 x+=3;
     }
     int min = Integer.MAX_VALUE;
     int max = Integer.MIN_VALUE;
     int min_worker=-1;
     int max_worker=-1;
     for(int i=0;i<available_workers;i++) {
    	 if(max<night[i]) {
    		 max=night[i];
    		 max_worker=i;
    	 }
    	 if(min>night[i]) {
    		 min=night[i];
    		 min_worker=i;
    	 }
     }

     x=0;
     int[] workers=new int[available_workers];
     int count=0;
     //ftiaxnw na exoun max 3 nuxterines bardies o ka8e worker
     while(max>3 && min<=3 && count<99) {
		 count++;
    	 int min_index=-1;
    	 int max_index=-1;
    	 boolean can_enter=false;
    	 boolean can_remove=false;
    	 int[] wolo = new int[available_workers];
    	 for(int i =0;i<7;i+=1) {
    		 for(int h=0;h<9;h++) {
    			 if(finalSchedule.get((i*9)+h).getEmployee().compareTo("null")!=0) {
    				 wolo[Integer.parseInt(finalSchedule.get((i*9)+h).getEmployee())]++;
    			 }
    		 }
    		 if((wolo[min_worker]!=0 && wolo[max_worker]==0) || (wolo[min_worker]==0 && wolo[max_worker]!=0) || (wolo[min_worker]==0 && wolo[max_worker]==0)) {
    			 wolo = new int[available_workers];
    			 continue;
    		 }
    		 for(int h =0;h<9;h++) {
    			 if(finalSchedule.get((i*9)+h).getEmployee().compareTo("null")!=0) {
    				 workers[Integer.parseInt(finalSchedule.get((i*9)+h).getEmployee())]++;
    				 if(h<6 && workers[min_worker]!=0 && !can_enter) {
    					 can_enter=true;
    					 min_index=(i*9)+h;
    				 }
    				 else if(h>=6 && workers[max_worker]!=0 && can_enter) {
    					 can_remove=true;
    					 max_index=(i*9)+h;
    				 }
    				 if(can_enter && can_remove) {
    					 break;
    				 }
    			 }
    		 }

    		 if(can_enter && can_remove) {
    			 String temp = finalSchedule.get(max_index).getEmployee();
    			 finalSchedule.get(max_index).setEmployee(finalSchedule.get(min_index).getEmployee());
    			 finalSchedule.get(min_index).setEmployee(temp);
    			 night[min_worker]++;
    			 night[max_worker]--;
    			 break;
    		 }
    		can_enter=false;
    		can_remove=false;
    	 }
    	 for( g =0;g<available_workers;g++) {
        	 night[g]=0;
         }
         x=0;
         for(int i =0; i<21;i++) {
        	 if(i%3==2) {
    		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
    		    		night[Integer.parseInt(finalSchedule.get(x).getEmployee())]++;
    		    	}
    		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
    		    		night[Integer.parseInt(finalSchedule.get(x+1).getEmployee())]++;
    		    	}		    	
    		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
    		    		night[Integer.parseInt(finalSchedule.get(x+2).getEmployee())]++;
    		    	}
    		    }
        	 x+=3;
         }

         min = Integer.MAX_VALUE;
         max = Integer.MIN_VALUE;
         min_worker=-1;
         max_worker=-1;
         for(int i=0;i<available_workers;i++) {        	 
        	 if(max<night[i]) {
        		 max=night[i];
        		 max_worker=i;
        	 }
        	 if(min>night[i]) {
        		 min=night[i];
        		 min_worker=i;
        	 }
         }
     }

     for(int m=2;m<18;m+=3) {
    	 int[] current_night = new int[available_workers];
    	 int[] next_morning = new int[available_workers];
    	 int[] swap_workers=new int[available_workers];
    	 int[] next_noon = new int[available_workers];
    	 int[] can_place_worker = new int[available_workers];
    	 int[] index_in_morning = new int[available_workers];
    	 int[] index_in_noon = new int[available_workers];
    	 
    	 int used_workers_morning=0;
    	 int used_workers_noon=0;
    	 int morning_sum=0;
    	 int noon_sum=0;
    	 
    	 if(m%3==2) { //night
    		 for(int h=0;h<3;h++) {
    			 if(finalSchedule.get((m*3)+h).getEmployee().compareTo("null")!=0)
    			 current_night[Integer.parseInt(finalSchedule.get((m*3)+h).getEmployee())]++;
    		 }
    	 }
    	 if((m+1)%3==0) {//next morning
    		 for(int h=0;h<3;h++) {
    			 if(finalSchedule.get(((m+1)*3)+h).getEmployee().compareTo("null")!=0) {
    				next_morning[Integer.parseInt(finalSchedule.get(((m+1)*3)+h).getEmployee())]++;
    			 	used_workers_morning++;
    			 	index_in_morning[Integer.parseInt(finalSchedule.get(((m+1)*3)+h).getEmployee())]=((m+1)*3)+h;
    			 }
    		 }
    	 }
    	 if((m+2)%3==1) {//next noon
    		 for(int h=0;h<3;h++) {
    			 if(finalSchedule.get(((m+2)*3)+h).getEmployee().compareTo("null")!=0) {
    				next_noon[Integer.parseInt(finalSchedule.get(((m+2)*3)+h).getEmployee())]++;
    				used_workers_noon++;
     			 	index_in_noon[Integer.parseInt(finalSchedule.get(((m+2)*3)+h).getEmployee())]=((m+2)*3)+h;
    			 }
    		 }
    	 }
    	 for(int j=0;j<available_workers;j++) {
    		 if(current_night[j]==1 && next_morning[j]==1) {
    			 swap_workers[j]++;
    			 morning_sum++; // posoi kai poioi workers prepei na allakoun.
    		 }
    		 if(current_night[j]==0 && next_noon[j]==1) {
    			 can_place_worker[j]++;
    			 noon_sum++; // posoi kai poi employees mporoun na mpoun.
    		 }
    	 }
    	 while(morning_sum>0) {
    		 for(int j=0;j<available_workers;j++) {
				 if(swap_workers[j]==1) {
					 if(noon_sum>0){ 			 
			    			 for(int h=0;h<available_workers;h++) {
			    				 if(can_place_worker[h]==1) {
			    					can_place_worker[h]=0;
			    					swap_workers[j]=0;
			    					String temp = finalSchedule.get(index_in_noon[h]).getEmployee();
			    					finalSchedule.get(index_in_noon[h]).setEmployee(finalSchedule.get(index_in_morning[j]).getEmployee());
			    					finalSchedule.get(index_in_morning[j]).setEmployee(temp);
				    				noon_sum--;
			    					morning_sum--;
			    					used_workers_morning--;
			    				 }
			    				 if(noon_sum==0) {
			    					 break;
			    				 }
			    			 }
		    			 if(morning_sum==0) {
		    				 break;
		    			 }
	    			 } else if(used_workers_noon<3) {
						 for(int h =0;h<3;h++) {
							 if(finalSchedule.get(((m+2)*3)+h).getEmployee().compareTo("null")==0) {
								 finalSchedule.get(((m+2)*3)+h).setEmployee(finalSchedule.get(index_in_morning[j]).getEmployee());
								 finalSchedule.get(index_in_morning[j]).setEmployee("null");
								 morning_sum--;
								 used_workers_noon++;
								 used_workers_morning--;
			    				 swap_workers[j]=0;
							 }
						 }
					 }
    			 }
    		 }
    	 }
     }
     
     int emptyShiftsTotal=0;
     int[] emptyShifts = new int[21];
     for(int i=0;i<63;i+=3) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")==0 && finalSchedule.get(i+1).getEmployee().compareTo("null")==0 && finalSchedule.get(i+2).getEmployee().compareTo("null")==0) {
    		 emptyShiftsTotal++;
    		 emptyShifts[i/3]++;
    	 }
     }
     
     
     // ksanametraw na sigourepsw 5 shifts per employee.
     for(int i=0;i<63;i++) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
    		 improved_totalShifts[Integer.parseInt(finalSchedule.get(i).getEmployee())]++;
    	 }
     }

     int[] shiftDelta=new int[available_workers];
     for(int i=0;i<available_workers;i++) {
    	 if(5-improved_totalShifts[i]>0) {
    		 shiftDelta[i]=(5-improved_totalShifts[i]);
    	 }else {
    		 System.out.println("panw apo 5 bardies");
    	 }
     }
     
     improved_totalShifts = new int[available_workers];
     for(int i=0;i<63;i++) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
    		 improved_totalShifts[Integer.parseInt(finalSchedule.get(i).getEmployee())]++;
    	 }
     }
     for(int i =0; i<ScheduleGenerator.available_workers;i++) {
    	 if(call==1) {
    		 System.out.println("employee: "+mappingA.get(i).getUid()+"  total shifts: "+improved_totalShifts[i]);
    	 }else if(call==2) {
    		 System.out.println("employee: "+mappingB.get(i).getUid()+"  total shifts: "+improved_totalShifts[i]);

    	 }
     }

     
     int[] prevNight = new int[available_workers];
     int[] currentDay = new int[available_workers];
     int[] nextMorning = new int[available_workers];

     for(int i =1;i<21;i++) {
    	 if(emptyShifts[i]==1) { // koitazw ta empty shifts.
    		 if(i%3==0) {// prwino adio shift
    			 int index = i*3;
    			 for(int f=index;f<index+9;f++) { // olh thn twrinh mera me adio shift
    		    	 if(finalSchedule.get(f).getEmployee().compareTo("null")!=0) {
    		    		 currentDay[Integer.parseInt(finalSchedule.get(f).getEmployee())]++;
    		    	 }
    			 }
    			 index = (i-1)*3;
    			 for(int f=index;f<index+3;f++) {// proigoumeno bradi.
    		    	 if(finalSchedule.get(f).getEmployee().compareTo("null")!=0) {
    		    		 prevNight[Integer.parseInt(finalSchedule.get(f).getEmployee())]++;
    		    	 }
    			 }
    			 for(int k=0;k<available_workers;k++) {
    				 if(shiftDelta[k]>0) {
    					 if(currentDay[k]!=1 && prevNight[k]!=1) {
    						 finalSchedule.get(i*3).setEmployee(Integer.toString(k));
    						 shiftDelta[k]--;
    						 emptyShifts[i]--;
    						 emptyShiftsTotal--;

    						 break;
    					 }
    				 }
    			 }
    		 } else if(i%3==1) { // meshmeriano adio shift.
    			 int index = (i-1)*3;
    			 for(int f=index;f<index+9;f++) { // olh thn twrinh mera me adio shift
    		    	 if(finalSchedule.get(f).getEmployee().compareTo("null")!=0) {
    		    		 currentDay[Integer.parseInt(finalSchedule.get(f).getEmployee())]++;
    		    	 }
    			 }
    			 for(int k=0;k<available_workers;k++) {
    				 if(shiftDelta[k]>0) {
    					 if(currentDay[k]!=1) {
    						 finalSchedule.get(i*3).setEmployee(Integer.toString(k));
    						 shiftDelta[k]--;
    						 emptyShifts[i]--;
    						 emptyShiftsTotal--;

    						 break;
    					 }
    				 }
    			 }
    		 }else if(i%3==2) { // braduno adio shift
    			 int index = (i-2)*3;
    			 for(int f=index;f<index+9;f++) { // olh thn twrinh mera me adio shift
    		    	 if(finalSchedule.get(f).getEmployee().compareTo("null")!=0) {
    		    		 currentDay[Integer.parseInt(finalSchedule.get(f).getEmployee())]++;
    		    	 }
    			 }
    			 index = (i+1)*3;
    			 for(int f=index;f<index+3;f++) {// proigoumeno bradi.
    		    	 if(finalSchedule.get(f).getEmployee().compareTo("null")!=0) {
    		    		 nextMorning[Integer.parseInt(finalSchedule.get(f).getEmployee())]++;
    		    	 }
    			 }
    			 for(int k=0;k<available_workers;k++) {
    				 if(shiftDelta[k]>0) {
    					 if(currentDay[k]!=1 && nextMorning[k]!=1) {
    						 finalSchedule.get(i*3).setEmployee(Integer.toString(k));
    						 shiftDelta[k]--;
    						 emptyShifts[i]--;
    						 emptyShiftsTotal--;
    						 break;
    					 }
    				 }
    			 }
    		 }
    	 }
         prevNight = new int[available_workers];
         currentDay = new int[available_workers];
         nextMorning = new int[available_workers];
     }

     System.out.println("--------------------------");
          
	 for( g =0;g<available_workers;g++) {
    	 night[g]=0;
     }
     x=0;
     for(int i =0; i<21;i++) {
    	 if(i%3==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x).getEmployee())]++;
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+1).getEmployee())]++;
		    	}		    	
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+2).getEmployee())]++;
		    	}
		    }
    	 x+=3;
     }

     
     prevNight = new int[available_workers];
     currentDay = new int[available_workers];
     nextMorning = new int[available_workers];
     for(int i=0;i<available_workers;i++) {
    	 if(shiftDelta[i]>0) {
	    	 for(int j=3;j<21;j+=3) {
	    		 int index=j*3;
	    		 for(int k=index-3;k<index;k++) {
	    			 if(finalSchedule.get(k).getEmployee().compareTo("null")!=0) {
	    				 prevNight[Integer.parseInt(finalSchedule.get(k).getEmployee())]++;
	    			 }
	    		 }
	    		 for(int k=index;k<index+9;k++) {
	    			 if(finalSchedule.get(k).getEmployee().compareTo("null")!=0) {
	    				 currentDay[Integer.parseInt(finalSchedule.get(k).getEmployee())]++;
	    			 }
	    		 }
	    		 if(j<18) {
		    		 for(int k=index+9;k<index+12;k++) {
		    			 if(finalSchedule.get(k).getEmployee().compareTo("null")!=0) {
		    				 nextMorning[Integer.parseInt(finalSchedule.get(k).getEmployee())]++;
		    			 }
		    		 }
	    		 }
	    	     if(shiftDelta[i]==0) {
	    	    	 break;
	    	     }
	    		 if(currentDay[i]==0) {
	    			 if(prevNight[i]==0) {
	    				 for(int k=index;k<index+3;k++) {
	    					 if(finalSchedule.get(k).getEmployee().compareTo("null")==0) {
	    						 finalSchedule.get(k).setEmployee(Integer.toString(i));
	    						 shiftDelta[i]--;
	    						 break;
	    					 }
	    				 }
	    			 } else if( prevNight[i]==1 && nextMorning[i]==1) {
	    				 for(int k=index+3;k<index+6;k++) {
	    					 if(finalSchedule.get(k).getEmployee().compareTo("null")==0) {
	    						 finalSchedule.get(k).setEmployee(Integer.toString(i));
	    						 shiftDelta[i]--;
	    						 break;
	    					 }
	    				 }
	    			 } else if(nextMorning[i]==0 && night[i]<3) {
	    				 for(int k=index+6;k<index+9;k++) {
	    					 if(finalSchedule.get(k).getEmployee().compareTo("null")==0) {
	    						 finalSchedule.get(k).setEmployee(Integer.toString(i));
	    						 shiftDelta[i]--;
	    						 break;
	    					 }
	    				 }
	    			 }
	    		 }
	    	 }
    	 }
	     prevNight = new int[available_workers];
	     currentDay = new int[available_workers];
	     nextMorning = new int[available_workers];
     }


     for(int i=0;i<available_workers;i++) {
    	 for(int h=0;h<7;h++) {
    		 if(vacShift[i][h]==1) {
    			 boolean found=false;
    			 for(int k=(h*9);k<(h*9)+9;k++) {
    				 if(finalSchedule.get(k).getEmployee().compareTo("null")!=0) {
    					 if(Integer.parseInt(finalSchedule.get(k).getEmployee())==i) {
    						 finalSchedule.get(k).setEmployee("null");
    						 found=true;
    						 break;
    					 }
    				 }
    			 }
    			 if(!found) {
    				 for(int a1=0;a1<63;a1++) {
    					 if(finalSchedule.get(a1).getEmployee().compareTo("null")!=0){
    						 if(Integer.parseInt(finalSchedule.get(a1).getEmployee())==i) {
        						 finalSchedule.get(a1).setEmployee("null");
        						 found=true;
        						 break;
        					 } 
    					 }
    				 }
    			 }
    		 }
    	 }
     }
     

     
     int[] adios=new int [21];
     emptyShiftsTotal=0;
     int s=0;
     for(int i=0;i<63;i+=3) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")==0 && finalSchedule.get(i+1).getEmployee().compareTo("null")==0 && finalSchedule.get(i+2).getEmployee().compareTo("null")==0) {
    		 emptyShiftsTotal++;
    		 adios[s]++;
    	 }
    	 s++;
     }
     x=0;
     for(int i =0; i<21;i++) {
    	 if(i%3==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x).getEmployee())]++;
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+1).getEmployee())]++;
		    	}		    	
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+2).getEmployee())]++;
		    	}
		    }
    	 x+=3;
     }

     
     x=0;
     for(int i=0;i<21;i++) {
		    System.out.println("Shift: "+i);
		    int first=-1;
		    int second=-1;
		    int third=-1;
		    if(call==1) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x).getEmployee());
		    		first=mappingA.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+1).getEmployee());
		    		second=mappingA.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+2).getEmployee());
		    		third=mappingA.get(b).getUid();
		    	}
		    }else if(call==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x).getEmployee());
		    		first=mappingB.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+1).getEmployee());
		    		second=mappingB.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+2).getEmployee());
		    		third=mappingB.get(b).getUid();
		    	}
		    }
		    int shift=i;
		    
		    
		    
		    
		    
		    System.out.println("	employee: "+first);
		    System.out.println("	employee: "+second);
		    System.out.println("	employee: "+third);
		    
		    x +=3;
     }
     
     int[] prevNigh = new int[available_workers];
     int[] currDay = new int[available_workers];
     int[] nextMorn = new int[available_workers];
     
    
     for(int j=0;j<21;j++) {
    	 if(adios[j]==1) {
    		 System.out.println("adio shift: "+j);
    	 }
     }
     System.out.println("--------------------------");
     
     for(int j=3;j<21;j++) {

    	 if(adios[j]==1) {
    		 boolean isMorn=false;
    		 boolean isAfternoon=false;
    		 boolean isNight=false;
    		 int day =0;
    		 int q=j;

    		 if(j%3==0) {
    			 isMorn=true;
    		 }else if(j%3==1) {
    			 isAfternoon=true;
    		 }else if(j%3==2) {
    			 isNight=true;
    		 }
    		 while(q>=3) {
    			 day++;
    			 q=q-3;
    		 }

    		 int shiftIndex=j*3;
    		 int dayIndex=day*9;
    		 for(int h=dayIndex; h<dayIndex+9; h++) {
    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
    				 currDay[Integer.parseInt(finalSchedule.get(h).getEmployee())]++;
    			 }
    		 }
    		 if(day>0) {
	    		 dayIndex=((day-1)*9)+6;
	    		 for(int h=dayIndex; h<dayIndex+3; h++) {
	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
	    				 prevNigh[Integer.parseInt(finalSchedule.get(h).getEmployee())]++;
	    			 }
	    		 }
    		 }
    		 if(day<6) {
	    		 dayIndex=(day+1)*9;
	    		 for(int h=dayIndex; h<dayIndex+3; h++) {
	    			 if(finalSchedule.get(h).getEmployee().compareTo("null")!=0) {
	    				 nextMorn[Integer.parseInt(finalSchedule.get(h).getEmployee())]++;
	    			 }
	    		 }
    		 }
    		 int[] tmpDay=new int[available_workers];
    		 int k=0;
    		 boolean swap=false;
    		 while ( k<7 && k!=day && !swap) {
    			 int m = k*9;
    			 for(int l=m;l<m+9;l++) {
        			 if(finalSchedule.get(l).getEmployee().compareTo("null")!=0) {
        				 tmpDay[Integer.parseInt(finalSchedule.get(l).getEmployee())]++;
        			 }
    			 }
    			 for(int r=0;r<available_workers;r++) {
    				 if(isAfternoon) {
    					 if(currDay[r]==0 && tmpDay[r]==1 && vacShift[r][day]==0) {
    						 int swapIndex=-1;
    						 for(int l=m;l<m+9;l++) {
    		        			 if(finalSchedule.get(l).getEmployee().compareTo("null")!=0) {
    		        				 if(Integer.parseInt(finalSchedule.get(l).getEmployee())==r) {
    		        					 swapIndex=l;
    		        					 ScheduleGenerator solo = new ScheduleGenerator();
    		        					 if(solo.soloEmployee(l, finalSchedule)) {
    		        						swapIndex=-1; 
    		        					 }
    		        					 if(swapIndex!=-1) {
        		        					 if(l>=(m+6)) {
        		        						 night[r]--;
        		        					 }
    		        						 break;
    		        					 }
    		        				 }
    		        			 }
    		    			 }
    						 if(swapIndex!=-1) {
    							Collections.swap(finalSchedule, shiftIndex, swapIndex);
    							swap=true;
    							adios[j]=0;
    							break;
    						 }
    					 }
    				 }else if(isMorn) {
    					 if(currDay[r]==0 && tmpDay[r]==1 && prevNigh[r]==0 && vacShift[r][day]==0) {
    						 int swapIndex=-1;
    						 for(int l=m;l<m+9;l++) {
    		        			 if(finalSchedule.get(l).getEmployee().compareTo("null")!=0) {
    		        				 if(Integer.parseInt(finalSchedule.get(l).getEmployee())==r) {
    		        					 swapIndex=l;
    		        					 ScheduleGenerator solo = new ScheduleGenerator();
    		        					 if(solo.soloEmployee(l, finalSchedule)) {
    		        						swapIndex=-1; 
    		        					 }
    		        					 if(swapIndex!=-1) {
        		        					 if(l>=(m+6)) {
        		        						 night[r]--;
        		        					 }
    		        						 break;
    		        					 }
    		        				 }
    		        			 }
    		    			 }
    						 if(swapIndex!=-1) {
    							 System.out.println("gemizw shift "+j +" pernw apo to day "+k);
    							 Collections.swap(finalSchedule, shiftIndex, swapIndex);
    							 swap=true;
    							 adios[j]=0;
    							 break;
    						 }
    					 }else if(k==(day-1)) {
    						 if(currDay[r]==0 && prevNigh[r]==1 && vacShift[r][day]==0) {
        						 int swapIndex=-1;
        						 for(int l=m;l<m+9;l++) {
        		        			 if(finalSchedule.get(l).getEmployee().compareTo("null")!=0) {
        		        				 if(Integer.parseInt(finalSchedule.get(l).getEmployee())==r) {
        		        					 swapIndex=l;
        		        					 ScheduleGenerator solo = new ScheduleGenerator();
        		        					 if(solo.soloEmployee(l, finalSchedule)) {
        		        						swapIndex=-1; 
        		        					 }
        		        					 if(swapIndex!=-1) {
            		        					 if(l>=(m+6)) {
            		        						 night[r]--;
            		        					 }
        		        						 break;
        		        					 }
        		        				 }
        		        			 }
        		    			 }
        						 if(swapIndex!=-1) {
        							 System.out.println("gemizw shift "+j +" pernw apo to day "+k);
        							 Collections.swap(finalSchedule, shiftIndex, swapIndex);
        							 swap=true;
        							 adios[j]=0;
        							 break;
        						 }
    						 }
    					 }
    				 }else if(isNight) {
    					 if(currDay[r]==0 && tmpDay[r]==1 && nextMorn[r]==0 && night[r]<3 && vacShift[r][day]==0) {
    						 int swapIndex=-1;
    						 for(int l=m;l<m+9;l++) {
    		        			 if(finalSchedule.get(l).getEmployee().compareTo("null")!=0) {
    		        				 if(Integer.parseInt(finalSchedule.get(l).getEmployee())==r) {
    		        					 swapIndex=l;
    		        					 ScheduleGenerator solo = new ScheduleGenerator();
    		        					 if(solo.soloEmployee(l, finalSchedule)) {
    		        						swapIndex=-1; 
    		        					 }
    		        					 if(swapIndex!=-1) {
        		        					 if(l<(m+6)) {
        		        						 night[r]++;
        		        					 }
    		        						 break;
    		        					 }
    		        				 }
    		        			 }
    		    			 }
    						 if(swapIndex!=-1) {
    							Collections.swap(finalSchedule, shiftIndex, swapIndex);
   							 	swap=true;
    							adios[j]=0;
    							break;
    						 }
    					 }
    				 }
    			 }
    			 k++;
    			 if(k==day) {
    				 k++;
    			 }
    		 }
    	     prevNigh = new int[available_workers];
    	     currDay = new int[available_workers];
    	     nextMorn = new int[available_workers];
    		 
    	 }
     }
     improved_totalShifts = new int[available_workers];
     for(int i=0;i<63;i++) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
    		 improved_totalShifts[Integer.parseInt(finalSchedule.get(i).getEmployee())]++;
    	 }
     }

     int[] totalVac = new int[available_workers];
     
     for(int i=0;i<available_workers;i++) {
    	 for(int j=0;j<7;j++) {
    		 if(vacShift[i][j]==1) {
    			 totalVac[i]++;
    		 }
    	 }
     }
     
     
     shiftDelta=new int[available_workers];
     for(int i=0;i<available_workers;i++) {
    	 if(5-improved_totalShifts[i]+totalVac[i]>0) {
    		 shiftDelta[i]=(5-improved_totalShifts[i])+totalVac[i];
    	 }
     }
     
     prevNigh = new int[available_workers];
     currDay = new int[available_workers];
     nextMorn = new int[available_workers];
     
     for(int e=0;e<available_workers;e++) {
    	 boolean telos=false;
	    	 while(shiftDelta[e]>0 && !telos) {
			     for(int i =0;i<21;i=i+3) {
			    	 if(vacShift[e][i/3]==1) {
			    		 continue;
			    	 }
			    	 int index=i*3;
			    	 for(int j = index;j<index+9;j++) {
			    		 if(finalSchedule.get(j).getEmployee().compareTo("null")!=0) {
			    			 currDay[Integer.parseInt(finalSchedule.get(j).getEmployee())]++;
			    		 }
			    	 }
			    	 if(i>3) {
				    	 index=(i-1)*3;
				    	 for(int j = index;j<index+3;j++) {
				    		 if(finalSchedule.get(j).getEmployee().compareTo("null")!=0) {
				    			 prevNigh[Integer.parseInt(finalSchedule.get(j).getEmployee())]++;
				    		 }
				    	 }
			    	 }
			    	 index=i*3;
			    	 for(int j=index+3;j<index+6;j++) {
			    		 if(finalSchedule.get(j).getEmployee().compareTo("null")==0) {
			    			 if(currDay[e]==0) {
			    				 finalSchedule.get(j).setEmployee(Integer.toString(e));
			    				 currDay[e]++;
			    				 shiftDelta[e]--;
			    				 break;
			    			 }
			    		 }
			    	 }
			    	 if(i>3) {
				    	 for(int j=index;j<index+3;j++) {
				    		 if(finalSchedule.get(j).getEmployee().compareTo("null")==0) {
				    			 if(currDay[e]==0 && prevNigh[e]==0) {
				    				 finalSchedule.get(j).setEmployee(Integer.toString(e));
				    				 currDay[e]++;
				    				 shiftDelta[e]--;
				    				 break;
				    			 }
				    		 }
				    	 }
			    	 }
			    	 if(i==18) {
			    		 telos=true;
			    	 }
			 }
	    }
    }
         
     
     
     
   /*-------------------------------------------------------------------------------------------------*/  
   	schedule_creator_Controller scC = new schedule_creator_Controller();
   	String[] week= scC.getCurrentWeek();
   	
     x=0;
     for(int i=0;i<21;i++) {
		    System.out.println("Shift: "+i);
		    int first=-1;
		    int second=-1;
		    int third=-1;
		    if(call==1) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x).getEmployee());
		    		first=mappingA.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+1).getEmployee());
		    		second=mappingA.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+2).getEmployee());
		    		third=mappingA.get(b).getUid();
		    	}
		    }else if(call==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x).getEmployee());
		    		first=mappingB.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+1).getEmployee());
		    		second=mappingB.get(b).getUid();
		    	}
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		Integer b= Integer.parseInt(finalSchedule.get(x+2).getEmployee());
		    		third=mappingB.get(b).getUid();
		    	}
		    }
		    int shift=i;
		    
		    
		    storeSchedule(shift, first, second, third, week, call,session, t);
		    
		    
		    
		    System.out.println("	employee: "+first);
		    System.out.println("	employee: "+second);
		    System.out.println("	employee: "+third);
		    
		    x +=3;
     }
     
     
     improved_totalShifts = new int[ScheduleGenerator.available_workers];
     // ksanametraw na sigourepsw 5 shifts per employee.
     for(int i=0;i<63;i++) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")!=0) {
    		 improved_totalShifts[Integer.parseInt(finalSchedule.get(i).getEmployee())]++;
    	 }
     }


     night = new int[available_workers];
     
     x=0;
     for(int i =0; i<21;i++) {
    	 if(i%3==2) {
		    	if(finalSchedule.get(x).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x).getEmployee())]++;
		    	}
		    	if(finalSchedule.get(x+1).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+1).getEmployee())]++;
		    	}		    	
		    	if(finalSchedule.get(x+2).getEmployee().compareTo("null")!=0) {
		    		night[Integer.parseInt(finalSchedule.get(x+2).getEmployee())]++;
		    	}
		    }
    	 x+=3;
     }
     for(int i =0;i<available_workers;i++) {
    	 if(call==1) {
    		 System.out.println("Number of nights for: "+mappingA.get(i).getUid()+" worker: "+night[i]);
    	 }else if(call==2) {
    		 System.out.println("Number of nights for: "+mappingB.get(i).getUid()+" worker: "+night[i]);

    	 }
     }
	 System.out.println("improved:");

     for(int i =0; i<ScheduleGenerator.available_workers;i++) {
    	 if(call==1) {
    		 System.out.println("employee: "+mappingA.get(i).getUid()+"  total shifts: "+improved_totalShifts[i]);
    	 }else if(call==2) {
    		 System.out.println("employee: "+mappingB.get(i).getUid()+"  total shifts: "+improved_totalShifts[i]);

    	 }
     }
     adios=new int [21];
     emptyShiftsTotal=0;
     int ss=0;
     for(int i=0;i<63;i+=3) {
    	 if(finalSchedule.get(i).getEmployee().compareTo("null")==0 && finalSchedule.get(i+1).getEmployee().compareTo("null")==0 && finalSchedule.get(i+2).getEmployee().compareTo("null")==0) {
    		 emptyShiftsTotal++;
    		 adios[ss]++;
    	 }
    	 ss++;
     }
     for(int j=0;j<21;j++) {
    	 if(adios[j]==1) {
    		 System.out.println("adio shift: "+j);
    	 }
     }
     System.out.println("empty shifts: "+emptyShiftsTotal);

     
	    } catch (Exception ex) {
	      LoggerFactory.getLogger(getClass()).error(ex.getMessage());
	    }
   
	t.commit();
	session.close();
  }
 

  
  
  private void storeSchedule(int shift, int first, int second, int third, String[] week , int call,Session session, Transaction t) {
  	
  	calendarevent ce_first = new calendarevent();
  	calendarevent ce_second = new calendarevent();
  	calendarevent ce_third = new calendarevent();
  	int building;
  	if(call==1) {
  		building=1;
  	}else{
  		building=2;
  	}
  	
  	int delta = shift;
  	int count=0;
  	while(delta>=3) {
  		delta=delta-3;
  		count++;
  	}
  		String[] parts = week[count].split("-");
		Integer max =(Integer) session.createQuery("select max(idCalendarEvent) from calendarevent").getSingleResult();
		if(max==null) {
			max=0;
		}
		int sshift=(shift%3)+1;
		if(first!=-1) {
			max+=1;
			ce_first=data(max,Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),first,building,sshift);
			session.save(ce_first);
		}
		if(second!=-1) {
			max+=1;
			ce_second=data(max,Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),second,building,sshift);
			session.save(ce_second);
		}
		if(third!=-1) {
			max+=1;
			ce_third=data(max,Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),third,building,sshift);
			session.save(ce_third);
		}

	
  }

  public calendarevent data(int max,int day, int month, int year,int iduser,int building, int shift) {
	  calendarevent ce = new calendarevent();
		ce.setIdCalendarEvent(max);
		ce.setDay(day);
		ce.setMonth(month);
		ce.setYear(year);
		ce.setIdUser(iduser);
		ce.setBuilding(building);
		ce.setShift(shift);
	  return ce;
  }


public void vacMap(int call) throws ParseException {
	  if(call==1) {
		  for(int i=0; i<available_workers;i++) {
			  int uid=mappingA.get(i).getUid();
			  int index=0;
			  boolean found=false;
			  while(index<schedule_creator_Controller.tuvd.size()) {
				  if(schedule_creator_Controller.tuvd.get(index).getUserID()==uid) {
					  found=true;
					  break;
				  }
				  index++;
			  }
			  if(found) {
				  List<String> vdays = schedule_creator_Controller.tuvd.get(index).getVacDays();
				  Calendar c = Calendar.getInstance();
				  SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				  for(String s: vdays) {
					  Date d =format.parse(s);
					  c.setTime(d);
					  c.setFirstDayOfWeek(Calendar.MONDAY);
					  int pos=c.get(Calendar.DAY_OF_WEEK)-2;
					  if(pos<0) {
						  pos=pos+7;
					  }
					  vacShift[i][pos]++;
				  }
			  }
		  }
		  for(int i =0; i < available_workers;i++) {
			  System.out.println("index: "+i);
			  System.out.println("uid: "+mappingA.get(i).getUid());
			  for(int h=0;h<7;h++) {
				  System.out.print(vacShift[i][h]+" ");
			  }
			  System.out.println(" ");
		  }
	  }
	  if(call==2) {
		  for(int i=0; i<available_workers;i++) {
			  int uid=mappingB.get(i).getUid();
			  int index=0;
			  boolean found=false;
			  while(index<schedule_creator_Controller.tuvd.size()) {
				  if(schedule_creator_Controller.tuvd.get(index).getUserID()==uid) {
					  found=true;
					  break;
				  }
				  index++;
			  }
			  if(found) {
				  List<String> vdays = schedule_creator_Controller.tuvd.get(index).getVacDays();
				  Calendar c = Calendar.getInstance();
				  SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				  for(String s: vdays) {
					  Date d =format.parse(s);
					  c.setTime(d);
					  c.setFirstDayOfWeek(Calendar.MONDAY);
					  int pos=c.get(Calendar.DAY_OF_WEEK)-2;
					  if(pos<0) {
						  pos=pos+7;
					  }
					  vacShift[i][pos]++;
				  }
			  }
		  }
	  }
  }

	public boolean soloEmployee(int employee,  List<finalShift> finalSchedule) {
		int count=0;
		if(employee%9<3) {
			if(employee%3==0) {
				if(finalSchedule.get(employee+1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==1) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==2) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee-2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			}
		}else if(employee%9>=3 && employee%9<6) {
			if(employee%3==0) {
				if(finalSchedule.get(employee+1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==1) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==2) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee-2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			}
		}else if(employee%9>=6) {
			if(employee%3==0) {
				if(finalSchedule.get(employee+1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==1) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee+2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			} else if(employee%3==2) {
				if(finalSchedule.get(employee-1).getEmployee().compareTo("null")!=0) {
					count++;
				}
				if(finalSchedule.get(employee-2).getEmployee().compareTo("null")!=0) {
					count++;
				}
			}
		}
		if(count!=0) {
			return false;
		} else {
			return true;
		}
	}
}







//{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}
//{0,1,2,3,4,5,6,7,8,9,10,11,12}