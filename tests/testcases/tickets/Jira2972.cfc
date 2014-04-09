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


	public void function testListReduce() localMode="true" {
		
		list=",,a,,b,c,,";
		
		// base test
		res=ListReduce(list, function( result,value,index){
 							return result&";"&index&":"&value;
 
                        },"merge:");
		assertEquals("merge:;1:a;2:b;3:c",res);


		// closure output
		savecontent variable="c" {
			res=ListReduce("a,b", function( result,value,index ){
							echo(serialize(arguments));
 							return "";
 
                        },"merge:");
		}
		assertEquals("{'result':'merge:','value':'a','index':1,'4':'a,b'}{'result':'','value':'b','index':2,'4':'a,b'}",c);

		// member function
		res=list.Reduce(function( result,value,index){return result&";"&index&":"&value;},"merge:");
		assertEquals("merge:;1:a;2:b;3:c",res);
	}

	public void function testStructReduce() localMode="true" {
		
		sct={a:1,b:2,c:3};
		//arr[5]='e';
		
		// base test
		res=StructReduce(sct, function( result,key,value){
 							return result&";"&key&":"&value;
 
                        },"merge:");
		assertEquals("merge:;B:2;A:1;C:3",res);

		// closure output
		savecontent variable="c" {
			res=StructReduce({a:1}, function( result,key,value ){
							echo(serialize(arguments));
 							return "";
 
                        },"merge:");
		}
		assertEquals("{'result':'merge:','key':'A','value':1,'4':{'A':1}}",c);

		// member function
		res=sct.reduce(function( result,key,value){
 							return result&";"&key&":"&value;
 
                        },"merge:");
		assertEquals("merge:;B:2;A:1;C:3",res); 
	}

	public void function testQueryReduce() localMode="true" {
		qry=query(a:["a1","a2"],b:["b1","b2"]);
		
		// base test
		res=QueryReduce(qry, function( result,row,rowNbr){
 							return result&";"&row.a&"->"&rowNbr;
 
                        },"merge:");
		assertEquals("merge:;a1->1;a2->2",res);

		return;

		// closure output
		savecontent variable="c" {
			res=QueryReduce({a:1}, function( result,key,value ){
							echo(serialize(arguments));
 							return "";
 
                        },"merge:");
		}
		assertEquals("{'result':'merge:','key':'A','value':1,'4':{'A':1}}",c);

		// member function
		res=qry.reduce(function( result,key,value){
 							return result&";"&key&":"&value;
 
                        },"merge:");
		assertEquals("merge:;B:2;A:1;C:3",res); 
	}


	public void function testReduce() localMode="true" {
		arr=["a","b"];
		it=arr.iterator();



		res=collectionReduce(it, function(res,value ){
 							return res&";"&value;
 
                        },"merge:");

		assertEquals("'merge:;a;b'",serialize(res));
		
		it=arr.iterator();

		savecontent variable="c" {
			res=collectionReduce(it, function(res,value ){
							echo(serialize(arguments));
 							return res&";"&value;
 
                        },"merge:");
		}
		assertEquals("{'res':'merge:','value':'a'}{'res':'merge:;a','value':'b'}",c);
	}
} 
</cfscript>