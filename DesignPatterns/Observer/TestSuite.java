package junit.framework;

import java.util.Vector;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.*;


public class TestSuite implements Test {

	private Vector fTests= new Vector(10);
	private String fName;

	public TestSuite() {
	}

	 public TestSuite(final Class theClass) {
		fName= theClass.getName();
		Constructor constructor= null;
		try {	
			constructor= getConstructor(theClass);
		} catch (NoSuchMethodException e) {
			addTest(warning("Class "+theClass.getName()+" has no public constructor TestCase(String name)"));
			return;
		}

		if (!Modifier.isPublic(theClass.getModifiers())) {
			addTest(warning("Class "+theClass.getName()+" is not public"));
			return;
		}

		Class superClass= theClass;
		Vector names= new Vector();
		while (Test.class.isAssignableFrom(superClass)) {
			Method[] methods= superClass.getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				addTestMethod(methods[i], names, constructor);
			}
			superClass= superClass.getSuperclass();
		}
		if (fTests.size() == 0)
			addTest(warning("No tests found in "+theClass.getName()));
	}

	public TestSuite(String name) {
		fName= name;
	}

	public void addTest(Test test) {
		fTests.addElement(test);
	}

	public void addTestSuite(Class testClass) {
		addTest(new TestSuite(testClass));
	}

	private void addTestMethod(Method m, Vector names, Constructor constructor) {
		String name= m.getName();
		if (names.contains(name)) 
			return;
		if (isPublicTestMethod(m)) {
			names.addElement(name);

			Object[] args= new Object[]{name};
			try {
				addTest((Test)constructor.newInstance(args));
			} catch (InstantiationException e) {
				addTest(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
			} catch (InvocationTargetException e) {
				addTest(warning("Exception in constructor: "+name+" ("+exceptionToString(e.getTargetException())+")"));
			} catch (IllegalAccessException e) {
				addTest(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
			}

		} else {
			if (isTestMethod(m)) 
				addTest(warning("Test method isn't public: "+m.getName()));
		}
	}
	
	private String exceptionToString(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();
		
	}

	public int countTestCases() {
		int count= 0;
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
			Test test= (Test)e.nextElement();
			count= count + test.countTestCases();
		}
		return count;
	}

	private Constructor getConstructor(Class theClass) throws NoSuchMethodException {
		Class[] args= { String.class };
		return theClass.getConstructor(args);
	}

	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	 }

	private boolean isTestMethod(Method m) {
		String name= m.getName();
		Class[] parameters= m.getParameterTypes();
		Class returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	 }

	public void run(TestResult result) {
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
	  		if (result.shouldStop() )
	  			break;
			Test test= (Test)e.nextElement();
			runTest(test, result);
		}
	}
	
	public void runTest(Test test, TestResult result) {
		test.run(result);
	}
	
	public Test testAt(int index) {
		return (Test)fTests.elementAt(index);
	}

	public int testCount() {
		return fTests.size();
	}

	public Enumeration tests() {
		return fTests.elements();
	}

	public String toString() {
		if (getName() != null)
			return getName();
		return super.toString();
	 }
		 
	public void setName(String name) {
		fName= name;
	}

	public String getName() {
		return fName;
	}
	 
	 private Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};		
	}
}