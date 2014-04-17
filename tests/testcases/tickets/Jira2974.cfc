<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	public void function testListEvery() localMode="true" {
		_listEvery(false);
	}


	public void function testListEveryParallel() localMode="true" {
		_listEvery(true);
	}

	private void function _listEvery(boolean parallel) localMode="true" {
		
		list=",,a,,b,c,,";
		//arr[5]='e';
		
		// base test
		res=ListEvery(list, function(value ){return true;},',',false,parallel);
		assertEquals(true,res);
		
		res=ListEvery(list, function(value ){return value =='b';},',',false,parallel);
		assertEquals(false,res);
		

		// closure output
		savecontent variable="c" {
			res=ListEvery(",a,,b,", function(){
							echo(serialize(arguments));
 							return true;
 
                        },',',false,parallel);
		}
		assertEquals("{'1':'a','2':1,'3':',a,,b,'}{'1':'b','2':2,'3':',a,,b,'}",c);

		savecontent variable="c" {
			res=ListEvery(",a,,b,", function(value){
							echo(">"&value);
 							return true;
 
                        },',',true,parallel);
		}
		assertEquals(">>a>>b>",c);

		savecontent variable="c" {
			res=ListEvery(",a,,b,", function(value){
							echo(">"&value);
 							return true;
 
                        },',',false,parallel);
		}
		assertEquals(">a>b",c);



		// member function
		res=List.Every(function(value ){return true;},',',false,parallel);
		assertEquals(true,res);


		res=List.Every(closure:function(value ){return true;},delimiter:',',includeEmptyFields:false,parallel:parallel);
		assertEquals(true,res);
	}
	

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


	public void function testQueryEvery() localMode="true" {
		_queryEvery(false);
	}


	public void function testQueryEveryParallel() localMode="true" {
		_queryEvery(true);
	}

	private void function _queryEvery(boolean parallel) localMode="true" {
		qry=query(a:["a1","a2"],b:["b1","b2"]);
		
		// base test
		res=QueryEvery(qry, function(){return true;},parallel);
		assertEquals(true,res);
		
		res=QueryEvery(qry, function(struct row, number rowNumber,query qry){return rowNumber == 2;},parallel);
		assertEquals(false,res);
		

		// closure output
		savecontent variable="c" {
			res=QueryEvery(qry, function(){
							echo(serialize(arguments));
 							return true;
 
                        },parallel);
		}
		assertEquals("{'1':{'b':'b1','a':'a1'},'2':1,'3':query('a':['a1','a2'],'b':['b1','b2'])}{'1':{'b':'b2','a':'a2'},'2':2,'3':query('a':['a1','a2'],'b':['b1','b2'])}",c);

		// member function
		res=qry.every(function(key,value ){return true;},parallel);
		assertEquals(true,res);
		
	}


	public void function testEvery() localMode="true" {
		arr=["a","b"];
		it=arr.iterator();

		res=collectionEvery(it, function(value ){return value =='b' || value== 'a';});
		assertEquals(true,res);
		
		it=arr.iterator();
		res=collectionEvery(it, function(value ){return value =='b';});
		assertEquals(false,res);
		
		it=arr.iterator();
		savecontent variable="c" {
			res=collectionEvery(it, function(){
							echo(serialize(arguments));
 							return true;
 
                        });
		}
		assertEquals("{'1':'a'}{'1':'b'}",c);
	}
} 
</cfscript>