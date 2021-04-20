package theTestApp;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Calendar;

public class calendarController extends SelectorComposer<Component> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Wire
	private Calendars calendar;
	private final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	SimpleCalendarModel calendarModel = new SimpleCalendarModel();
	
	static int userID;
	static String admin;

	private Date getDate(String dateText) {
		try {
			return DATA_FORMAT.parse(dateText);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isAdmin(boolean admin) {
		return admin;
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Execution execution = Executions.getCurrent();
		String idUser = execution.getParameter("idUser");
		admin = execution.getParameter("admin");
		
		userID = Integer.parseInt(idUser);
		boolean isAdmin;

		if(admin.equals("1")) {
			isAdmin=true;
		}else {
			isAdmin=false;
		}
		//Transaction setup
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	NativeQuery<Object[]> query;
    	NativeQuery<Object[]> query3;

    	if(isAdmin) {
    		query = session.createSQLQuery("select * from test.calendarevent");
    		query3 = session.createSQLQuery("select iduser,name,surname from test.user");

    	}else {
    		query = session.createSQLQuery("select * from test.calendarevent where idUser=\""+userID+"\"");
    		query3 = session.createSQLQuery("select iduser,name,surname from test.user where idUser=\""+userID+"\"");

    	}
    	List<Object[]> rows3 = query3.list();
    	if(rows3.size()!=0 && CalendarPopup.names.isEmpty()) {
    		for(Object[] row:rows3) {
    	    	CalendarPopup.names.add(Integer.parseInt(row[0].toString())+" "+row[1].toString()+" "+row[2].toString());
    		}
    	}

    	String day = null;
    	String month = null;
    	String year = null;
    	int shift = 0;
		int building = 0;
		int user;
    	List<Object[]> rows = query.list();
    	if(rows.size()!=0) {
    		for(Object[] row:rows) {
    			day=row[1].toString();
    			month=row[2].toString();
    			year=row[3].toString();
    			shift=Integer.parseInt(row[4].toString());
    			building=Integer.parseInt(row[6].toString());
    			user=Integer.parseInt(row[5].toString());
    			String key=day+" "+month+" "+year+" "+shift+" "+building+" "+user;
    	    	NativeQuery<Object[]> query2;
        		query2 = session.createSQLQuery("select name,surname from test.user where idUser=\""+user+"\"");
        		List<Object[]> user_info = query2.list();
        		Object[] info = user_info.get(0);
        		String name=info[0].toString();
        		String surname=info[1].toString();
    			SimpleCalendarEvent sce = new SimpleCalendarEvent();
    			String shift_start = null;
    			String shift_end = null;
    			String end_day=null;
    			if(shift==1) {
    				shift_start="06:00";
    				shift_end="14:00";
    				end_day=day;
    			}else if(shift==2) {
    				shift_start="14:00";
    				shift_end="22:00";
    				end_day=day;
    			}else if(shift==3) {
    				shift_start="22:00";
    				shift_end="06:00";
    				int x = Integer.parseInt(day);
    				x++;
    				end_day=Integer.toString(x) ;
    				
    			}
    			String site=null;
    			if(building==1) {
    				site="Πανεπιστημιούπολη Βουτών";
    			}else if (building==2) {
    				site="Πανεπιστημιούπολη Κνωσού";
    			}
    			String eventstart=day+"/"+month+"/"+year+" "+shift_start;
    			String eventend=end_day+"/"+month+"/"+year+" "+shift_end;
    			sce.setBeginDate(getDate(eventstart));
    			sce.setEndDate(getDate(eventend));
    			if(user==userID) {
        			sce.setContentColor("green");
        			sce.setHeaderColor("green");

    			}else {
    				sce.setContentColor("black");
        			sce.setHeaderColor("black");

    			}
    			sce.setContent(site+" "+shift_start+"-"+shift_end);
    			sce.setTitle(name+" "+surname+" @"+row[0].toString());
    			calendarModel.add(sce);
    		}
    	}
		calendar.setModel(calendarModel);

    	t.commit();
    	session.close();
    	
	}
	
	@Listen("onClick=#redirect_vacation")
	public void redirect() throws UnsupportedEncodingException {
		String idUser = Integer.toString(userID);
		String adminn = admin;
		String encodeStr=URLEncoder.encode(idUser,StandardCharsets.UTF_8.name());
		String baseURL;
		if(adminn.compareTo("1")==0) {
			baseURL="/vacation.zul?idUser=";
		}else {
			baseURL="/vacation_non_admin.zul?idUser=";
		}
		String url = baseURL+encodeStr+"&admin=";
		encodeStr=URLEncoder.encode(adminn,StandardCharsets.UTF_8.name());
		url+=encodeStr;
		Executions.sendRedirect(url);
	}
	@Listen("onClick=#redirect_schedule_creator")
	public void redirect1() throws UnsupportedEncodingException {
		String idUser = Integer.toString(userID);
		String adminn = admin;
		String encodeStr=URLEncoder.encode(idUser,StandardCharsets.UTF_8.name());
		String baseURL="/schedule_creator.zul?idUser=";
		String url = baseURL+encodeStr+"&admin=";
		encodeStr=URLEncoder.encode(adminn,StandardCharsets.UTF_8.name());
		url+=encodeStr;
		Executions.sendRedirect(url);
	}
	
	@Listen("onClick=#redirect_billing")
	public void redirect2() throws UnsupportedEncodingException {

		String idUser = Integer.toString(userID);
		String adminn = admin;
		String encodeStr=URLEncoder.encode(idUser,StandardCharsets.UTF_8.name());
		String baseURL="/billing.zul?idUser=";
		String url = baseURL+encodeStr+"&admin=";
		encodeStr=URLEncoder.encode(adminn,StandardCharsets.UTF_8.name());
		url+=encodeStr;
		Executions.sendRedirect(url);
	}
	
    @Listen("onClick = #next")
    public void gotoNext(){
        calendar.nextPage();
    }
    @Listen("onClick = #prev")
    public void gotoPrev(){
        calendar.previousPage();
    }
    
    @Listen("onClick = #pageDay")
    public void changeToDay(){
        calendar.setMold("default");
        calendar.setDays(1);
    }
    @Listen("onClick = #pageWeek")
    public void changeToWeek(){
        calendar.setMold("default");
        calendar.setDays(7);
    }
    @Listen("onClick = #pageMonth")
    public void changeToYear(){
        calendar.setMold("month");
    }
}
