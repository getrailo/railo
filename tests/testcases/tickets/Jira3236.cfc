<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	variables.base=getDirectoryFromPath(getCurrentTemplatePath())&"Jira3236/";
	variables.src=variables.base&"src/";
	variables.trg=variables.base&"trg/";


	public void function test(){
		directoryCopy( variables.src, variables.trg, true, function( path ){
				dump(arguments.path);
				return path.find("tmp1"); // only copy the "tmp1" path
							
			});

		assertTrue(arrayToList(directoryList(variables.trg)).find("tmp2")==0);

	}
} 
</cfscript>