package junit.framework;

public interface TestListener {

	public void addError(Test test, Throwable t);
 	public void addFailure(Test test, AssertionFailedError t);  
 	public void endTest(Test test); 
	public void startTest(Test test);
}