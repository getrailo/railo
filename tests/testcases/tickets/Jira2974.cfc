<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}
	public void function testArrayEvery() localMode="true" {
		_arrayEvery(false);
	}


	public void function testArrayEveryParallel() localMode="true" {
		_arrayEvery(true);
	}

	private void function _arrayEvery(boolean parallel) localMode="true" {
		
		arr=['a','b','c'];
		//arr[5]='e';
		
		// base test
		res=ArrayEvery(arr, function(value ){return true;},parallel);
		assertEquals(true,res);
		
		res=ArrayEvery(arr, function(value ){return value =='b';},parallel);
		assertEquals(false,res);
		

		// closure output
		savecontent variable="c" {
			res=ArrayEvery(['a'], function(){
							echo(serialize(arguments));
 							return true;
 
                        },parallel);
		}
		assertEquals("{'1':'a','2':1,'3':['a']}",c);

		// member function
		res=arr.every(function(value ){return true;},parallel);
		assertEquals(true,res);
	}


	public void function testStructEvery() localMode="true" {
		_structEvery(false);
	}


	public void function testStructEveryParallel() localMode="true" {
		_structEvery(true);
	}

	private void function _structEvery(boolean parallel) localMode="true" {
		
		sct={a:1,b:2,c:3};
		//arr[5]='e';
		
		// base test
		res=StructEvery(sct, function(key,value ){return true;},parallel);
		assertEquals(true,res);
		
		res=StructEvery(sct, function(key,value ){return key =='b';},parallel);
		assertEquals(false,res);
		

		// closure output
		savecontent variable="c" {
			res=StructEvery({a:1}, function(){
							echo(serialize(arguments));
 							return true;
 
                        },parallel);
		}
		assertEquals("{'1':'A','2':1,'3':{'A':1}}",c);

		// member function
		res=sct.every(function(key,value ){return true;},parallel);
		assertEquals(true,res);
		
	}


	public void function testEvery() localMode="true" {
		arr=["a","b"];
		it=arr.iterator();

		res=Every(it, function(value ){return value =='b' || value== 'a';});
		assertEquals(true,res);
		
		it=arr.iterator();
		res=Every(it, function(value ){return value =='b';});
		assertEquals(false,res);
		
		it=arr.iterator();
		savecontent variable="c" {
			res=Every(it, function(){
							echo(serialize(arguments));
 							return true;
 
                        });
		}
		assertEquals("{'1':'a'}{'1':'b'}",c);
	}
} 
</cfscript>