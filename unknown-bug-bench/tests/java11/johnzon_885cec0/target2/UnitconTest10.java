import java.io.Writer;
import java.lang.Object;
import java.sql.DriverManager;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.websocket.internal.mapper.MapperLocatorDelegate;
import org.junit.Test;

/* Duration of synthesis: 18.3947608471 */
public class UnitconTest10 {
@Test
public void test() {
try {
Writer stream2 = DriverManager.getLogWriter();
Object object1 = new Object();
Mapper con_recv0 = MapperLocatorDelegate.locate();
con_recv0.writeObject(object1, stream2);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
