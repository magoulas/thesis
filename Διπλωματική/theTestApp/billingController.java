package theTestApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class billingController extends SelectorComposer<Component> {

	/**
	 * 
	 */
	static private String admin;
	static private int userID;
	private static final long serialVersionUID = 1L;
	private ListModel<UserList> userModel;
	
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
    	if(rows.size()!=0) {
    		for(Object[] row:rows) {
    		UserList ulist=new UserList();
    			ulist.setUid(Integer.parseInt(row[0].toString()));
    			ulist.setName(row[1].toString());
    			ulist.setSurname(row[2].toString());
    			ulist.setStandard(Integer.parseInt(row[3].toString()));
    			ulist.setNight(Integer.parseInt(row[4].toString()));
    			ulist.setHoliday(Integer.parseInt(row[6].toString()));
    			ulist.setNight_holiday(Integer.parseInt(row[5].toString()));
    			
    			((ListModelList<UserList>)userModel).add(ulist); 
    		}
    	}
    	
    	t.commit();
    	session.close();

	}
	

	
	public billingController() {
		userModel = new ListModelList<UserList>();
		((ListModelList<UserList>)userModel).setMultiple(true);
	}
	
	public ListModel<UserList> getUserModel(){
		return userModel;
	}
	
	@Listen("onClick=#redirect")
	public void redirect() throws UnsupportedEncodingException {
		String idUser = Integer.toString(userID);
		String adminn = admin;
		String encodeStr=URLEncoder.encode(idUser,StandardCharsets.UTF_8.name());
		String baseURL;
		baseURL="/history.zul?idUser=";
		String url = baseURL+encodeStr+"&admin=";
		encodeStr=URLEncoder.encode(adminn,StandardCharsets.UTF_8.name());
		url+=encodeStr;
		Executions.sendRedirect(url);
	}
}
