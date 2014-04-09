component extends="org.railo.cfml.test.RailoTestCase" {

	function giveIntegerPropertyAsIntegerTest () {
		local.result = CreateObject( 'webservice', createURL("Jira2978/service.cfc?wsdl")).acceptNestedArray({items:[{name:"item1",id:1},{name:"item2",id:2}]});

		assertEquals(expected:"item1",actual:local.result.getItems()[1].getName());
		assertEquals(expected:"1",actual:local.result.getItems()[1].getID());
		assertEquals(expected:"item2",actual:local.result.getItems()[2].getName());
		assertEquals(expected:"2",actual:local.result.getItems()[2].getID());
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}