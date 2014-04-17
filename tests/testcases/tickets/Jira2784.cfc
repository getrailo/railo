<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testCompressionOnAppCFC(){
		// compress is disabled when this is enabled
		if(getPageContext().getConfig().debugLogOutput()) return;

		http method="get" result="local.result" url="#createURL("Jira2784/appcfc/index.cfm?compression=true")#" addtoken="false";
		assertEquals(false,isNull(result.responseheader['Content-Encoding']));
		assertEquals('gzip',result.responseheader['Content-Encoding']);
	}
	
	public void function testCompressionOffAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2784/appcfc/index.cfm?compression=false")#" addtoken="false";
		assertEquals(true,isNull(result.responseheader['Content-Encoding']));
	}
	
	public void function testCompressionOnTagApp(){
		// compress is disabled when this is enabled
		if(getPageContext().getConfig().debugLogOutput()) return;

		http method="get" result="local.result" url="#createURL("Jira2784/tag/index.cfm?compression=true")#" addtoken="false";
		assertEquals(false,isNull(result.responseheader['Content-Encoding']));
		assertEquals('gzip',result.responseheader['Content-Encoding']);
	}
	
	public void function testCompressionOffTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2784/tag/index.cfm?compression=false")#" addtoken="false";
		assertEquals(true,isNull(result.responseheader['Content-Encoding']));
	}
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>