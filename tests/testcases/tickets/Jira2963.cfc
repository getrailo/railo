component extends="org.railo.cfml.test.RailoTestCase" hint="Tests calling a web service that accepts a typed array,
			which would error." {

	function testSendMyItemStruct() {
		local.accept = CreateObject( 'webservice', createURL("Jira2963/service.cfc?wsdl")).send({name:"1"})
	}
	function testSendMyItemStructArray() {
		local.accept = CreateObject( 'webservice', createURL("Jira2963/service.cfc?wsdl")).sendArray([{name:"1"}])
	}
	function testSendMyItemStructArrayArray() {
		local.accept = CreateObject( 'webservice', createURL("Jira2963/service.cfc?wsdl")).sendArrayArray([[{name:"1"}]])
	}
	private function testEchoArray() {
		local.accept = CreateObject( 'webservice', createURL("Jira2963/service.cfc?wsdl")).echoArray([{name:"1"}])
	}



	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}
