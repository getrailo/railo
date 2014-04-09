component extends="org.railo.cfml.test.RailoTestCase" {



	function testChildWithExtends() {
		local.service = CreateObject( 'webservice', createURL("Jira2961/service.cfc?wsdl"));
		local.membersObj = CreateObject( 'webservice', createURL("Jira2961/service.cfc?wsdl")).getMembers();
		local.members=membersObj.members;
		
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
