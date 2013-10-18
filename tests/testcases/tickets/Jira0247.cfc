<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function test(){
		local.second = createobject( "component", "Jira0247.second");
		assertEquals("default",second.check());

		second.init();
		assertEquals("inited",second.check());

		createObject( "component", "Jira0247.second" );
		assertEquals("inited",second.check());
		
		sct=getmetaData(second.check);
		assertEquals("check",sct.name);
	}
} 
</cfscript>