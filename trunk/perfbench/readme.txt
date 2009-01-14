PerfBench (details of sub-projects appear below)
================================================================================

1) jmeter-utils: 

Code in this sub-project is released under the Apache 2.0 license.

Refer: http://www.apache.org/licenses/LICENSE-2.0.html

--------------------------------------------------------------------------------

2) seam-jpa: 

Code in this sub-project is adapted from the "jpa" example 
in the Seam Framework distribution and is released here 
under the same license terms.

changes made:

- now using maven folder structure along with a maven project file (pom.xml)
- removed "sidebar" and "conversations" facelets ui:define placeholder from template.xhtml
- removed "sidebar" ui:define content from the view *.xhtml files where existing
- removed conversations.xhtml
- minor re-fomatting and indentation of XML config files, facelets views and java code
- removed session-timeout config from web.xml
- java code split into 2 packages: seamjpa.entity and seamjpa.seam
- removed dead / commented code, split some HQL strings to wrap long lines of code for readability


--------------------------------------------------------------------------------

3) wicket-jpa:

Code in this sub-project is adapted from the "jpa" and "wicket" examples 
in the Seam Framework distribution and is released here
under the same license terms.

The JPA entities Booking.java, Hotel.java and User.java are identical to what is used
in the seam-jpa source - except for removal of seam "Name" and "Scope" annotations 
and the different package name.


================================================================================
Information on open source projects from which some of the code in this
"PerfBench" project have been derived:
================================================================================

a) The Seam Framework is distributed under the LGPL 2.1 license.

For more information, please refer http://www.gnu.org/licenses/lgpl.html

and the home of the Seam Framework at: http://www.seamframework.org


b) The lib/maven-ant-tasks.jar binary is from the Maven project: http://maven.apache.org

and is disributed under the Apache 2.0 license: http://maven.apache.org/license.html

--------------------------------------------------------------------------------

For any queries related to PerfBench - please contact the author using the 
open-source project hosting services.at http://code.google.com/p/perfbench/





    
    