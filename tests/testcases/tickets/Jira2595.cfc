<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function test(){
		//get original settings
		var beforeTriggerDataMember=getapplicationSettings().triggerDataMember;
		
		// now change the setting
		application action="update" triggerDataMember="#true#";
		
		try{
			t = new Jira2595.test();
			t.foo = 123;
			assertEquals("FOO",structKeyList(t));
		}
		finally {
			// reset to starting setting
			if(!beforeTriggerDataMember)
				application action="update" triggerDataMember="#false#";
		
		}
	}
} 
</cfscript>