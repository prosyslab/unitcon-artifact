import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.statics.LONG_Upper;
import org.junit.Test;

/* Duration of synthesis: 28.4249339104 */
public class UnitconTest2 {
@Test
public void test() {
try {
short kind5 = 183;
boolean useInterface6 = false;
String className1 = "root";
Type[] argTypes4 = null;
ClassGen cg23 = null;
ConstantPoolGen cp24 = null;
InstructionFactory con_recv1 = new InstructionFactory(cg23, cp24);
Type retType3 = LONG_Upper.theInstance();
con_recv1.createInvoke(className1, className1, retType3, argTypes4, kind5, useInterface6);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
