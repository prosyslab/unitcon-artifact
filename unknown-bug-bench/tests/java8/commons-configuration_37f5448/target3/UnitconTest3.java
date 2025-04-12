import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.junit.Test;

/* Duration of synthesis: 19.26957798 */
public class UnitconTest3 {
@Test
public void test() {
try {
Configuration configuration1 = new DatabaseConfiguration();
ConfigurationUtils.toString(configuration1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
