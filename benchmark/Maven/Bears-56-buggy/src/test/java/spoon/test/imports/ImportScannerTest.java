package spoon.test.imports;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.reflect.visitor.MinimalImportScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;

import java.security.AccessControlException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

/**
 * Created by gerard on 14/10/2014.
 */
public class ImportScannerTest {

	@Test
	public void testComputeMinimalImportsInClass() throws Exception {
		String packageName = "spoon.test";
		String className = "SampleImportClass";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new MinimalImportScanner();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(theClass);

		assertTrue(imports.isEmpty());
	}

	@Test
	public void testComputeImportsInClass() throws Exception {
		String packageName = "spoon.test";
		String className = "SampleImportClass";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(theClass);

		// java.lang are also computed
		assertEquals(4, imports.size());
	}

	@Test
	public void testComputeImportsInClassWithSameName() throws Exception {
		String packageName = "spoon.test.imports.testclasses2";
		String className = "ImportSameName";
		String qualifiedName = packageName + "." + className;

		Factory aFactory = build(packageName, className).getFactory();
		CtType<?> theClass = aFactory.Type().get(qualifiedName);

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(theClass);

		assertEquals(0, imports.size());
	}


	@Test
	public void testMultiCatchImport() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/imports/testclasses/MultiCatch.java"));

		compiler.build();

		final List<CtClass<?>> classes = Query.getElements(factory, new NameFilter<CtClass<?>>("MultiCatch"));

		ImportScanner importScanner = new ImportScannerImpl();
		importScanner.computeImports(classes.get(0));
		// as ArithmeticException come from java.lang it is not imported anymore
		//assertTrue( importScanner.isImported( factory.Type().createReference( ArithmeticException.class ) ));
		assertTrue( importScanner.isImported( factory.Type().createReference( AccessControlException.class ) ));
	}

	// @Test
	// public void testTargetTypeNull() throws Exception {
	// 	Launcher spoon = new Launcher();
	// 	Factory factory = spoon.createFactory();
	// 	CtFieldReference fieldRef = factory.createFieldReference();
	// 	fieldRef.setStatic(true);

	// 	ImportScanner importScanner = new MinimalImportScanner();
	// 	importScanner.computeImports(fieldRef);
	// }
}
