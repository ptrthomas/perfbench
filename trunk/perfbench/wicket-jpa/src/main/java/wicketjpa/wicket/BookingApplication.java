package wicketjpa.wicket;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.wicket.Component;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.convert.converters.BigDecimalConverter;

public class BookingApplication extends WebApplication {

    private EntityManagerFactory emf;

    public Class getHomePage() {
        return MainPage.class;
    }

    @Override
    public void init() {
        super.init();
        emf = Persistence.createEntityManagerFactory("bookingDatabase");
        getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {
            public boolean isActionAuthorized(Component c, Action a) {
                return true;
            }
            public boolean isInstantiationAuthorized(Class clazz) {
                if (TemplatePage.class.isAssignableFrom(clazz)) {
                    if (BookingSession.get().getUser() == null) {
                        throw new RestartResponseAtInterceptPageException(HomePage.class);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {
        return new JpaRequestCycle(this, (WebRequest) request, response);
    }

    @Override
    public BookingSession newSession(Request request, Response response) {
        return new BookingSession(request);
    }

//    @Override
//    protected ISessionStore newSessionStore() {
//        return new HttpSessionStore(this);
//    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator converterLocator = new ConverterLocator();
        BigDecimalConverter converter = new BigDecimalConverter() {
            @Override
            public NumberFormat getNumberFormat(Locale locale) {
                return DecimalFormat.getCurrencyInstance(Locale.US);
            }
        };
        converterLocator.set(BigDecimal.class, converter);
        return converterLocator;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
