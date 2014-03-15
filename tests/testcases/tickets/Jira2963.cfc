component extends="org.railo.cfml.test.RailoTestCase" hint="Tests calling a web service that accepts a typed array,
			which would error." {

	function test () {
		local.accept = CreateObject( 'webservice', createURL("Jira2963/service.cfc?wsdl")).myFunction([{name:"1"}])
	}
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}
