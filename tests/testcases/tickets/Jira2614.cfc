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
		variables.dest=getDirectoryFromPath(getCurrenttemplatepath())&"Jira2614/downloads";
		if(fileExists(dest)) fileDelete(dest);
		if(directoryexists(dest)) directorydelete(dest,true);
		
		http method="post" result="result" url="#createURL("Jira2614/index.cfm")#" addtoken="false"  multipart="true"{
			httpparam type="file" name="file" file="#getCurrentTemplatePath()#";
		}
	}

	public function afterTests(){
		if(fileExists(dest)) fileDelete(dest);
		if(directoryexists(dest)) directorydelete(dest,true);
		
	}

	public void function testFileContent(){
		assertEquals("",trim(result.filecontent));
	}

	public void function testDestinationFile(){
		assertEquals(false,fileExists(dest));
		assertEquals(true,directoryexists(dest));
		assertEquals(true,fileExists(dest&"/"&listLast(getCurrenttemplatepath(),'\/')));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>