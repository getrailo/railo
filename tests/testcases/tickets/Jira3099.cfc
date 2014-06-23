<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testUDF(){
		http method="get" result="local.result" url="#createURL("Jira3099/index.cfm?type=udf")#" addtoken="false";
		assertEquals("'OVERRIDE''OVERRIDE'",trim(result.filecontent));
	}
	private void function testClosure(){
		http method="get" result="local.result" url="#createURL("Jira3099/index.cfm?type=closure")#" addtoken="false";
		assertEquals("'OVERRIDE''OVERRIDE'",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>