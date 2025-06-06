package spoon.test.loop;

import org.junit.Test;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.loop.testclasses.Condition;
import spoon.test.loop.testclasses.Join;
import spoon.testing.utils.ModelUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class LoopTest {
	private static final String nl = System.lineSeparator();

	// @Test
	// public void testAnnotationInForLoop() throws Exception {
	// 	CtType<?> aFoo = ModelUtils.build(new File("./src/test/resources/spoon/test/loop/testclasses/")).Type().get("spoon.test.loop.testclasses.Foo");

	// 	CtFor aFor = aFoo.getMethod("m").getElements(new TypeFilter<>(CtFor.class)).get(0);
	// 	assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(0)).getType().getAnnotations().size());
	// 	assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(1)).getType().getAnnotations().size());

	// 	CtForEach aForEach = aFoo.getMethod("m").getElements(new TypeFilter<>(CtForEach.class)).get(0);
	// 	assertEquals(1, aForEach.getVariable().getType().getAnnotations().size());
	// }

	// @Test
	// public void testForeachShouldHaveAlwaysABlockInItsBody() throws Exception {
	// 	final CtClass<Join> aType = build(Join.class, Condition.class).Class().get(Join.class);
	// 	final CtConstructor<Join> joinCtConstructor = aType.getConstructors().stream().findFirst().get();
	// 	final CtLoop ctLoop = joinCtConstructor.getBody().getElements(new TypeFilter<>(CtLoop.class)).get(0);
	// 	assertTrue(ctLoop.getBody() instanceof CtBlock);
	// 	String expected = //
	// 			"for (spoon.test.loop.testclasses.Condition<? super T> condition : conditions)" + nl //
	// 					+ "    this.conditions.add(spoon.test.loop.testclasses.Join.notNull(condition));" + nl;
	// 	assertEquals(expected, ctLoop.toString());
	// }
}
