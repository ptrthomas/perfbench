package wicketjpa.wicket;

import javax.persistence.EntityManager;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import wicketjpa.entity.User;

public class PasswordPage extends TemplatePage {

    public PasswordPage() {
        add(new PasswordForm("form"));
    }

    private class PasswordForm extends Form {

        private User user = getBookingSession().getUser();

        public PasswordForm(String id) {
            super(id);
            setModel(new CompoundPropertyModel(user));
            FormComponent passwordField = new PasswordTextField("password");            
            add(new EditBorder("passwordBorder", passwordField));
            FormComponent verifyField = new PasswordTextField("verify", new Model(""));
            add(new EditBorder("verifyBorder", verifyField));            
            add(new EqualPasswordInputValidator(passwordField, verifyField));            
            add(new PageLink("cancel", MainPage.class));
        }

        @Override
        protected void onSubmit() {
            EntityManager em = getEntityManager();
            em.merge(user);
            getSession().info("Password updated");
            setResponsePage(MainPage.class);
        }        
    }    
}
