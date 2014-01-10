<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testCompressionNoAttr(){
		http method="get" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		assertEquals("gzip",req.headers['Accept-Encoding']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	public void function testCompressionNone(){
		http method="get" compression="none" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		
		assertEquals("deflate;q=0",req.headers['Accept-Encoding']);
		assertEquals("deflate;q=0",req.headers['TE']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	
	public void function testCompressionTrue(){
		http method="get" compression="true" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		assertEquals("gzip",req.headers['Accept-Encoding']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	
	public void function testCompressionFalse(){
		http method="get" compression="false" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		
		assertEquals("deflate;q=0",req.headers['Accept-Encoding']);
		assertEquals("deflate;q=0",req.headers['TE']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>