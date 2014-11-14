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


	public void function testMultiPart(){
		test(true);
	}
	public void function testNOTMultiPart(){
		test(false);
	}

	private void function test(boolean multipart){
		http method="post" result="result" url="#createURL("Jira2622/index.cfm")#?TEST1=fromURL" addtoken="false" multipart="#multipart#" {
			httpparam type="formfield" name="test1" value="Test1-1";
			httpparam type="formfield" name="test1" value="Test1-2";
			httpparam type="formfield" name="TEST1" value="TEST1-3";
		}
		
		local.res=deserializejson(trim(result.filecontent));
		
		assertEquals("TEST1-3,fromurl",arrayToList(res.map['TEST1']));
		assertEquals("test1-1,test1-2",arrayToList(res.map['test1']));
		assertEquals("test1-1,test1-2",arrayToList(res.values));
		
		assertEquals("test1-1,test1-2,TEST1-3",res.form.test1);
		assertEquals("fromurl",res.url.test1);
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>