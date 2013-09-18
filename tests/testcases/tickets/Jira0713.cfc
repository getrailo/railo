<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		cfc=createObject( "component", "Jira0713.Test").init();
	}

	public void function test(){
		
		assertEquals("Brett",cfc.getName());
		cfcc=duplicate(cfc);
		assertEquals("Brett",cfcc.getName());
		cfcc.setName('qqq');
		assertEquals("qqq",cfcc.getName());
	}
} 
</cfscript>