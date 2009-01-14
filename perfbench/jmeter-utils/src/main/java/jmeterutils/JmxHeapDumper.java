package jmeterutils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

// http://www.mhaller.de/archives/85-Java-heap-dumps.html
public class JmxHeapDumper {

    public static void main(String[] args) throws Exception {
        String url = "service:jmx:rmi:///jndi/rmi://localhost:9004/jmxrmi";
        JMXServiceURL jmxURL = new JMXServiceURL(url);
        JMXConnector connector = JMXConnectorFactory.connect(jmxURL);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        String hotSpotDiagName = "com.sun.management:type=HotSpotDiagnostic";
        ObjectName name = new ObjectName(hotSpotDiagName);
        String operationName = "dumpHeap";
        Object[] params = new Object[] { args[0], Boolean.TRUE };
        String[] signature = new String[] { String.class.getName(), boolean.class.getName() };
        connection.invoke(name, operationName, params, signature);
        System.out.println("*** heap dumped to: " + args[0]);
    }

}
