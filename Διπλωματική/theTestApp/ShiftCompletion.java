package theTestApp;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ShiftCompletion implements Job{
	static int s=0;
	static String march25 = "25-03";
	static String may1="01-05";
	static String aug15="15-08";
	static String dec24="24-12";
	static String dec25="25-12";
	static String dec26="26-12";
	static String jan1="01-01";
	static String jan6="06-01";
	static String oct28="28-10";
	static String dec31="31-12";
	static String dummy="11-06";
	
	@Override
	 public void execute(JobExecutionContext context) throws JobExecutionException {
		if(s==0) {
			return;
		}
		System.out.println(" Shift Completion ");
		s++;
		completion();
	}
	
	
	public static void completion() {
		   boolean isHol=false;

		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
		   LocalDateTime now = LocalDateTime.now();  
		   String[] parts = dtf.format(now).toString().split("-");
		   int day = Integer.parseInt(parts[0]);
		   int month = Integer.parseInt(parts[1]);
		   int year = Integer.parseInt(parts[2]);
		   Calendar c= Calendar.getInstance();
		   if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			   isHol=true;
			}
		   
		   c = getOrthodoxEaster(year);
		   String m;
		   if(c.get(Calendar.MONTH)<10) {
			   m="0"+(c.get(Calendar.MONTH)+1);
		   }else {
			   m=Integer.toString(c.get(Calendar.MONTH))+1;
		   }
		   String a;
		   if(c.get(Calendar.DATE)<10) {
			   a="0"+c.get(Calendar.DATE);
		   }else {
			   a=Integer.toString(c.get(Calendar.DATE));
		   }
		   String east= a+"-"+m+"-"+c.get(Calendar.YEAR);
		   
		   String[] g=east.split("-");
		   
		   
		   c.set(Integer.parseInt(g[2]), Integer.parseInt(g[1]), Integer.parseInt(g[0]));

		   c.add(Calendar.DAY_OF_MONTH, -48);
		   
		   LocalDate ld = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		   ld.format(dtf);
		   ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
		   String[] ashM = ld.toString().split("-");
		   String cleanMonday = ashM[2]+"-"+ashM[1]+"-"+ashM[0];
		   
		   ld = LocalDate.of(Integer.parseInt(g[2]), Integer.parseInt(g[1]), Integer.parseInt(g[0]));
		   ld.format(dtf);
		   ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
		   String[] eM = ld.toString().split("-");
		   String easterMonday = eM[2]+"-"+eM[1]+"-"+eM[0];
		   
		   ld = LocalDate.of(Integer.parseInt(g[2]), Integer.parseInt(g[1]), Integer.parseInt(g[0]));
		   ld.format(dtf);
		   ld = ld.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
		   eM = ld.toString().split("-");
		   String bigFriday = eM[2]+"-"+eM[1]+"-"+eM[0];
		   
		   
		   ld = LocalDate.of(Integer.parseInt(g[2]), Integer.parseInt(g[1]), Integer.parseInt(g[0]));
		   ld.format(dtf);
		   ld = ld.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
		   eM = ld.toString().split("-");
		   String bigSaturday = eM[2]+"-"+eM[1]+"-"+eM[0];
		   
		   
		   c.set(Integer.parseInt(g[2]), Integer.parseInt(g[1]), Integer.parseInt(g[0]));
		   c.add(Calendar.DAY_OF_MONTH, 48);

		   ld = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		   ld.format(dtf);
		   ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
		   String[] hoSpirit = ld.toString().split("-");
		   String holySpirit = hoSpirit[2]+"-"+hoSpirit[1]+"-"+hoSpirit[0];

		    StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
		  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
		  	  
		  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
		  	Session session = factory.openSession();  
		  	Transaction t = session.beginTransaction();		   

			TypedQuery<calendarevent> query1 = session.createQuery("SELECT a FROM calendarevent a "
		            + "WHERE  a.day=?1 and a.month=?2 and a.year=?3");
		    query1.setParameter(1, day);
		    query1.setParameter(2, month);
		    query1.setParameter(3, year);

		    List<calendarevent> event = query1.getResultList();
		    
		    String today=Integer.toString(year);
		    if(month<10) {
		    	today="0"+month+"-"+today;
		    }else {
		    	today=month+"-"+today;
		    }
		    if(day<10) {
		    	today="0"+day+"-"+today;
		    }else {
		    	today=day+"-"+today;
		    }
		    
	        Set<String> holidaySet = new HashSet<String>(); 
	        holidaySet.add(easterMonday);
	        holidaySet.add(cleanMonday);
	        holidaySet.add(holySpirit);
	        holidaySet.add(bigSaturday);
	        holidaySet.add(bigFriday);
	        holidaySet.add(march25+"-"+year);
	        holidaySet.add(may1+"-"+year);
	        holidaySet.add(aug15+"-"+year);
	        holidaySet.add(dec24+"-"+year);
	        holidaySet.add(dec25+"-"+year);
	        holidaySet.add(dec26+"-"+year);
	        holidaySet.add(jan1+"-"+year);
	        holidaySet.add(jan6+"-"+year);
	        holidaySet.add(oct28+"-"+year);
	        holidaySet.add(dec31+"-"+year);
	        
	       // holidaySet.add(dummy+"-"+year);
	        
	        Iterator<String> iter = holidaySet.iterator();
	        ShiftCompletion sc = new ShiftCompletion();
	        while(iter.hasNext() && !isHol) {
	        	isHol= sc.isHoliday(today,iter.next());
	        }
	        
	        if(isHol) {
	        	
	        	for(calendarevent ce:event) {
	        		int idUser = ce.getIdUser();
	        		int shift = ce.getShift();
	        		user employee = session.get(user.class, idUser);
	        		if(shift==3) {
	        			int i = employee.getHolidayNightShift()+1;
	        			employee.setHolidayNightShift(i);
	        		}else {
	        			int i = employee.getHolidayShift()+1;
	        			employee.setHolidayShift(i);
	        		}
	        		session.save(employee);

	        	}
	        	
	        }else {
	        	
	        	for(calendarevent ce:event) {
	        		int idUser = ce.getIdUser();
	        		int shift = ce.getShift();
	        		user employee = session.get(user.class, idUser);
	        		if(shift==3) {
	        			int i = employee.getNightShift()+1;
	        			employee.setNightShift(i);
	        		}else {
	        			int i = employee.getStandardShift()+1;
	        			employee.setStandardShift(i);
	        		}
	        		session.save(employee);

	        	}
	        }
	        
	    	t.commit();
	    	session.close();
		    
	}
	
	public boolean isHoliday(String today, String holiday) {
		if(today.contains(holiday)) {
			return true;
		}
		return false;
	}
	
	public static Calendar getOrthodoxEaster(int myear) {
		Calendar dof = Calendar.getInstance();

		int r1 = myear % 4;
		int r2 = myear % 7;
		int r3 = myear % 19;
		int r4 = (19 * r3 + 15) % 30;
		int r5 = (2 * r1 + 4 * r2 + 6 * r4 + 6) % 7;
		int mdays = r5 + r4 + 13;

		if (mdays > 39) {
			mdays = mdays - 39;
			dof.set(myear, 4, mdays);
		} else if (mdays > 9) {
			mdays = mdays - 9;
			dof.set(myear, 3, mdays);
		} else {
			mdays = mdays + 22;
			dof.set(myear, 2, mdays);
		}
		return dof;
	}
}
