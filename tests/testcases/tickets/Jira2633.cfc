<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}
	
	public void function testVariablesSister(){
		rtn=new Jira2633.Test().getSisterVariables();
		rtn=rtn();
		
		assertEquals("2633",rtn.c); // local test (inside the outer closure)
		
		rtn.a();
		rtn.c=rtn.b();
		assertEquals("2633",rtn.c);
	}
	
	
	public void function testThisSister(){
		rtn=new Jira2633.Test().getSisterThis();
		rtn=rtn();
		
		assertEquals("2633",rtn.c); // local test (inside the outer closure)
		
		rtn.a();
		rtn.c=rtn.b();
		assertEquals("2633",rtn.c);
	}
	
	public void function testUndefinedSister(){
		rtn=new Jira2633.Test().getSisterUndefined();
		rtn=rtn();
		
		assertEquals("2633",rtn.c); // local test (inside the outer closure)
		
		rtn.a();
		rtn.c=rtn.b();
		assertEquals("2633",rtn.c);
	}
	
	
	
	
	
	
	
	
	
	
	
	public void function testVariablesLevel1(){
		c=new Jira2633.Test().getVariables();
		assertEquals("test->variables",c());
		
	}
	
	public void function testVariablesLevel2(){
		c=new Jira2633.Test().getVariables(2);
		c=c();
		assertEquals("test->variables",c());
	}
	
	
	public void function testThisLevel1(){
		c=new Jira2633.Test().getThis();
		assertEquals("test->this",c());
	}
	
	public void function testThisLevel2(){
		c=new Jira2633.Test().getThis(2);
		c=c();
		assertEquals("test->this",c());
	}
	
	public void function testUndefinedLevel1(){
		c=new Jira2633.Test().getUndefined();
		assertEquals("test->local",c());
	}
	
	public void function testUndefinedLevel2(){
		c=new Jira2633.Test().getUndefined(2);
		c=c();
		assertEquals("test->local",c());
	}
} 
</cfscript>