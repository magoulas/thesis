package theTestApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;

public class CalendarPopup extends GenericForwardComposer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Window popup;
	@Wire
	private Calendars calendar;
	private boolean visibility=false;
	@Wire
	private Datebox beginbox;
	@Wire
	private Datebox endbox;
	@Wire
	private Checkbox shift_1;
	@Wire
	private Checkbox shift_2;	
	@Wire
	private Checkbox shift_3;
	@Wire
	private Checkbox building_1;
	@Wire
	private Checkbox building_2;
	@Wire
	private Listbox list;

	static List<String> names;

	public void doAfterCompose(Component comp) throws Exception 
	   {
	       super.doAfterCompose(comp);
	       names = new LinkedList();
	       popup.setVisible(visibility);

	   }

	String day;
	String month;
	String year;
	String shift;
	String idUser;
	String building;
	//otan anoigei adio event gia na to ftiaksw.
	public void onEventCreate$calendar(CalendarsEvent event) throws ParseException {
		if(!visibility) {
			visibility=true;
			
			Date startDate= event.getBeginDate();
			String begindate=startDate.toString();
			String[] date=begindate.split(" ");
			String month = getMonth(date[1]);
			this.day=date[2];
			Integer x = Integer.parseInt(date[2]);
			x++;
			String s = x.toString();
			startDate= new SimpleDateFormat("dd/MM/yyyy").parse(s+"/"+month+"/"+date[5]);

			Date endDate= event.getEndDate();
			String enddate=endDate.toString();
			date=enddate.split(" ");
			month = getMonth(date[1]);
			endDate= new SimpleDateFormat("dd/MM/yyyy").parse(date[2]+"/"+month+"/"+date[5]);
			
			beginbox.setValue(startDate);
			endbox.setValue(endDate);
			popup.setTitle("Event to create");
			endbox.setDisabled(true);
			popup.setVisible(visibility);
				
			
			ListModelList model = new ListModelList(names.size());
			model.addAll(names);
			list.setModel(model);
			list.setMultiple(true);
			list.setCheckmark(true);
			
			this.month=month;
			this.year=date[5];
		}else {
			visibility=false;
			shift_1.setChecked(false);
			shift_2.setChecked(false);
			shift_3.setChecked(false);
			building_1.setChecked(false);
			building_2.setChecked(false);

			popup.setVisible(visibility);
		}
	}
	public int eventID;
	
	//otan anoigei etoimo event na parw ta data
	public void onEventEdit$calendar(CalendarsEvent event) throws ParseException {
		
		if(!visibility) {
			visibility=true;
			String content = event.getCalendarEvent().getContent();
			String[] content_split=content.split(" ");
			String[] shifts=content_split[2].split("-");
			String title = event.getCalendarEvent().getTitle();
			String[] event_id_split=title.split(" @");
			eventID=Integer.parseInt(event_id_split[1]);
			int shift = 0;
			int building = 0;
			if(shifts[0].equals("06:00")) {
				shift_1.setChecked(true);
				shift=1;
			}else if(shifts[0].equals("14:00")) {
				shift_2.setChecked(true);
				shift=2;
			}else if(shifts[0].equals("22:00")) {
				shift_3.setChecked(true);
				shift=3;
			}
			if(content_split[1].equals("Βουτών")) {
				building_1.setChecked(true);
				building=1;
			}else if(content_split[1].equals("Κνωσού")){
				building_2.setChecked(true);
				building=2;
			}
			Date startDate= event.getCalendarEvent().getBeginDate();
			String begindate=startDate.toString();
			String[] date=begindate.split(" ");
			String month = getMonth(date[1]);
			startDate= new SimpleDateFormat("dd/MM/yyyy").parse(date[2]+"/"+month+"/"+date[5]);
			Date endDate= event.getCalendarEvent().getEndDate();
			String enddate=endDate.toString();
			date=enddate.split(" ");
			month = getMonth(date[1]);
			endDate= new SimpleDateFormat("dd/MM/yyyy").parse(date[2]+"/"+month+"/"+date[5]);
			
			Execution execution = Executions.getCurrent();
			String idUser = execution.getParameter("idUser");
			
			
			popup.setTitle("Event for: "+event.getCalendarEvent().getTitle());
			beginbox.setValue(startDate);
			endbox.setValue(endDate);
			popup.setVisible(visibility);
		}else {
			visibility=false;
			shift_1.setChecked(false);
			shift_2.setChecked(false);
			shift_3.setChecked(false);
			building_1.setChecked(false);
			building_2.setChecked(false);

			popup.setVisible(visibility);
		}

	}
	
	public String getMonth(String month) {
		String mhnas = null;
		switch (month) {
		case "Jan":
			mhnas="01";
			break;
		case "Feb":
			mhnas="02";
			break;
		case "Mar":
			mhnas="03";
			break;
		case "Apr":
			mhnas="04";
			break;
		case "May":
			mhnas="05";
			break;
		case "Jun":
			mhnas="06";
			break;
		case "Jul":
			mhnas="07";
			break;
		case "Aug":
			mhnas="08";
			break;
		case "Sep":
			mhnas="09";
			break;
		case "Oct":
			mhnas="10";
			break;
		case "Nov":
			mhnas="11";
			break;
		case "Dec":
			mhnas="12";
			break;
		}
		return mhnas;
	}
	
	public void onCheck$shift_1(CheckEvent event) {
		if(shift_1.isChecked()) {
			this.shift="1";
		}
		if(shift_2.isChecked()||shift_3.isChecked()) {
			shift_2.setChecked(false);
			shift_3.setChecked(false);
			this.shift="1";
		}
	}
	public void onCheck$shift_2(CheckEvent event) {
		if(shift_2.isChecked()) {
			this.shift="2";
		}
		if(shift_1.isChecked()||shift_3.isChecked()) {
			shift_1.setChecked(false);
			shift_3.setChecked(false);
			this.shift="2";
		}
	}
	public void onCheck$shift_3(CheckEvent event) {
		if(shift_3.isChecked()) {
			this.shift="3";
		}
		if(shift_1.isChecked()||shift_2.isChecked()) {
			shift_2.setChecked(false);
			shift_1.setChecked(false);
			this.shift="3";

		}
	}
	public void onCheck$building_1(CheckEvent event) {
		if(building_1.isChecked()) {
			this.building="1";
		}
		if(building_2.isChecked()) {
			building_2.setChecked(false);
			this.building="1";
		}
	}
	
	
	public void onCheck$building_2(CheckEvent event) {
		if(building_2.isChecked()) {
			this.building="2";
		}
		if(building_1.isChecked() ) {
			building_1.setChecked(false);
			this.building="2";
		}
	}
	
    public void onClick$ok(Event event) {
    	Execution execution = Executions.getCurrent();
		String idUser = execution.getParameter("idUser");
		String admin;
		admin = execution.getParameter("admin");
		/*		
    	if(admin.compareTo("0")==0) {
        	visibility=false;
    		shift_1.setChecked(false);
    		shift_2.setChecked(false);
    		shift_3.setChecked(false);
    		building_1.setChecked(false);
    		building_2.setChecked(false);

    		popup.setVisible(visibility);
    		return;
    	}*/
    	StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	Query query;
    	NativeQuery max_idCalendarEvent;
    	
    	Iterator iter=list.getSelectedItems().iterator();
    	while(iter.hasNext()) {
    	max_idCalendarEvent = session.createSQLQuery("select max(idCalendarEvent)  from test.calendarevent");
    	int rows = 0;
    	
    	if(max_idCalendarEvent.getSingleResult()!=null) {
    		rows= (int)max_idCalendarEvent.getSingleResult();
    		rows++;
    	}else {
    		rows=0;
    	}


    	System.out.println("query execution "+eventID);
    	
    		Listitem listitem= (Listitem)iter.next();
    		System.out.println(listitem.getValue().toString());
			String[] listdata=listitem.getValue().toString().split(" ");
			String userID = listdata[0];
        	query=session.createNativeQuery("insert into calendarevent (idCalendarEvent,day,month,year,shift,idUser,building) values ("+rows+","+day+","+month+","+year+","+this.shift+","+userID+","+this.building+")");
        	query.executeUpdate();
    	}
    	t.commit();
    	session.close();
    //	insert into test.calendarevent values (1,25,03,2020,1,1,1);



    	
    	visibility=false;
		shift_1.setChecked(false);
		shift_2.setChecked(false);
		shift_3.setChecked(false);
		building_1.setChecked(false);
		building_2.setChecked(false);

		popup.setVisible(visibility);
    }
    
    public void onClick$cancel(Event event) {
    	visibility=false;
		shift_1.setChecked(false);
		shift_2.setChecked(false);
		shift_3.setChecked(false);
		building_1.setChecked(false);
		building_2.setChecked(false);

		popup.setVisible(visibility);
    }
    
    public void onClick$delete(Event event) {
    	Execution execution = Executions.getCurrent();
		String idUser = execution.getParameter("idUser");
		String admin;
		admin = execution.getParameter("admin");
		/*
    	if(admin.compareTo("0")==0) {
        	visibility=false;
    		shift_1.setChecked(false);
    		shift_2.setChecked(false);
    		shift_3.setChecked(false);
    		building_1.setChecked(false);
    		building_2.setChecked(false);

    		popup.setVisible(visibility);
    		return;
    	}*/
    	StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	Query query;
    	
    	System.out.println("query execution "+eventID);
    	query=session.createQuery("delete from calendarevent where idCalendarEvent="+eventID+"");
    	query.executeUpdate();
    	t.commit();
    	session.close();
    	
    	visibility=false;
		shift_1.setChecked(false);
		shift_2.setChecked(false);
		shift_3.setChecked(false);
		building_1.setChecked(false);
		building_2.setChecked(false);

		popup.setVisible(visibility);
    }
    
    public void onSelect$listboxId(SelectEvent event) {
    	System.out.println(event.getData());
    }
}





//delete from test.calendarevent where idCalendarEvent=1;





















