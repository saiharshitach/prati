package rs.cybertrade.prati;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@WebListener
public class Servis implements ServletContextListener{
	@SuppressWarnings("unused")
	private ApplicationContext context;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		context = null;
	}

}
