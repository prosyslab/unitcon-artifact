import java.lang.Class;
import org.apache.karaf.shell.impl.action.command.ActionCommand;
import org.apache.karaf.shell.impl.action.command.ArgumentCompleter;
import org.apache.karaf.shell.impl.action.command.ManagerImpl;
import org.junit.Test;

/* Duration of synthesis: 34.3331198692 */
public class UnitconTest2 {
@Test
public void test() {
try {
boolean scoped2 = true;
Class actionClass4 = UnitconInterface.class;
ManagerImpl manager3 = null;
ActionCommand command1 = new ActionCommand(manager3, actionClass4);
new ArgumentCompleter(command1, scoped2);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
