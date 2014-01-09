<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

		
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
</cfscript>