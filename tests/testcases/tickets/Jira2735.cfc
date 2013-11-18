<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testCFhttpHead(){
		http method="head" result="result" url="#createURL("Jira2735/index.cfm")#" addtoken="false" resolveurl="false";
			
	}
	public void function testCFhttpHeadText(){
		http method="head" result="result" url="#createURL("Jira2735/index.cfm?text=true")#" addtoken="false" resolveurl="false";
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>