package spoon.test.strings;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.factory.Factory;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.createFactory;

public class StringLiteralTest {
	// @SuppressWarnings("unused")
	// @Test
	// public void testSnippetFullClass() {
	// 	Factory factory = createFactory();
	// 	CtClass<?> clazz = factory.Code().createCodeSnippetStatement(
	// 			"class StringValueUTF {\n" +
	// 			"	String f0 = \"toto\";\n" +
	// 			"	String f1 = \"\\n\";\n" +
	// 			"	char c1 = '\\n';\n" +
	// 			"	String f2 = \"\\u20ac\";\n" +
	// 			"	char c2 = '\\u20ac';\n" +
	// 			"	String f3 = \"€\";\n" +
	// 			"	char c3 = '€';\n" +
	// 			"	String f4 = \"\\t\";\n" +
	// 			"	char c4 = '\\t';\n" +
	// 			"	String f5 = \"	\";\n" +
	// 			"	char c5 = '	';\n" +
	// 			"	String f6 = \"€\\u20ac\";\n" +
	// 			"}"
	// 	).compile();
	// 	CtField<?> f0 = (CtField<?>) clazz.getFields().toArray()[0];
	// 	CtField<?> f1 = (CtField<?>) clazz.getFields().toArray()[1];
	// 	CtField<?> c1 = (CtField<?>) clazz.getFields().toArray()[2];
	// 	CtField<?> f2 = (CtField<?>) clazz.getFields().toArray()[3];
	// 	CtField<?> c2 = (CtField<?>) clazz.getFields().toArray()[4];
	// 	CtField<?> f3 = (CtField<?>) clazz.getFields().toArray()[5];
	// 	CtField<?> c3 = (CtField<?>) clazz.getFields().toArray()[6];
	// 	CtField<?> f4 = (CtField<?>) clazz.getFields().toArray()[7];
	// 	CtField<?> c4 = (CtField<?>) clazz.getFields().toArray()[8];
	// 	CtField<?> f5 = (CtField<?>) clazz.getFields().toArray()[9];
	// 	CtField<?> c5 = (CtField<?>) clazz.getFields().toArray()[10];
	// 	CtField<?> f6 = (CtField<?>) clazz.getFields().toArray()[11];

	// 	assertEquals("java.lang.String f0 = \"toto\";", f0.toString());
	// 	assertEquals("java.lang.String f1 = \"\\n\";", f1.toString());
	// 	assertEquals("char c1 = '\\n';", c1.toString());
	// 	assertEquals("java.lang.String f2 = \"\\u20ac\";", f2.toString());
	// 	assertEquals("char c2 = '\\u20ac';", c2.toString());
	// 	assertEquals("java.lang.String f3 = \"€\";", f3.toString());
	// 	assertEquals("char c3 = '€';", c3.toString());
	// 	assertEquals("java.lang.String f4 = \"\\t\";", f4.toString());
	// 	assertEquals("char c4 = '\\t';", c4.toString());
	// 	assertEquals("java.lang.String f5 = \"	\";", f5.toString());
	// 	assertEquals("char c5 = '	';", c5.toString());

	// 	// spoon cannot handle unicode and unicode in the same literal
	// 	// assertEquals("java.lang.String f6 = \"€\\u20ac\";", f6.toString());
	// }

}
