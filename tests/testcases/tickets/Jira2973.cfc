<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}


	public void function testArrayReduce() localMode="true" {
		
		arr=['a','b','c'];
		//arr[5]='e';
		
		// base test
		res=ArrayReduce(arr, function( result,value,index){
 							return result&";"&index&":"&value;
 
                        },"merge:");
		assertEquals("merge:;1:a;2:b;3:c",res);

		// closure output
		savecontent variable="c" {
			res=ArrayReduce(['a'], function( result,value,index ){
							echo(serialize(arguments));
 							return "";
 
                        },"merge:");
		}
		assertEquals("{'result':'merge:','value':'a','index':1,'4':['a']}",c);

		// member function
		res=arr.reduce(function( result,value,index){
 							return result&";"&index&":"&value;
 
                        },"merge:");
		assertEquals("merge:;1:a;2:b;3:c",res);
	}

	public void function testStructReduce() localMode="true" {
		
		sct={a:1,b:2,c:3};
		//arr[5]='e';
		
		// base test
		res=StructSome(sct, function(key,value ){return key =='b';});
		assertEquals(true,res);
		
		res=StructSome(sct, function(key,value ){return key =='d';});
		assertEquals(false,res);
		

		// closure output
		savecontent variable="c" {
			res=Some({a:1}, function(){
							echo(serialize(arguments));
 							return false;
 
                        });
		}
		assertEquals("{'1':'A','2':1,'3':{'A':1}}",c);

		savecontent variable="c" {
			res=StructReduce({a:1}, function( result,key,value ){
							echo(serialize(arguments));
 							return "";
 
                        },"merge:");
		}
		assertEquals("{'result':'merge:','key':'A','value':1,'4':{'A':1}}",c);

		// member function
		res=sct.some(function(key,value ){return key =='b';});
		assertEquals(true,res);
		
	}


	public void function testSome() localMode="true" {
		arr=["a","b"];
		it=arr.iterator();

		res=Some(it, function(value ){return value =='b';});
		assertEquals(true,res);
		
		it=arr.iterator();
		res=Some(it, function(value ){return value =='c';});
		assertEquals(false,res);
		
		it=arr.iterator();
		savecontent variable="c" {
			res=Some(it, function(){
							echo(serialize(arguments));
 							return false;
 
                        });
		}
		assertEquals("{'1':'a'}{'1':'b'}",c);
	}
} 
</cfscript>