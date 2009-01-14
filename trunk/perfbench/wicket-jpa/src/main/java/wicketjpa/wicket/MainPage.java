package wicketjpa.wicket;

import wicketjpa.entity.Hotel;
import wicketjpa.entity.Booking;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class MainPage extends TemplatePage {

    private static final List<Integer> pageSizes = Arrays.asList(5, 10, 20);

    private WebMarkupContainer hotelsContainer;      

    public MainPage() {
        setModel(new CompoundPropertyModel(new PropertyModel(this, "session")));
        add(new FeedbackPanel("messages"));
        add(new SearchForm("form"));
        hotelsContainer = new WebMarkupContainer("hotelsContainer");        
        add(hotelsContainer.setOutputMarkupId(true));

        final PropertyListView hotelsListView = new PropertyListView("hotels") {
            protected void populateItem(ListItem item) {
                item.add(new Label("name"));
                item.add(new Label("address"));
                item.add(new Label("city"));
                item.add(new Label("state"));
                item.add(new Label("country"));
                item.add(new Label("zip"));                
                item.add(new Link("view", item.getModel()) {
                    public void onClick() {
                        Hotel hotel = (Hotel) getModelObject();
                        setResponsePage(new HotelPage(hotel));
                    }
                });
            }
            @Override
            public boolean isVisible() {
                return !getList().isEmpty();
            }
        };

        hotelsContainer.add(new WebMarkupContainer("noResultsContainer") {
            @Override
            public boolean isVisible() {
                return !hotelsListView.isVisible();
            }
        });

        hotelsContainer.add(hotelsListView);
        hotelsContainer.add(new Link("moreResultsLink") {
            public void onClick() {
                BookingSession session = getBookingSession();
                session.setPage(session.getPage() + 1);
                loadHotels();
            }
            @Override
            public boolean isVisible() {
                return hotelsListView.getList().size() == getBookingSession().getPageSize();
            }
        });                

        if(getBookingSession().getBookings() == null) {
            loadBookings();
        }

        final PropertyListView bookingsListView = new PropertyListView("bookings") {
            protected void populateItem(ListItem item) {
                item.add(new Label("hotel.name"));
                item.add(new Label("hotel.address"));
                item.add(new Label("hotel.city"));
                item.add(new Label("hotel.state"));
                item.add(new Label("hotel.country"));
                item.add(new Label("checkinDate"));
                item.add(new Label("checkoutDate"));
                item.add(new Label("id"));
                item.add(new Link("cancel", item.getModel()) {                    
                    public void onClick() {
                        Booking booking = (Booking) getModelObject();
                        logger.info("Cancel booking: " + booking.getId() 
                                + " for " + getBookingSession().getUser().getUsername());
                        EntityManager em = getEntityManager();
                        Booking cancelled = em.find(Booking.class, booking.getId());
                        if (cancelled != null) {
                            em.remove(cancelled);
                            loadBookings();
                            getSession().info("Booking cancelled for confirmation number " + booking.getId());
                            setResponsePage(MainPage.class);
                        }
                    }
                });
            }
            @Override
            public boolean isVisible() {
                return !getList().isEmpty();
            }  
        };

        add(bookingsListView);

        add(new WebMarkupContainer("noBookingsContainer") {
            @Override
            public boolean isVisible() {
                return !bookingsListView.isVisible();
            }
        });
        
    }

    private class SearchForm extends Form implements IAjaxIndicatorAware {

        private ContextImage ajaxIndicator;

        public SearchForm(String id) {
            super(id);            
            TextField searchField = new TextField("searchString");
            add(searchField);
            searchField.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                protected void onUpdate(AjaxRequestTarget target) {
                    refreshHotelsContainer(target);
                }                
            });
            add(new DropDownChoice("pageSize", pageSizes));
            add(new AjaxButton("submit") {                
                protected void onSubmit(AjaxRequestTarget target, Form form) {                    
                    refreshHotelsContainer(target);
                }
            });
            ajaxIndicator = new ContextImage("ajaxIndicator", new Model("img/spinner.gif"));
            add(ajaxIndicator.setOutputMarkupId(true));
        }

        public String getAjaxIndicatorMarkupId() {
            return ajaxIndicator.getMarkupId();
        }

    }

    private void refreshHotelsContainer(AjaxRequestTarget target) {        
        getBookingSession().setPage(0);
        loadHotels();
        target.addComponent(hotelsContainer);        
    }

    private void loadHotels() {
        BookingSession session = getBookingSession();
        String searchString = session.getSearchString();
        String pattern = searchString == null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
        Query query = getEntityManager().createQuery("select h from Hotel h"
                + " where lower(h.name) like :pattern"
                + " or lower(h.city) like :pattern"
                + " or lower(h.zip) like :pattern"
                + " or lower(h.address) like :pattern");
        query.setParameter("pattern", pattern);
        query.setMaxResults(session.getPageSize());
        query.setFirstResult(session.getPage() * session.getPageSize());
        session.setHotels(query.getResultList());
    }
}

