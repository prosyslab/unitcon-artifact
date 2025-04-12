import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.beanutils.ConfigurationDynaBean;
import org.junit.Test;

/* Duration of synthesis: 35.7916231155 */
public class UnitconTest15 {
@Test
public void test() {
try {
String name1 = "get(";
Map map42 = null;
Configuration configuration2 = new MapConfiguration(map42);
ConfigurationDynaBean con_recv0 = new ConfigurationDynaBean(configuration2);
con_recv0.get(name1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
