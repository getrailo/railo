<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public void function testWithContentTypeDeclaration(){
		http result="local.result" url="#createURL("Jira2715/index.cfm")#" method="post" {
			httpparam type="header" name="Content-Type" value="application/x-www-form-urlencoded";
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		assertEquals("bas,fieldnames,railo",listSort(structKeyList(sct),'textnocase'));
		assertEquals("1",sct.bas);
		assertEquals("2",sct.railo);
	}
	
	private void function testNoContentTypeDeclaration(){
		http result="local.result" url="#createURL("Jira2715/index.cfm")#" method="post" {
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		assertEquals("bas,fieldnames,railo",listSort(structKeyList(sct),'textnocase'));
		assertEquals("1",sct.bas);
		assertEquals("2",sct.railo);
	}
	
	private void function test(){
		http result="local.result" url="#createURL("Jira2715/reqhead.cfm")#" method="post" {
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		//dump(createURL("Jira2715/reqhead.cfm")); abort;
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>