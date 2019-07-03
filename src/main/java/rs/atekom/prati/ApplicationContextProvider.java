package rs.atekom.prati;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware{

	private static class AplicationContextHolder{
        private static final InnerContextResource CONTEXT_PROV = new InnerContextResource();
        private AplicationContextHolder() {
            super();
        }
    }

    private static final class InnerContextResource {
        private ApplicationContext context;
        private InnerContextResource(){
            super();
        }

        private void setContext(ApplicationContext context){
            this.context = context;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return AplicationContextHolder.CONTEXT_PROV.context;
    }
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		AplicationContextHolder.CONTEXT_PROV.setContext(ac);
	}

}
