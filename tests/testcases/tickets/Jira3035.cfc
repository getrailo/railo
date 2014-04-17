<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testWithoutAbort(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithoutAbort")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}
	public void function testWithAbort(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithAbort")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}


	public void function testWithReturnFormat(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithReturnFormat")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>