<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test(){
		http method="get" result="local.result" url="#createURL("Jira2872/index.cfm")#" addtoken="false";
		assertEquals(200,result.status_code);
		assertEquals("",trim(result.fileContent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>