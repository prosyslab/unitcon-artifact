import org.activiti.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.Test;

/* Duration of synthesis: 27.1068470478 */
public class UnitconTest2 {
@Test
public void test() {
try {
ExecutionEntity executionEntity2 = null;
InclusiveGatewayActivityBehavior con_recv1 = new InclusiveGatewayActivityBehavior();
con_recv1.executeInactive(executionEntity2);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
