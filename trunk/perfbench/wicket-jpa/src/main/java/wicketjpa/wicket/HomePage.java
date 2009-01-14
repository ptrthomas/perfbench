package wicketjpa.wicket;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import wicketjpa.entity.User;

public class HomePage extends WebPage {

    public HomePage() {
        add(new LoginForm("form"));
    }

    private class LoginForm extends StatelessForm {

        private TextField username = new TextField("username", new Model(""));
        private TextField password = new PasswordTextField("password", new Model(""));

        public LoginForm(String id) {
            super(id);            
            add(username);            
            add(password.setRequired(false));
            add(new BookmarkablePageLink("register", RegisterPage.class));
            add(new FeedbackPanel("messages"));
        }

        @Override
        protected void onSubmit() {
            EntityManager em = JpaRequestCycle.get().getEntityManager();
            Query query = em.createQuery("select u from User u"
                    + " where u.username = :username and u.password = :password");
            query.setParameter("username", username.getInput());
            query.setParameter("password", password.getInput());
            List<User> users = query.getResultList();
            if (users.size() == 0) {
                error("Login failed");
                return;
            }
            User user = users.get(0);
            BookingSession.get().setUser(user);
            getSession().info("Welcome, " + user.getUsername());
            setResponsePage(MainPage.class);
        }        
    }
}
