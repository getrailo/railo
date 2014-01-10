<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.sDate = '13-11-2-13';
			
	}
	
	
	public void function testLSIsDate(){
		assertEquals(false,lsIsDate(sDate,'Dutch (Standard)'));
	}
	private void function testLSParseDateTime(){
		try{
			lsParseDateTime(sDate,'Dutch (Standard)');
			// error
			fail("must throw:can't cast [13-11-2-13] to date value");
		}
		catch(local.exp){}
	}
} 
</cfscript>