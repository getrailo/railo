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
		result=httpCall(calledName:"Jira2588/index.cfm",method:"post",body:'{"foo":"bar"}');
		body=evaluate(trim(result.filecontent));
	}

	public void function testHTTPReqData(){
		assertEquals('{"foo":"bar"}',body.HTTPReqData.content);
	}
	
	public void function testForm(){
		assertEquals('',structKeyList(body.FORM));
	}
		
	private struct function httpCall(string calledName, string method='get',boolean addtoken=false, body){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		http method="#arguments.method#" result="local.result" url="#baseURL##arguments.calledName#" addtoken="#arguments.addtoken#" {
			if(!isNull(arguments.body))httpparam type="body" name="test" value="#arguments.body#";
		}
		return result;
	}
	
} 
</cfscript>