<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		result=httpCall(calledName:"Jira2588/index.cfm",method:"post",body:'{"foo":"bar"}');
		body=evaluate(trim(result.filecontent));
	}

	public void function testHTTPReqData(){
		assertEquals('{"foo":"bar"}',body.HTTPReqData.content);
	}
	
	public void function testForm(){
		assertEquals('',structKeyList(body.FORM));
	}
		
	private struct function httpCall(string calledName, string method='get',boolean addtoken=false, body){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		http method="#arguments.method#" result="local.result" url="#baseURL##arguments.calledName#" addtoken="#arguments.addtoken#" {
			if(!isNull(arguments.body))httpparam type="body" name="test" value="#arguments.body#";
		}
		return result;
	}
	
} 
</cfscript>