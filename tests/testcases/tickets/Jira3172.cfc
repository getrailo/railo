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
	variables.str="#chr(1051)##chr(1101)##chr(1075)##chr(1099)##chr(1088)##chr(1099)#";
	//public function setUp(){}

	public void function testISO88595(){
		http method="get" result="local.result" url="#createURL("Jira3172/iso_8859_5.cfm")#" charset="iso-8859-5" addtoken="false";
		assertEquals("#str#-#str#",trim(result.filecontent));
	}

	public void function testUTF8(){
		http method="get" result="local.result" url="#createURL("Jira3172/utf_8.cfm")#" charset="UTF-8" addtoken="false";
		assertEquals("#str#-#str#",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>