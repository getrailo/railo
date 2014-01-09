<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public void function testSuppressOnAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2786/appcfc/Test.cfc?method=test&suppress=true")#" addtoken="false";
		assertEquals('{}',result.filecontent);
	}
	
	public void function testSuppressOffAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2786/appcfc/Test.cfc?method=test&suppress=false")#" addtoken="false";
		assertEquals('Body Content{}',result.filecontent);
	}
	
	public void function testSuppressOnTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2786/tag/Test.cfc?method=test&suppress=true")#" addtoken="false";
		assertEquals('{}',result.filecontent);
	}
	
	public void function testSuppressOffTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2786/tag/Test.cfc?method=test&suppress=false")#" addtoken="false";
		assertEquals('Body Content{}',result.filecontent);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>