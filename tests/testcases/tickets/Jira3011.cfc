<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public void function testDirectoryExistsWithAbsolutePath(){
		assertEquals(true,directoryExists(getDirectoryFromPath(getCurrentTemplatePath())));
	}
} 
</cfscript>