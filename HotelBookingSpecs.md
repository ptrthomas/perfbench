# Overview #

The Hotel Booking application is a sample application that is part of the [Seam Framework](http://seamframework.org/) distribution.  A high level listing of features can be found [here](http://docs.jboss.org/seam/2.1.2/reference/en-US/html/tutorial.html#booking).

This document outlines the functionality, specifications and screen-by-screen break-down of the Hotel Booking application, which has been slightly adapted for [PerfBench](http://code.google.com/p/perfbench), and can be used as a reference for implementers as well as reviewers of the implementations.

# Reference Application #

  * the original application is the JPA version of the Seam Hotel Booking example: [browse](http://fisheye.jboss.org/browse/Seam/tags/JBoss_Seam_2_2_0_GA/examples/jpa) | [SVN](http://anonsvn.jboss.org/repos/seam/branches/community/Seam_2_2/examples/jpa/)
  * the "baseline" application is the above with minimal cosmetic changes to make it viable as a comparison point [browse](http://code.google.com/p/perfbench/source/browse/trunk/perfbench/seam-jpa) | [SVN](http://perfbench.googlecode.com/svn/trunk/perfbench/seam-jpa)
  * list of cosmetic changes made:
    * now using maven folder structure along with a maven project file (pom.xml)
    * removed "sidebar" and "conversations" facelets ui:define placeholder from template.xhtml
    * removed "sidebar" ui:define content from the view `*`.xhtml files where existing
    * removed conversations.xhtml
    * minor re-fomatting and indentation of XML config files, facelets views and java code
    * removed session-timeout config from web.xml
    * changed facelets.DEVELOPMENT param to false in web.xml
    * java code split into 2 packages: "seamjpa.entity" and "seamjpa.seam"
    * removed dead / commented code, split some HQL strings to wrap long lines of code for readability

# Rules #
  1. All screens and all actions (hyperlinks, buttons) of the baseline application should be implemented and be deployed even if not navigated to in the JMeter script
  1. The views should be migrated one-to-one, obviously merging of two screens into one or skipping a screen is not allowed
  1. Original naming conventions should be preserved as far as possible, but using framework conventions, for e.g. booking.xhtml becomes BookingPage.html and a package name example is "wicketjpa.entity"
  1. HTML and CSS should be logically identical to the baseline so as to replicate the look and feel.  Exceptions are where some static text is changed (e.g. built using Apache Wicket) and where the underlying framework injects JavaScript and modifies the HTML whitespace
  1. Exact same session usage should be simulated.  For e.g. if the Seam booking example pushes a list of search results into the HttpSession as a result of some action, the same should happen in Wicket.
  1. Exact same validation behavior should be simulated.  Form fields are flagged as "required" where applicable.  And for e.g. if the Seam booking example makes an Ajax request to the server to validate form input fields that the user is filling out, the same should be done in Wicket.
  1. In addition to the above point, when the user clicks the submit button and there are validation errors, the error messages should still be displayed alongside each form input field even though the page has not been refreshed using Ajax.
  1. Exact same JPA entities should be used
    * note: this has been relaxed for Grails and equivalent GORM entities have been instead
  1. Exact same database queries (JPA-QL) used be used.
  1. Hibernate Validator: the Seam booking example uses JPA entities annotated with Hibernate Validator annotations such as @Length and @NotNull, and form validation has to re-use these rules
    * note: this has been relaxed for Grails because of the use of GORM instead of JPA, so GORM constraints have been used instead of Hibernate Validator annotations.  However, the form validation re-uses the entity constraints as expected.
  1. The browser back-button should ideally work as expected, either the expected page is shown or a "page expired" error is displayed - although this is not verified as part of the JMeter script.
  1. A key aspect of the Hotel Booking use-case is that multiple booking flows (view hotel --> booking form --> booking confirm) can be in progress simultaneously for the same logged in user's session.  This should be implemented correctly, for e.g. if you open 2 "view hotel" pages for 2 different hotels in two browser tabs and proceed to book and confirm, you should get 2 bookings for 2 different hotels.
  1. Security: except for the "home" page and the "register" page all pages should be secured so that a user who has not logged in would be redirected to the "home" page.
  1. The default page for an authenticated user should be the "main" page
  1. Templating: except for the "home" page and the "register" page, all pages should use a common template where the header and footer areas have  been defined.
  1. Flash scope / messages: some of the actions in the original application result in informational messages being displayed to the user when the page changes (or refreshes) which should be implemented identically.  For example, if the Seam application displays a welcome message on the "main" page after a successful sign-on, the same should be done for Wicket.
  1. Logging: The log4j configuration should be logically identical to that of the baseline.  Also, some of the actions in the original application result in INFO / ERROR log messages to the console and these should be ported identically.
  1. WEB-INF/web.xml should be identical except for framework-specific configuration
    * note: this can't be practically done for Grails for obvious reasons
  1. Versions of Jetty, Hibernate and HSQLDB used for deployment when running the test should be the same
  1. Compile source and target should be JDK 1.6 (debug enabled).
  1. Run time environment should be identical, for e.g. as of now the JVM is started with the "-server" flag, and options: -Xms128m -Xmx128m.  The common Ant script and bootstrap routines available takes care of all of this.
  1. Exact same JPA configuration should be used (i.e. identical META-INF/persistence.xml)
    * note: this has been relaxed for Grails / GORM since there is no persistence.xml but the same Hibernate configuration is used in conf/DataSource.groovy
  1. Exact same initial database import (import.sql) should be used
    * note: slight changes for Grails because of the way GORM works, the only changes in this case are (a) Customer table renamed to User (b) "auto-increment" id column added to User table (c) "version" columns added to all 3 tables (User, Hotel, Booking)
  1. Currency values should be displayed formatted as US Dollars e.g. "$0.12" wherever applicable

# Screen Notes #
## Home ##
  * one of the 2 un-secured pages that does not use the common template
  * contains login form
  * invalid login should display an error message as well as log an error
  * has a link to the "register" page
  * successful login should display a welcome message on the next screen: "main"

## Register ##
  * One of the 2 un-secured pages that does not use the common template
  * contains a register form
  * form fields should have server-side validation over Ajax
  * if user-id exists should be validated
  * that the password and verify passwords match should be validated
  * successful registration should display an info message on the next screen: "home"

## (the Template) ##
  * once logged in, all pages have a common template (header, footer and css)
  * the header has links to go to the "main" and settings ("password") page
  * there is a third "logout" link which invalidates the http session and takes user back to the "home" page

## Main ##
  * contains a search form, and 2 tables for
    * hotel search results
    * bookings done by user sorted by most recent first
  * for both tables, the table header should be hidden in case there are no rows of data visible and instead, a "no {record-type}s found" message should be displayed
  * the search input text field should update the hotel search results over Ajax (autocomplete style) with every keystroke
  * there is a page-size drop-down (which defaults to 10) where the choices are 5, 10 and 20
  * if the search form submit button is used, it submits and refreshes the hotel search results over Ajax
  * whenever an Ajax refresh of the hotel search results is in progress, a visual indicator (spinner.gif) is to be momentarily displayed, the indicator is positioned next to the submit button after the input (search) text field
  * hotel search results are paginated and the current (session) page size is used to query the database
  * any update of the hotel search results is cached in the user's session
  * the search string, page size and current page (pagination) index are also cached in the session
  * when the user first hits this page, the list of bookings by current user are queried and displayed and also cached in the session
  * only a simple "next" pagination link is available which is visible only when current page item count == session page size
  * user can click to view a hotel and be taken to the "hotel" page
  * user can click to cancel a booking, after the database update, the list of bookings will be re-queried and the page refreshed with a message displayed
  * there is no pagination for the list of bookings displayed !

## Hotel ##
  * this is a read-only view of the hotel details
  * "Back to Search" button returns to the "main" page
  * "Book Hotel" button goes to the "book" page
  * both buttons are form submit buttons

## Book ##
  * this is the main booking form
  * refer code for the date validations
  * form fields should have server-side validation over Ajax
  * a date picker component should be available for the date fields
  * refer the code of the baseline application for the exact option values rendered in the select boxes / radio buttons on the form
  * "Cancel" button aborts the flow and takes user to "main" page
  * "Proceed" button shows entered data on the next "confirm" page
  * both buttons are form submit buttons

## Confirm ##
  * this is a read-only view of the user-entered booking details
  * "Revise" button shows the "book" page again retaining the values for editing in the form
  * "Cancel" button aborts the flow and takes user to "main" page
  * "Confirm" button inserts the new booking in the database, re-loads the list of bookings in the session and takes the user to the "main" page, a message is displayed as well as logged to the log
  * all three buttons are form submit buttons

## Password ##
  * this page is shown when the user clicks the "settings" link in the template header
  * this page just a form with just 2 fields for changing the logged-in user's password
  * form fields should have server-side validation over Ajax
  * that the password and verify passwords match should be validated
  * submitting using the "Change" button should update the user record in the database and return to the "main" page and a confirmation message should be displayed
  * "Cancel" button takes the user to the "main" page
  * both buttons are form submit buttons

# JMeter Test Plan Notes #
  * open the booking.jmx file in JMeter to see the structure [(example)](http://code.google.com/p/perfbench/source/browse/trunk/perfbench/seam-jpa/src/test/jmeter)
  * login and logout happens only once for each virtual user
  * the main "loop" is run 10 times (per user)
  * additional resources part of the web-pages (e.g CSS, JS and images) are ignored
  * JMeter scripts should of course be logically same for each: Seam, Wicket etc, including simulation of Ajax requests
  * the Ant script ensures that the home page is available before kicking off the JMeter script, which means that the Jetty start-up time does not impact results