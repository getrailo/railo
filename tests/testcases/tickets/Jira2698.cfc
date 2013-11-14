component extends='mxunit.framework.TestCase' {
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
	
	public void function loadSerialisationFromOlderVersionNot412002() {
		// Prove that it happens with objects nested deeply
		var b64="rO0ABXNyABtyYWlsby5ydW50aW1lLkNvbXBvbmVudEltcGz8l2NuBRYqxAwAAHhyACVyYWlsby5ydW50aW1lLnR5cGUudXRpbC5TdHJ1Y3RTdXBwb3J0ZymyiRg5heMCAAB4cHdEAEJldmFsdWF0ZUNvbXBvbmVudCgnVGVzdCcsJ2ZjZDI2ZDI5NTFmODhiMzJjMDAxNGViMmQ0YmMyNWQ0Jyx7fSx7fSl4";
		var bin=toBinary(b64);
		try{
			ObjectLoad(bin);
			fail("must fail because Test does not exist");
		}
		catch(local.e){
			assertEquals(true,findNoCase("can't find Test",e.message)GT 0);
		}
	}
	public void function loadSerialisationFromOlderVersion412002() {
		// Prove that it happens with objects nested deeply
		var b64="rO0ABXNyABtyYWlsby5ydW50aW1lLkNvbXBvbmVudEltcGxu6FC7O+EFyAwAAHhyACVyYWlsby5ydW50aW1lLnR5cGUudXRpbC5TdHJ1Y3RTdXBwb3J0ZymyiRg5heMCAAB4cHdPZXZhbHVhdGVDb21wb25lbnQoJ2ptLmppcmEudGVzdC5UZXN0JywnZmNkMjZkMjk1MWY4OGIzMmMwMDE0ZWIyZDRiYzI1ZDQnLHt9LHt9KXg=";
		var bin=toBinary(b64);
			debug("only this version is not supported");
			ObjectLoad(bin);
			try{fail("must fail because Test does not exist");
		}
		catch(local.e){
			assertEquals(true,findNoCase("can't find Test",e.message)GT 0);
		}
	}
	
	
}