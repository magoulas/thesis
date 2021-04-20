package theTestApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.google.common.collect.Iterables;

public class schedule_creator_Controller extends SelectorComposer<Component> {
    private static List<Integer> S1 = new ArrayList<>();
    private static List<Integer> S2 = new ArrayList<>();
    private static List<Integer> dif = new ArrayList<>();
	public static List<TotalUserVacDays> tuvd = new ArrayList<>();


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static private String admin;
	static private int userID;
	private ListModel<UserList> usersModel;
	public static String[] building_assignment = new String[13];
	
	@Wire Window win;
	@Wire Combobox combo0;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		Execution execution = Executions.getCurrent();
		userID=Integer.parseInt(execution.getParameter("idUser"));
		admin=execution.getParameter("admin");
		boolean isAdmin;
		if(admin.equals("1")) {
			isAdmin=true;
		}else {
			isAdmin=false;
		}
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	NativeQuery<Object[]> query;
    	
    	query = session.createSQLQuery("select * from test.user");
    	List<Object[]> rows = query.list();
    	int uid=0;
    	String name=null;
    	String surname=null;
    	if(rows.size()!=0) {
    		for(Object[] row:rows) {
    			uid=Integer.parseInt(row[0].toString());
    			name=row[1].toString();
    			surname=row[2].toString();
    			
            	UserList uList = new UserList();
            	uList.setUid(uid);
            	uList.setName(name);
            	uList.setSurname(surname);
            	((ListModelList<UserList>)usersModel).add(uList);
    		}
    		

    	}
    	
    	t.commit();
    	session.close();
    	}
	
	
	public schedule_creator_Controller() {
		usersModel = new ListModelList<UserList>();
		((ListModelList<UserList>)usersModel).setMultiple(true);
	}
	
	public ListModel<UserList> getUsersModel(){
		return usersModel;
	}
	
	//A= Knwsos
	//B= Boutes
	
	@Listen("onSelect=combobox")
	public void building(SelectEvent event) {
		String[] first=event.getSelectedItems().toString().replace(">", "").replace("]", "").split("#");
		String[] building_employee = first[1].split("\\+");
		building_assignment[Integer.parseInt(building_employee[1])]=building_employee[0];
	}
	
	@Listen("onClick=#submit")
	public void assignments() throws ParseException {
		int i=0;
		int len=0;
		while(i<13 && building_assignment[i]!=null ) {
			if(building_assignment[i].compareTo("B")==0) {
				len++;
			}
			i++;
		}
		if(i==13) {
		    StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
		  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
		  	  
		  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
		  	Session session = factory.openSession();  
		  	Transaction t = session.beginTransaction();		 
		  	
		  	scheduled_week wek = session.get(scheduled_week.class, 0);
		  	int a = wek.getScheduled_week();
		  	a++;
		  	wek.setScheduled_week(a);
		  	session.save(wek);

			
			ScheduleGenerator.mappingA= new ArrayList<>();
			ScheduleGenerator.mappingB= new ArrayList<>();

			Messagebox.show("Roster submited.");
			ScheduleGenerator.workers_range2= new Integer[len];
			ScheduleGenerator.workers_range=new Integer[(13-len)];
			int k=0;
			int o=0;
			for(int j =0;j<13;j++) {
				if(building_assignment[j].compareTo("B")==0) {
					employeeMapping emp = new employeeMapping();
					emp.setIndex(k);
					emp.setUid(j);
					ScheduleGenerator.mappingB.add(emp);
					ScheduleGenerator.workers_range2[k]=k;
					k++;
				}else {
					employeeMapping emp = new employeeMapping();
					emp.setIndex(o);
					emp.setUid(j);
					ScheduleGenerator.mappingA.add(emp);
					ScheduleGenerator.workers_range[o]=o;
					o++;
				}
			}
		ScheduleGenerator.available_workers=ScheduleGenerator.workers_range.length;
		ScheduleGenerator.totalSundayShiftsA= new int[ScheduleGenerator.mappingA.size()];
		ScheduleGenerator.totalSundayShiftsB= new int[ScheduleGenerator.mappingB.size()];

		for(employeeMapping emp: ScheduleGenerator.mappingA) {
			int uid = emp.getUid();
			user u = session.get(user.class, uid);
			ScheduleGenerator.totalSundayShiftsA[emp.getIndex()]=u.getHolidayNightShift()+u.getHolidayShift();
		}
		for(employeeMapping emp: ScheduleGenerator.mappingB) {
			int uid = emp.getUid();
			user u = session.get(user.class, uid);
			ScheduleGenerator.totalSundayShiftsB[emp.getIndex()]=u.getHolidayNightShift()+u.getHolidayShift();
		}
		ScheduleGenerator.totalSundayShifts=ScheduleGenerator.totalSundayShiftsA;
		t.commit();
		session.close();
		totalVacDays();
		ScheduleGenerator mj = new ScheduleGenerator();
		mj.schedule(1);
		mj.schedule(2);
		}else {
			Messagebox.show("Not all employees have been assigned. Roster not submited.");
		}
	}
	
	@Listen("onClick=#auto_generate")
	public void auto_assignments() throws ParseException {
		Messagebox.show("Roster submited.");
	    StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
	  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
	  	  
	  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
	  	Session session = factory.openSession();  
	  	Transaction t = session.beginTransaction();	
	  	
	  	scheduled_week wek = session.get(scheduled_week.class, 0);
	  	int a = wek.getScheduled_week();
	  	a++;
	  	wek.setScheduled_week(a);
	  	session.save(wek);

		roster_generator();
		
		ScheduleGenerator.totalSundayShiftsA= new int[ScheduleGenerator.mappingA.size()];
		ScheduleGenerator.totalSundayShiftsB= new int[ScheduleGenerator.mappingB.size()];

		for(employeeMapping emp: ScheduleGenerator.mappingA) {
			int uid = emp.getUid();
			user u = session.get(user.class, uid);
			ScheduleGenerator.totalSundayShiftsA[emp.getIndex()]=u.getHolidayNightShift()+u.getHolidayShift();
		}
		for(employeeMapping emp: ScheduleGenerator.mappingB) {
			int uid = emp.getUid();
			user u = session.get(user.class, uid);
			ScheduleGenerator.totalSundayShiftsB[emp.getIndex()]=u.getHolidayNightShift()+u.getHolidayShift();
		}
	  	
		t.commit();
		session.close();
		ScheduleGenerator.available_workers=ScheduleGenerator.workers_range.length;
		ScheduleGenerator.totalSundayShifts=ScheduleGenerator.totalSundayShiftsA;
		ScheduleGenerator mj = new ScheduleGenerator();
		mj.schedule(1);
		mj.schedule(2);
	}
	
	
	public void totalVacDays() {
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
	  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
	  	  
	  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
	  	Session session = factory.openSession();  
	  	Transaction t = session.beginTransaction();
		String[] days= getCurrentWeek();

	  	String weekStart = days[0];
	  	
		WeekNumber wn=new WeekNumber();
		int current_week=-1;
		try {
			current_week = wn.getWeekNum(weekStart.replace("-", ""));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		TypedQuery<vacationdays> query1 = session.createQuery("SELECT a FROM vacationdays a "
	            + "WHERE  a.status=1 and a.week_num_start<=?2 and a.week_num_end>=?3");
	    //query1.setParameter(1, moyear); a.starting_date LIKE CONCAT('%',?1,'%') and
	    query1.setParameter(2, current_week);
	    query1.setParameter(3, current_week);

	    List<vacationdays> vacDays = query1.getResultList();
	   
		t.commit();
		session.close();
		
		List<UserVacDays> uvd = getVacDays(days,vacDays);
		int id =0;
		for(int d=0;d<uvd.size();d++) {
			System.out.println("---------------");
			System.out.println(uvd.get(d).getUserID());
			TotalUserVacDays user = new TotalUserVacDays();
			user.setUserID(uvd.get(d).getUserID());
			user.setId(id);
			user.setVacDays(uvd.get(d).getVacDays());
			user.setSum(uvd.get(d).getVacDays().size());
			id++;
			tuvd.add(user);
			for(int f=0;f<uvd.get(d).getVacDays().size();f++) {
				System.out.println(uvd.get(d).getVacDays().get(f));
			}
		}
	}
	
	public void roster_generator() {
	  	
		totalVacDays();
		
		Map<String, Integer> lookup = new HashMap<>();
		int[] S = new int[tuvd.size()];
		for(int g=0;g<tuvd.size();g++) {
			S[g]=tuvd.get(g).getSum();
		}

		int minDifference = minPartition(S,S.length-1,0,0,lookup);
		System.out.println("min dif: "+minDifference);
		int difIndex=-1;
		for(int h=0;h<S1.size();h++) {
			if(dif.get(h)==minDifference) {
				difIndex=h;
				break;
			}
		}
		int neededSum=S1.get(difIndex);
			
		int maxx = -1;
		for(int i =0;i<tuvd.size();i++) {
			if(maxx<tuvd.get(i).getUserID()) {
				maxx=tuvd.get(i).getUserID();
			}
		}
		
		Set<Integer> users = new HashSet<>();
		Set<Integer> totalUsers = new HashSet<>();
		for(int i=0; i<13;i++) {
			totalUsers.add(i);
		}
		for(TotalUserVacDays h:tuvd) {
			users.add(h.getUserID());
		}
		Integer[] arr = new Integer[maxx+1];
		Integer[] vacPerUser = new Integer[maxx+1];
		for(TotalUserVacDays h:tuvd) {
			vacPerUser[h.getUserID()]=h.getSum();
		}
		for(int i =0;i<(maxx+1);i++) {
			if(users.contains(i)) {
				arr[i]=vacPerUser[i];
			}else {
				arr[i]=0;
			}
		}
		Possible_Worker_set.printAllSubsets(arr, maxx+1, neededSum);
		
		System.out.println("------------");
		ArrayList<Integer> firstSet = null;
		if(!Possible_Worker_set.possible_set.isEmpty()) {
			firstSet=Possible_Worker_set.possible_set.get(0);
		}
		Set<Integer> firstUsers = new HashSet<>();
		Set<Integer> secondUsers = new HashSet<>();
		if(!(firstSet==null)) {
			for(int x:firstSet) {
				int y=Arrays.asList(arr).indexOf(x);
				firstUsers.add(y);
				totalUsers.remove(y);
				users.remove(y);
			}

			secondUsers.addAll(users);
			totalUsers.removeAll(secondUsers);
		}
		int remaining_users = totalUsers.size();
		if(remaining_users>=0) {
			Iterable<Integer> tmp = Iterables.limit(totalUsers,remaining_users/2);
			for(Integer x:tmp) {
				firstUsers.add(x);
			}
		}
		totalUsers.removeAll(firstUsers);
		remaining_users = totalUsers.size();
		if(remaining_users>=0) {
			Iterable<Integer> tmp = Iterables.limit(totalUsers,remaining_users);
			for(Integer x:tmp) {
				secondUsers.add(x);
			}
		}
		
		ScheduleGenerator.workers_range=new Integer[firstUsers.size()];
		firstUsers.toArray(ScheduleGenerator.workers_range);
		ScheduleGenerator.mappingA=new ArrayList<>();
		ScheduleGenerator.mappingB=new ArrayList<>();
		for(int j=0;j<ScheduleGenerator.workers_range.length;j++) {
			employeeMapping emp= new employeeMapping();
			emp.setIndex(j);
			emp.setUid(ScheduleGenerator.workers_range[j]);
			ScheduleGenerator.mappingA.add(emp);
			ScheduleGenerator.workers_range[j]=j;
		}
		ScheduleGenerator.workers_range2=new Integer[secondUsers.size()];
		secondUsers.toArray(ScheduleGenerator.workers_range2);
		for(int j=0;j<ScheduleGenerator.workers_range2.length;j++) {
			employeeMapping emp= new employeeMapping();
			emp.setIndex(j);
			emp.setUid(ScheduleGenerator.workers_range2[j]);
			ScheduleGenerator.mappingB.add(emp);
			ScheduleGenerator.workers_range2[j]=j;
		}
		
	}
	
	  public String[] getCurrentWeek() {
		    Calendar now = Calendar.getInstance();
		    StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
		  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
		  	  
		  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
		  	Session session = factory.openSession();  
		  	Transaction t = session.beginTransaction();		 
		  	
		  	scheduled_week wek = session.get(scheduled_week.class, 0);
		  	int k = wek.getScheduled_week();
			now.set(Calendar.WEEK_OF_YEAR, k);
		    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		    String[] days = new String[7];
		    int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
		    now.add(Calendar.DAY_OF_MONTH, delta );
		    for (int i = 0; i < 7; i++)
		    {
		        days[i] = format.format(now.getTime());
		        now.add(Calendar.DAY_OF_MONTH, 1);
		    }
			
		    for(int i =0; i<days.length;i++) {
			  days[i]=days[i].replace("/", "-");
		    }
		    
	    	t.commit();
	    	session.close();
		return days;
		  
	  }

	  public List<UserVacDays> getVacDays(String[] currWeek, List<vacationdays> vacDays) {
		List<UserVacDays> uvd = new ArrayList();
		  for(int j =0; j<vacDays.size();j++) {
			  UserVacDays x = new UserVacDays();
			  String startDate = vacDays.get(j).getStarting_date();
			  String endDate = vacDays.get(j).getEnding_date();
			  
			  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			  
			  LocalDate start = LocalDate.parse(startDate,formatter);
			  LocalDate end = LocalDate.parse(endDate,formatter);
			   long numOfDaysBetween = ChronoUnit.DAYS.between(start, end)+1; 
			   List<LocalDate> betweenDays = IntStream.iterate(0, i -> i + 1)
			      .limit(numOfDaysBetween)
			      .mapToObj(i -> start.plusDays(i))
			      .collect(Collectors.toList());
			   
			   List<String> meres = new ArrayList();;
			   
			   int startIndex=-1;
			   int endIndex=-1;
			   String arxh=startDate;
			   String telos=endDate;
			   if(start.compareTo(LocalDate.parse(currWeek[0],formatter))<=0) {
				   arxh=currWeek[0];
			   }else if(start.compareTo(LocalDate.parse(currWeek[0],formatter))>0 && start.compareTo(LocalDate.parse(currWeek[6],formatter))<=0) {
				   arxh=startDate;
			   }
			   if(end.compareTo(LocalDate.parse(currWeek[6],formatter))<=0) {
				   telos=endDate;
			   }else if(end.compareTo(LocalDate.parse(currWeek[6],formatter))>0) {
				   telos=currWeek[6];
			   }
			   for(int k =0;k<betweenDays.size();k++) {
				   if(betweenDays.get(k).format(formatter).toString().compareTo(arxh)==0) {
					   startIndex=k;
				   }
				   if(betweenDays.get(k).format(formatter).toString().compareTo(telos)==0) {
					   endIndex=k;
					   break;
				   }
			   }
			   int h=0;
			   for(int l=startIndex; l<=endIndex; l++) {
				   meres.add(betweenDays.get(l).format(formatter).toString());
				   h++;
			   }
			   int i =0;
			   while(i<uvd.size()) {
				   if(uvd.get(i).getUserID()==vacDays.get(j).getUser_id()) {
					   uvd.get(i).getVacDays().addAll(meres);
					   break;
				   }
				   i++;
			   }
			   if(i>=uvd.size()) {
				   x.setUserID(vacDays.get(j).getUser_id());
				   x.setVacDays(meres);
				   uvd.add(x);
			   }
		  }
		  return uvd;
	  }
	  
	  public static int minPartition(int[] S, int n, int S1, int S2, Map<String, Integer > lookup) {

				// base case: if list becomes empty, return the absolute
				// difference between two sets
				if (n < 0) {
					dif.add(Math.abs(S1 - S2));
					schedule_creator_Controller.S1.add(S1);
					schedule_creator_Controller.S2.add(S2);
					return Math.abs(S1 - S2);
				}
				
				// construct an unique map key from dynamic elements of the input
				// Note that can uniquely identify the subproblem with n & S1 only,
				// as S2 is nothing but S - S1 where S is sum of all elements
				String key = n + "|" + S1;
				
				// if sub-problem is seen for the first time, solve it and
				// store its result in a map
				if (!lookup.containsKey(key)) {
					// Case 1. include current item in the subset S1 and recur
					// for remaining items (n - 1)
					int inc = minPartition(S, n - 1, S1 + S[n], S2, lookup);
					
					// Case 2. exclude current item from subset S1 and recur for
					// remaining items (n - 1)
					int exc = minPartition(S, n - 1, S1, S2 + S[n], lookup);
				
					lookup.put(key, Integer.min(inc, exc));

				}
				
				return lookup.get(key);
				}
	  
	
	
}
