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

	//public function setUp(){}

	public void function testActionCacheWithoutBody(){
		http method="get" result="local.res1" url="#createURL("Jira3021/reqnb.cfm")#" addtoken="false";
		sleep(10);
		http method="get" result="local.res2" url="#createURL("Jira3021/reqnb.cfm")#" addtoken="false";
		
		assertEquals(res1.filecontent,res2.filecontent);
	}


	public void function testActionCacheWithBody(){
		http method="get" result="local.res1" url="#createURL("Jira3021/reqwb.cfm")#" addtoken="false";
		res1=listItemTrim(res1.fileContent);
		sleep(10);
		http method="get" result="local.res2" url="#createURL("Jira3021/reqwb.cfm")#" addtoken="false";
		res2=listItemTrim(res2.fileContent);
		assertNotEquals(listFirst(res1),listFirst(res2));
		assertEquals(listLast(res1),listLast(res2));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
} 
</cfscript>