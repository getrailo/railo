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
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	private function testArgs(arg1, arg2, arg3='default', arg4) {
		return arguments;
	}

	public void function testPositionalArguments(){
		if(hasFullNullSupport()) throw "test need full null support disabled";
		var res=testArgs("test","test");
		//dump(var:res,label:'p1');
		assertTrue(structKeyexists(res,"arg1"));
		assertTrue(structKeyexists(res,"arg2"));
		assertTrue(structKeyexists(res,"arg3"));
		assertFalse(structKeyexists(res,"arg4"));

		var res=testArgs("test","test",nullValue(),nullValue());
		//dump(var:res,label:'p2');
		assertTrue(structKeyexists(res,"arg1"));
		assertTrue(structKeyexists(res,"arg2"));
		assertTrue(structKeyexists(res,"arg3"));
		assertFalse(structKeyexists(res,"arg4"));

	}
	public void function testNamedArguments(){
		if(hasFullNullSupport()) throw "test need full null support disabled";

		var res=testArgs(arg1="test",arg2="test");
		//dump(var:res,label:'n');
		assertTrue(structKeyexists(res,"arg1"));
		assertTrue(structKeyexists(res,"arg2"));
		assertTrue(structKeyexists(res,"arg3"));
		assertFalse(structKeyexists(res,"arg4"));
		res=testArgs(arg1="test",arg2="test",arg3=nullValue(),arg4=nullValue());
		//dump(var:res,label:'n');
		assertTrue(structKeyexists(res,"arg1"));
		assertTrue(structKeyexists(res,"arg2"));
		assertTrue(structKeyexists(res,"arg3"));
		assertFalse(structKeyexists(res,"arg4"));
	}

	
	private boolean function hasFullNullSupport(){
		return server.ColdFusion.ProductName EQ "Railo" && getPageContext().getConfig().getFullNullSupport();
	}
} 
</cfscript>