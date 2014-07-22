<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	// calling via http is necessary because we have a template exception
	public void function testTagTryCatchFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?tagtcf=true")#" addtoken="false";
		assertEquals("tag:in finally;",trim(result.filecontent));
	}
	public void function testTagTryFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?tagtf=true")#" addtoken="false";
		assertEquals("tag:in finally;",trim(result.filecontent));
	}
	public void function testScriptTryCatchFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?scripttcf=true")#" addtoken="false";
		assertEquals("script:in finally;",trim(result.filecontent));
	}
	public void function testScriptTryFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?scripttf=true")#" addtoken="false";
		assertEquals("script:in finally;",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>