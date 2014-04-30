component extends="org.railo.cfml.test.RailoTestCase" {

	function giveIntegerPropertyAsIntegerTest () {
		local.thingWithDate = CreateObject( 'webservice', createURL("Jira3044/service.cfc?wsdl")).returnThingWithDate();
		local.struct = {key1:2, key2: local.thingwithdate.number, key3:local.thingwithdate.date}
	
		myStruct.sortedKeys = StructSort(local.struct , 'numeric');
		myStruct.sortedKeysUsingMember = local.struct.sort( 'numeric' );
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}