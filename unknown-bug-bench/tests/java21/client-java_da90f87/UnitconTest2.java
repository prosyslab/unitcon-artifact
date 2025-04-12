import io.kubernetes.client.Exec.ExecutionBuilder;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import java.lang.String;
import org.junit.Test;

/* Duration of synthesis: 255.174407005 */
public class UnitconTest2 {
@Test
public void test() {
try {
String name2 = "false";
String namespace1 = "false";
String[] command3 = null;
ApiClient apiClient4 = null;
Exec con_recv1 = new Exec(apiClient4);
Exec.ExecutionBuilder con_recv0 = con_recv1.newExecutionBuilder(namespace1, name2, command3);
con_recv0.execute();
}
catch(Exception e) {
e.printStackTrace();
}
}
}
