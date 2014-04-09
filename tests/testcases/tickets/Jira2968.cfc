<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public void function testListGetAt(){
		var myList = "one hundred, two hundred, three hundred";
		assertEquals("one hundred",ListGetAt(myList, 1, ","));
		
	}
} 
</cfscript>