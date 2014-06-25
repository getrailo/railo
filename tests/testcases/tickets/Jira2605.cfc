<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public void function test(){
		http method="get" result="local.result" url="#createURL("Jira2605/index.cfm")#" addtoken="false";
		//echo((result.filecontent));
		assertEquals(200,result.status_code);
		assertEquals("3645",result.filecontent);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>