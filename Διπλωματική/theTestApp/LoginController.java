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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoginController extends SelectorComposer<Window> {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	@Wire
    Textbox input;
    @Wire
    Label output;
    @Wire
    Label x;
    @Wire
    Label name;
    @Wire
    Label surname;
    */
	
    @Wire
    Textbox NameIn;
    @Wire
    Textbox PassIn;
    
    
    //REDIRECT TO MAIN FROM LOGIN
    public void redirect(Object[] rows) throws UnsupportedEncodingException {
       // Executions.sendRedirect("/calendar.zul?idUser=");
    	String idUser = rows[2].toString();
    	String admin = rows[3].toString();
    	String encodeStr=URLEncoder.encode(idUser,StandardCharsets.UTF_8.name());
    	String baseURL;
    	if(admin.compareTo("1")==0) {
    		baseURL="/calendar.zul?idUser=";
    	}else {
    		baseURL="/calendar_non_admin.zul?idUser=";
    	}
    	String url=baseURL+encodeStr+"&admin=";
    	//Executions.sendRedirect(url);
    	encodeStr=URLEncoder.encode(admin,StandardCharsets.UTF_8.name());
    	url+=encodeStr;
    	Executions.sendRedirect(url);
    	//Executions.sendRedirect(Executions.getCurrent().encodeURL(url));
    }
    
    
//Check if user exists for login. Create new user.
    
    @Listen("onClick=#login")
    public void Login() throws UnsupportedEncodingException {
    	login name = new login();
    	name.setUsername(NameIn.getValue());
    	name.setPassword(PassIn.getValue());

    	StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();  
    	Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();  
    	  
    	SessionFactory factory = meta.getSessionFactoryBuilder().build();  
    	Session session = factory.openSession();  
    	Transaction t = session.beginTransaction();   
    	
    	NativeQuery<Object[]> query = session.createSQLQuery("select * from test.login where username=\""+name.getUsername()+"\" and password=\""+name.getPassword()+"\"");
    	List<Object[]> rows = query.list();
    	if(rows.size()!=0) {
    		for(Object[] row:rows) {

    	    	redirect(row);
    		}
    		return;
    	}
    	else{
			Messagebox.show("Username and password missmatch");
    		System.out.println("User does not exist or username and password missmatch");
    	}
    	//Create new user.
    	//session.save(name);
    	t.commit();
    	session.close();

    }

     
}