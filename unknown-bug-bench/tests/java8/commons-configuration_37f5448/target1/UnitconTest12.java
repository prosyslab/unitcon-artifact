import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationMap;
import org.apache.commons.configuration2.io.FileHandler;
import org.junit.Test;

/* Duration of synthesis: 24.0936739445 */
public class UnitconTest12 {
@Test
public void test() {
try {
Configuration configuration64 = null;
Map map1 = new ConfigurationMap(configuration64);
FileHandler.fromMap(map1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
