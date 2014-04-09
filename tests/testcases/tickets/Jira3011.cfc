<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public void function testDirectoryExistsWithAbsolutePath(){
		assertEquals(true,directoryExists(getDirectoryFromPath(getCurrentTemplatePath())));
	}
	public void function testDirectoryExistsWithAbsolutePathAndDotInName(){
		var base=getDirectoryFromPath(getCurrentTemplatePath());
		var dir=base&"3011.b.c/";
		assertEquals(false,directoryExists(dir));
		directoryCreate(dir);
		try{
		assertEquals(true,directoryExists(dir));
		}
		finally {
			directoryDelete(dir);
		}
		assertEquals(false,directoryExists(dir));
	}
} 
</cfscript>