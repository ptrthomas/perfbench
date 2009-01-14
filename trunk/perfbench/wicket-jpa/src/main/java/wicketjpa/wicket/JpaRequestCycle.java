package wicketjpa.wicket;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

public class JpaRequestCycle extends WebRequestCycle {

    private EntityManager em;
    private boolean endConversation;

    public static JpaRequestCycle get() {
        return (JpaRequestCycle) RequestCycle.get();
    }

    public JpaRequestCycle(WebApplication application, WebRequest request, Response response) {
        super(application, request, response);
    }

    public EntityManager getEntityManager() {
        if (em == null) {
            EntityManagerFactory emf = ((BookingApplication) getApplication()).getEntityManagerFactory();
            em = emf.createEntityManager();
            em.getTransaction().begin();
        }
        return em;
    }

    private boolean isTransactionActive() {
        return em != null && em.getTransaction().isActive();
    }

    @Override
    protected void onEndRequest() {
        super.onEndRequest();
        if (isTransactionActive()) {
            em.getTransaction().commit();
        }
        if (endConversation) {
            getRequest().getPage().getPageMap().remove();
        }
    }

    @Override
    public Page onRuntimeException(Page page, RuntimeException e) {        
        if (isTransactionActive()) {
            em.getTransaction().rollback();
        }
        if (e instanceof PageExpiredException) {
            getSession().error("The page you requested has expired.");
            return new MainPage();
        }
        return super.onRuntimeException(page, e);
    }

    public void endConversation() {
        endConversation = true;
    }
}
