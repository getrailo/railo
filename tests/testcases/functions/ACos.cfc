<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public void function testACos(){
		assertEquals(0,acos(1));
		assertEquals("0.795398830184",tostring(acos(0.7)));
		
		try{
			assertEquals(1,tostring(acos(1.7)));
			fail("must throw:1.7 must be within range: ( -1 : 1 )");
		}
		catch(local.exp){}
	}
} 
</cfscript>