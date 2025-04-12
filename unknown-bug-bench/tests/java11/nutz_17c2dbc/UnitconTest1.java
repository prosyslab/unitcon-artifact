import java.lang.Object;
import java.lang.reflect.Type;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.injector.JsonInjector;
import org.junit.Test;

/* Duration of synthesis: 24.4817230701 */
public class UnitconTest1 {
@Test
public void test() {
try {
Object refer4 = null;
String name20 = "";
Type type19 = null;
ServletContext sc1 = Mvcs.getServletContext();
HttpServletRequest req2 = Mvcs.getReq();
HttpServletResponse resp3 = Mvcs.getResp();
JsonInjector con_recv0 = new JsonInjector(type19, name20);
con_recv0.get(sc1, req2, resp3, refer4);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
