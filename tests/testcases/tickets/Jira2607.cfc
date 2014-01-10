<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function testLocal(){
		setting showdebugoutput="false";
		local.local1 = "test";
		local.fn = function() {
			assertEquals("test",local1);
		};
        local.fn();
	}

	public void function testArguments(){
		_testArguments("test");
	}
	
	private void function _testArguments(arg1){
		local.fn = function() {
			assertEquals("test",arg1);
		};
        local.fn();
	}
	
	

	public void function testVariables(){
		variables._test2607="test";
		local.fn = function() {
			assertEquals("test",_test2607);
		};
        local.fn();
	}
} 
</cfscript>