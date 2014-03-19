<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}


	public void function testArrayMap() localMode="true" {
		_arrayMap(false);
	}

	public void function testArrayMapParallel() localMode="true" {
		_arrayMap(true);
	}

	private void function _arrayMap(boolean parallel) localMode="true" {
		arr=['a','b','c'];
		arr[5]='e';
		res=ArrayMap(arr, function( value ){
 							return value EQ 'b';
 
                        },parallel);

		assertEquals("false,true,false,,false",arrayToList(res));
		savecontent variable="c" {
			res=ArrayMap([1], function( value ){
							echo(serialize(arguments));
 							return value EQ 'b';
 
                        },parallel);
		}
		assertEquals("{'value':1,'2':1,'3':[1]}",c);
	}


	public void function testStructMap() localMode="true" {
		_structMap(false);
	}

	public void function testStructMapParallel() localMode="true" {
		_structMap(true);
	}

	private void function _structMap(boolean parallel) localMode="true" {
		sct=structNew("linked");
		sct.a=1;
		sct.b=2;
		sct.c=3;

		res=StructMap(sct, function(key, value ){
 							return key&":"&value;
 
                        },parallel);

		assertEquals("{'B':'B:2','A':'A:1','C':'C:3'}",serialize(res));
		savecontent variable="c" {
			res=StructMap({a:1}, function(key, value ){
							echo(serialize(arguments));
 							return key&":"&value;
 
                        },parallel);
		}
		assertEquals("{'key':'A','value':1,'3':{'A':1}}",c);
	}




	public void function testMap() localMode="true" {
		_map(false);
	}

	public void function testMapParallel() localMode="true" {
		_map(true);
	}

	private void function _map(boolean parallel) localMode="true" {
		arr=["a"];
		it=arr.iterator();



		res=Map(it, function(value ){
 							return value;
 
                        },parallel);

		assertEquals("['a']",serialize(res));
		
		it=arr.iterator();

		savecontent variable="c" {
			res=Map(it, function(value ){
							echo(serialize(arguments));
 							return value;
 
                        },parallel);
		}
		assertEquals("{'value':'a'}",c);
	}
} 
</cfscript>