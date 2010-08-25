package org.eclipse.buckminster.git.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(VersionFinderTest.class);
		suite.addTestSuite(MaterializerTest.class);
		//$JUnit-END$
		return suite;
	}

}
