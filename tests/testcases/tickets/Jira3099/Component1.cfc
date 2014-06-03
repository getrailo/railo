component {

	variables.testValue = "Component1";

	public function testClosure(){
		
		local.com2 = new component2();
		local.com2.override = function() {			
			echo(serialize(listSort(structKeyList(this),"textnocase")));
			echo(serialize(listSort(structKeyList(variables.this),"textnocase")));
		}		
		local.com2.override();
	}

	public function testUDF(){
		
		local.com2 = new component2();
		local.com2.override = susi;
		local.com2.override();
		//dump(local.com2);	
	}

	private function susi(){			
		echo(serialize(listSort(structKeyList(this),"textnocase")));
		echo(serialize(listSort(structKeyList(variables.this),"textnocase"))); 
	}
}