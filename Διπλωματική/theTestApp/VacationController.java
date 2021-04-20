package theTestApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Calendar;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class VacationController extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static private String admin;
	static private int userID;
	private ListModel<VacationList> vacationModel;

	
	@Wire Window win;
	@WireVariable private String adminn=admin;
	@Wire Calendar startcal;
	@Wire Calendar endcal;
	@Wire Textbox vac_type;
	
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
    	
    	if(isAdmin) {
    		query = session.createSQLQuery("select * from test.vacationdays");

    	}else {
    		query = session.createSQLQuery("select * from test.vacationdays where user_id=\""+userID+"\"");

    	}

    	
    	List<Object[]> rows = query.list();
    	int vacation_id=-1;
    	String name=null;
    	String surname=null;
    	String starting_date=null;
    	String ending_date=null;
    	String type=null;
    	int status = 0;
    	String str_status=null;
    	if(rows.size()!=0) {
    		for(Object[] row:rows) {
    			vacation_id=Integer.parseInt(row[0].toString());
    			starting_date=row[2].toString();
    			ending_date=row[3].toString();
    			type=row[4].toString();
    			status=Integer.parseInt(row[5].toString());
    			if(status==0) {
    				str_status="Pending";
    			}else if(status==1) {
    				str_status="Accepted";
    			}else if(status==-1) {
    				str_status="Rejected";
    			}

    			NativeQuery<Object[]> query3;

    			query3= session.createSQLQuery("select iduser,name,surname from test.user where idUser=\""+row[1].toString()+"\"");
    			
    			List<Object[]> rows1=query3.list();
    			if(rows1.size()!=0) {
    				for(Object[] row1:rows1) {
    					name=row1[1].toString();
    					surname=row1[2].toString();
    				}
    			}
    			VacationList vlist= new VacationList();
    			
    			vlist.setVid(vacation_id);
    			vlist.setName(name);
    			vlist.setSurname(surname);
    			vlist.setStart_date(starting_date);
    			vlist.setEnd_date(ending_date);
    			vlist.setStatus(str_status);
    			vlist.setType(type);
    			((ListModelList<VacationList>)vacationModel).add(0,vlist); 
    			}
    	}
    	
    	t.commit();
    	session.close();
	}
	
	public VacationController() {
		vacationModel = new ListModelList<VacationList>();
		((ListModelList<VacationList>)vacationModel).setMultiple(true);
	}
	
	public ListModel<VacationList> getVacationModel(){
		return vacationModel;
	}
	
	@Listen("onClick=button")
	public void accept(MouseEvent event){
		Component cmp = new Button();
		cmp = event.getTarget();
		if(cmp.getId().toString().compareTo("submit")==0) {
			return;
		}
		String[] parts = cmp.getId().toString().split(("\\+"));
		String button=parts[0];
		String vid=parts[1];
		
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	Query query;

    	
    	
		if(button.compareTo("Accept")==0) {
			VacationList vlist= new VacationList();
			int found=-1;
			for(int i =0;i<((ListModelList<VacationList>)vacationModel).size();i++) {
				vlist=((ListModelList<VacationList>)vacationModel).get(i);
				if(vlist.getVid()==Integer.parseInt(vid)) {
					found=i;
					break;
				}
			}
			if(found!=-1) {
				((ListModelList<VacationList>)vacationModel).remove(found);
				vlist.setStatus("Accepted");
				((ListModelList<VacationList>)vacationModel).add(found, vlist);;
				vacationdays vl = session.get(vacationdays.class, Integer.parseInt(vid));
	    		vl.setStatus(1);
	    		t.commit();
	        	session.close();
			}
		}else if(button.compareTo("Decline")==0) {
			VacationList vlist= new VacationList();
			int found=-1;
			for(int i =0;i<((ListModelList<VacationList>)vacationModel).size();i++) {
				vlist=((ListModelList<VacationList>)vacationModel).get(i);
				if(vlist.getVid()==Integer.parseInt(vid)) {
					found=i;
					break;
				}
			}
			if(found!=-1) {
				((ListModelList<VacationList>)vacationModel).remove(found);
				vlist.setStatus("Rejected");
				((ListModelList<VacationList>)vacationModel).add(found,vlist);
				vacationdays vl = session.get(vacationdays.class, Integer.parseInt(vid));
	    		vl.setStatus(-1);
	    		t.commit();
	        	session.close();
			}
		}
		
	}
	
	@Listen("onClick=button#submit")
	public void submit() throws ParseException{
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build(); 
    	
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();
    	
    	Date startdate = new Date();
    	startdate = startcal.getValue();
    	Date enddate = new Date();
    	enddate = endcal.getValue();
		String[] start = startdate.toString().split((" "));
		String[] end = enddate.toString().split((" "));
		
		if(Integer.parseInt(start[5])>Integer.parseInt(end[5])) {
			return;
		}else if(Integer.parseInt(getMonth(start[1]))>Integer.parseInt(getMonth(end[1]))) {
			return;
		}else if(Integer.parseInt(start[2])>Integer.parseInt(end[2]) && Integer.parseInt(getMonth(start[1]))>=Integer.parseInt(getMonth(end[1]))) {
			return;
		}
		
		String inputstart = start[2]+"-"+getMonth(start[1])+"-"+start[5];
		String inputend = end[2]+"-"+getMonth(end[1])+"-"+end[5];
		
		WeekNumber wn=new WeekNumber();
		int week_num = wn.getWeekNum(inputstart.replace("-", ""));
		int week_end = wn.getWeekNum(inputend.replace("-",""));
		vacationdays vd = new vacationdays();
		vd.setStarting_date(inputstart);
		vd.setEnding_date(inputend);
		vd.setType(vac_type.getValue());
		vd.setUser_id(userID);
		vd.setStatus(0);
		vd.setWeek_num_start(week_num);
		vd.setWeek_num_end(week_end);
		
		Integer max =(Integer) session.createQuery("select max(vacation_id) from vacationdays").getSingleResult();
		if(max==null) {
			max=0;
		}
		vd.setVacation_id(max+1);
		user employee = session.get(user.class, userID);
		
		session.save(vd);
		
		t.commit();
    	session.close();
		
    	VacationList vlist= new VacationList();
		
    	
    	
		vlist.setVid(vd.getVaction_id());
		vlist.setName(employee.getName());
		vlist.setSurname(employee.getSurname());
		vlist.setStart_date(vd.getStarting_date());
		vlist.setEnd_date(vd.getEnding_date());
		vlist.setStatus("Pending");
		vlist.setType(vd.getType());
		((ListModelList<VacationList>)vacationModel).add(vlist);   
    	
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
	
	
}
