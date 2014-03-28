component extends="org.railo.cfml.test.RailoTestCase" {

	function test () {
		local.members = CreateObject( 'webservice', createURL("Jira2961/service.cfc?wsdl")).getMembers().members;
		for(local.i = 1; i <= arrayLen(local.members) ; i++) {
			local.member = local.members[local.i];
			assertEquals(expected:"Member #local.i#",actual:member.name);
		}
	}
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}
