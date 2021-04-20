package theTestApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Tooltip;
import org.zkoss.chart.model.DefaultCategoryModel;

public class historyController extends SelectorComposer<Component>{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	static private String admin;
	static private int userID;
	private ListModel<UserList> userModel;	
	private ListModel<Months> monthsModel;
	private ListModel<Year> yearsModel;
	private static String month;
	private static int year;
	private static DefaultCategoryModel model1 = new DefaultCategoryModel();
	private static DefaultCategoryModel model2 = new DefaultCategoryModel();

	
	@Wire Charts chart;
	@Wire Charts chart2;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);

		
    	StoreMonthlyShifts sms = new StoreMonthlyShifts();
    	for(int i =1;i<13;i++) {
    		String month = sms.findMonth(i);
    		Months m = new Months();
    		m.setMonth(month);
    		((ListModelList<Months>)monthsModel).add(m); 
    	}
    	
    	for(int i=1980;i<2040;i++) {
    		Year y = new Year();
    		y.setYear(i);
    		((ListModelList<Year>)yearsModel).add(y); 

    	}
		BufferedReader reader;

    	File file=new File("C:/Users/strsi/Desktop/Diplwmatikh/Workspace/testApp");
    	File files[]=  file.listFiles();
    	List<String> list = OrderMonths(files);
    	for(String f:list){
    	        reader = new BufferedReader(new FileReader(f));
    			String line = reader.readLine();
    			while (line != null) {
    	    		String[] parts = line.split(" ");
    	    		model1.setValue(parts[1]+" "+parts[2], f, Integer.parseInt(parts[5].toString()));
    	    		model2.setValue(parts[1]+" "+parts[2], f, Integer.parseInt(parts[6].toString()));
    				line = reader.readLine();
    			
    	    }
    	}
    	
    	chart.setModel(model1);
    	chart2.setModel(model2);

        chart.getXAxis().setCrosshair(true);
        chart.getYAxis().setMin(0);
        chart.getYAxis().getTitle().setText("Night Holiday Shifts");
        Tooltip tooltip = chart.getTooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltip.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>"
            + "<td style=\"padding:0\"><b>{point.y:.1f} NHS</b></td></tr>");
        tooltip.setFooterFormat("</table>");
        tooltip.setShared(true);
        tooltip.setUseHTML(true);
        
        chart.getPlotOptions().getColumn().setPointPadding(0.2);
        chart.getPlotOptions().getColumn().setBorderWidth(0);
        
        chart2.getXAxis().setCrosshair(true);
        chart2.getYAxis().setMin(0);
        chart2.getYAxis().getTitle().setText("Holiday Shifts");
        Tooltip tooltip2 = chart2.getTooltip();
        tooltip2.setHeaderFormat("<span style=\"font-size:10px\">{point.key}</span><table>");
        tooltip2.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>"
            + "<td style=\"padding:0\"><b>{point.y:.1f} HS</b></td></tr>");
        tooltip2.setFooterFormat("</table>");
        tooltip2.setShared(true);
        tooltip2.setUseHTML(true);
        
        chart2.getPlotOptions().getColumn().setPointPadding(0.2);
        chart2.getPlotOptions().getColumn().setBorderWidth(0);
		}
	
	public historyController() {
		userModel = new ListModelList<UserList>();
		monthsModel = new ListModelList<Months>();
		yearsModel=new ListModelList<Year>();

		((ListModelList<UserList>)userModel).setMultiple(true);
	}
	
	public ListModel<UserList> getUserModel(){
		return userModel;
	}
	
	public ListModel<Months> getMonthsModel(){
		return monthsModel;
	}
	
	
	
    @Listen("onSelect = #months")
    public void changeMonth() {
        Set<Months> months = ((ListModelList<Months>)monthsModel).getSelection();
        historyController.month = months.iterator().next().getMonth();
    }

    @Listen("onSelect = #years")
    public void changeYear() {
        Set<Year> years = ((ListModelList<Year>)yearsModel).getSelection(); 
        historyController.year = years.iterator().next().getYear();
    }
    
    @Listen("onClick=#submit")
    public void findFile() {
		BufferedReader reader;
		try {
			if(userModel.getSize()!=0) {
				userModel = new ListModelList<UserList>();
			}
			reader = new BufferedReader(new FileReader(month+"-"+year+".txt"));
			String line = reader.readLine();
			while (line != null) {
	    		String[] parts = line.split(" ");
		    	UserList ulist=new UserList();
		    	ulist.setUid(Integer.parseInt(parts[0].toString()));
		    	ulist.setName(parts[1].toString());
		    	ulist.setSurname(parts[2].toString());
		    	ulist.setStandard(Integer.parseInt(parts[3].toString()));
		    	ulist.setNight(Integer.parseInt(parts[4].toString()));
		    	ulist.setHoliday(Integer.parseInt(parts[6].toString()));
		    	ulist.setNight_holiday(Integer.parseInt(parts[5].toString()));	
		    	((ListModelList<UserList>)userModel).add(ulist); 
				line = reader.readLine();
				
			}
			reader.close();


		
		
		
		} catch (IOException e) {
			Messagebox.show("Month not archived or file not found.");
			System.out.println("File not found");
		}
    }
    
	public ListModel<Year> getYearsModel() {
		return yearsModel;
	}

	public void setYearsModel(ListModel<Year> yearsModel) {
		this.yearsModel = yearsModel;
	}

	public static DefaultCategoryModel getModel1() {
		return model1;
	}

	public static DefaultCategoryModel getModel2() {
		return model2;
	}
	
	public static List<String> OrderMonths(File[] files) {
		List<String> list = new ArrayList<String>();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for(File f: files) {
			if(f.getName().contains("-"+Integer.toString(year))){
				list.add(f.getName());
			}
		}
	      Collections.sort(list,  new Comparator<String>() {
	      public int compare(String o1, String o2) {
	         try {
	              SimpleDateFormat sdf = new SimpleDateFormat("MMMMM");
	              return sdf.parse(o1).compareTo(sdf.parse(o2));  //sdf.parse returns date - So, Compare Date with date
	             } catch (ParseException ex) {
	                 return o1.compareTo(o2);
	             }
	         }
	     });
		return list;
		
	}
	
}







