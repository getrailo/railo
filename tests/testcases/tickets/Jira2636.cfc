/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){

	}
	
	//public function afterTests(){}
	
	//public function setUp(){}
	
	public void function testRequiredArguments(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		try{
			test.base();
			fail("must throw something like: remote component throws the following error:The parameter b to function base is required but was not passed in. ");
		}
		catch(local.exp){}
		
		
	}
		


	public void function testArgumentsGeneral(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		var res=test.base(true);
		assertEquals(true,isStruct(res));
		assertEquals(false,isNull(res.b));
		assertEquals(true,isNull(res.n));
		
		var res=test.base(true,1);
		assertEquals(true,isStruct(res));
		assertEquals(false,isNull(res.b));
		assertEquals(false,isNull(res.n));
	}
	
	public void function testArgumentsAny(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		// boolean
		var res=test.object(true);
		assertEquals(true,isBoolean(res));
		assertEquals(true,res);
		
		// numeric
		var res=test.object(123);
		assertEquals(true,isNumeric(res));
		assertEquals(123,res);
		var res=test.object(123.456);
		assertEquals(true,isNumeric(res));
		assertEquals(123.456,res);
		
		// string
		local.textNonAscii=chr(228)&chr(25104)&chr(1604)&chr(1506);
		var res=test.object(textNonAscii);
		assertEquals(textNonAscii,res);
		
		// array
		local.arr=[1,2,3];
		var res=test.object(arr);
		assertEquals(serialize(arr),serialize(res));
		
		// struct
		local.sct={b:'abc'};
		var res=test.object(sct);
		assertEquals(serialize(sct),serialize(res));
		
		// query
		local.qry=query(a:[1,2,'abc']);
		var res=test.object(qry);
		assertEquals(serialize(qry),serialize(res));
		
		// component
		local.cfc=new Jira2636.Test();
		var res=test.object(cfc);
		assertEquals(serialize(cfc),serialize(res));
		
		//var res=test.object(this);
		//assertEquals(serialize(this),serialize(res));
	}
	
	public void function testArgumentsBoolean(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		var res=test.boolean(true);
		assertEquals(true,isBoolean(res));
		assertEquals(true,res);
	}
	
	public void function testArgumentsNumber(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		var res=test.number(123);
		assertEquals(true,isNumeric(res));
		assertEquals(123,res);
		var res=test.number(123.456);
		assertEquals(true,isNumeric(res));
		assertEquals(123.456,res);
	}
	
	public void function testArgumentsString(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		local.textNonAscii=chr(228)&chr(25104)&chr(1604)&chr(1506);
		var res=test.string(textNonAscii);
		assertEquals(textNonAscii,res);
		
	}
	
	public void function testArgumentsArray(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		local.arr=[1,2,3];
		var res=test.array(arr);
		assertEquals(serialize(arr),serialize(res));
		
	}
	
	public void function testArgumentsStruct(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		local.sct={b:'abc'};
		var res=test.struct(sct);
		assertEquals(serialize(sct),serialize(res));
		
	}
	
	public void function testArgumentsQuery(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		local.qry=query(a:[1,2,'abc']);
		var res=test.query(qry);
		assertEquals(serialize(qry),serialize(res));
		
	}
	
	public void function testArgumentsComponent(){
		local.test=createObject("http",createURL("Jira2636/Test.cfc"));
		
		local.cfc=new Jira2636.Test();
		var res=test.component(cfc);
		assertEquals(serialize(cfc),serialize(res));
		
		
	}
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
} 
</cfscript>