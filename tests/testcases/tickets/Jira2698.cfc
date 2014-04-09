component extends='org.railo.cfml.test.RailoTestCase' {
	public void function trySavingLargeNestedStruct() {
		// Prove that it doesn't happen with nested structures
		var nestedStruct = {};
		var nestInMe = nestedStruct;
		// Make a big struct
		var nestedStruct = {};
		var v = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];
		for (var i in v) {
			for (var j in v) {
				for (var k in v) {
					for (var l in v) {
						nestedStruct[i][j][k][l] = {};
					}
				}

			}
		}
		debug('Nested struct len = '&len(serialize(nestedStruct)));
		ObjectSave(nestedStruct);
		debug('Nested struct saved without error');
	}
	
	
	public void function triggerUTFDataFormatException() {
		// Prove that it happens with objects nested deeply
		objTest = new Jira2698.TestObject( 500 );
		var res=ObjectSave(objTest);
		ObjectLoad(res);
	}
	
}