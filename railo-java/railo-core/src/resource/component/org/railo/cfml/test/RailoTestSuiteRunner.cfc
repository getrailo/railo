/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
component extends="mxunit.framework.TestSuiteRunner" {

	/**
	* Primary method for running TestSuites and individual tests.
	* @allSuites a structure corresponding to the key/componentName
	* @results The TestResult collecting parameter.
	* @testMethod A single test method to run.
	*/
	public function run(
		struct allSuites,
		TestResult results=createObject("component","TestResult").TestResult(),
		string testMethod){

		var methodIndex = 1;
		systemOutput("+++xh",true,true);
		loop collection="#arguments.allSuites#" index="local.currentTestSuiteName" item="local.currentSuite" {
			local.testCase = createTestCaseFromComponentOrComponentName(currentSuite.ComponentObject);
			
			// set the MockingFramework if one has been set for the TestSuite
			if(len(variables.MockingFramework))
				testCase.setMockingFramework(variables.MockingFramework);
			
			// Invoke prior to tests. Class-level setUp
			if(testCase.okToRunBeforeTests()) {
				testCase.beforeTests();
				testCase.disableBeforeTests();
			}
			
			if(len(arguments.testMethod))
				runTestMethod(testCase, testMethod, results, currentTestSuiteName);
			else {
				loop from="1" to="#arrayLen(currentSuite.methods)#" index="methodIndex" {
					runTestMethod(testCase, currentSuite.methods[methodIndex], results, currentTestSuiteName);
				}
			}
			
			// Invoke after tests. Class-level tearDown 
			if(testCase.okToRunAfterTests()){
				testCase.afterTests();
				testCase.disableAfterTests();
			}
		}
		results.closeResults(); // Get correct time run for suite

		return results;
	}
}
