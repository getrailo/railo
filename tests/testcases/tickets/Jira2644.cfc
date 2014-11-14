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

	public function setUp(){
		variables.baseURL=createURL("Jira2644/");
		
	}

	public void function testCompositeId(){
		// first call only initialize the data
		http method="get" result="local.result" url="#variables.baseURL#one.cfm" addtoken="false";
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
		
		// now get the result
		http method="get" result="local.result" url="#variables.baseURL#two.cfm" addtoken="false";
		local.res=evaluate(trim(result.filecontent));
		setting showdebugoutput="false";
		
		assertEquals('CA',res.getStateCode()&"");
		assertEquals('US',res.getCountryCode()&"");	
	}
	
	
	public void function testCompositeId2(){
		// first call only initialize the data
		http method="get" result="local.result" url="#variables.baseURL#bestseller.cfm" addtoken="false";
		assertEquals(200,result.status_code);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>