<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test(){
		http method="get" result="local.result" url="#createURL("Jira2275/index.cfm")#" addtoken="false";
		
		assertEquals("DISPLAY,FIELD_ID,INFOCARD_ID,TYPE",trim(result.filecontent));
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>