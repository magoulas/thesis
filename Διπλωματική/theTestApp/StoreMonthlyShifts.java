package theTestApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

public class StoreMonthlyShifts implements Job {
	static int i=0;
	
	@Override
	 public void execute(JobExecutionContext context) throws JobExecutionException {
		if(i==0) {
			return;
		}
		i++;
		System.out.println("Store Monthly Shifts");
		
		   StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
		  	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
		  	  
		  	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
		  	Session session = factory.openSession();  
		  	Transaction t = session.beginTransaction();
		  	
		  	TypedQuery<user> query1 = session.createQuery("SELECT a FROM user a ");
		    List<user> user = query1.getResultList();
		    Date date = new Date();
		    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    int month = localDate.getMonthValue();
		    int year = localDate.getYear();
		    System.out.println("month: "+month);
		    StoreMonthlyShifts sms = new StoreMonthlyShifts();
		    String m = sms.findMonth(month);
		    //File myFile = new File("webapps/myfile.txt"); gia ton tomcat;
			File myFile = new File(m+"-"+year+".txt");
			FileWriter myWriter = null;
			try {
				myWriter = new FileWriter(myFile);
			} catch (IOException e) {
				System.out.println("file problems");
				e.printStackTrace();
			}
		for(user x: user) {
			TypedQuery<user> query2 = session.createQuery("UPDATE user SET standardShift = 0, nightShift = 0, holidayNightShift = 0, holidayShift = 0 WHERE iduser = "+x.getIduser());
			query2.executeUpdate();
			try {
				myWriter.write(x.getIduser()+" "+x.getName()+" "+x.getSurname()+" "+x.getStandardShift()+" "+x.getNightShift()+" "+x.getHolidayNightShift()+" "+x.getHolidayShift()+"\n");

			} catch (IOException e) {
				System.out.println("writer problems");
				e.printStackTrace();
			}
		}
		try {
			myWriter.close();
		} catch (IOException e) {
			System.out.println("writer close");
			e.printStackTrace();
		}
		
		
		t.commit();
    	session.close();
	}
	
	
	public String findMonth(int month) {
		switch (month) {
		case 1:
			return "January";
		case 2:
			return "February";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		}
		
		
		return "error";
	}
}
