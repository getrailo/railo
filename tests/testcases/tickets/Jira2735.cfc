<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testCFhttpHead(){
		http method="head" result="result" url="#createURL("Jira2735/index.cfm")#" addtoken="false" resolveurl="false" {
			// Body httpparam type="body" name="test" value="";
			// form field httpparam type="formfield" name="test" value="";
		}
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>