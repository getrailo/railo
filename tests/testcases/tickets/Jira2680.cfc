<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function test(){
		http method="post" result="local.result" url="#createURL("Jira2680/test1.cfm")#" addtoken="false" {
			// Body httpparam type="body" name="test" value="";
			httpparam type="formfield" name="bas[num]" value="test";
			httpparam type="formfield" name="bas2[]" value="test";
		}
		sct=evaluate(trim(result.filecontent));
		assertEquals(true,structKeyExists(sct,'bas[num]'));
		assertEquals(true,structKeyExists(sct,'bas2'));
		assertEquals(true,isArray(sct.bas2));
		
	}
	public void function testSquareBracket(){
		http method="post" result="local.result" url="#createURL("Jira2680/test2.cfm")#" addtoken="false" {
			// Body httpparam type="body" name="test" value="";
			httpparam type="formfield" name="susi" value="test";
			httpparam type="formfield" name="bas[num]" value="test";
		}
		assertEquals(200,result.status_code);
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>