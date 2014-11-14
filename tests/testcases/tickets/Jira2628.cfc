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
		variables.path=getdirectoryFromPath(getCurrentTemplatePath())&"Jira2628/";
		variables.before=path&"before/";
		variables.after=path&"after/";
		
		
		//variables.acomp=path&"AComponent.cfc";
		//variables.ainterf=path&"AInterface.cfc";
		
		
		// variables.filePath=createFile("susi.txt","Susi Sorglos");
	}
	public function afterTests(){
		// deleteFile("susi.txt");
	}
	
	
	public function setUp(){
		
	}

	public void function testChanging(){
		
		fileCopy(variables.before&"AComponent.cfc",variables.path&"AComponent.cfc");
		fileCopy(variables.before&"AInterface.cfc",variables.path&"AInterface.cfc");

		variables.meta = getComponentMetadata("Jira2628.Test");
		
		assertEquals(true,isDefined('meta.extends.functions'));
		assertEquals(1,arrayLen(meta.extends.functions));
		assertEquals(true,isDefined('meta.implements.AInterface.functions'));
		assertEquals(1,arrayLen(meta.implements.AInterface.functions));
		
		sleep(1000);
		
		// change component
		fileCopy(variables.after&"AComponent.cfc",variables.path&"AComponent.cfc");
		//file action="touch" file="#variables.path#AComponent.cfc";
		
		local.meta = getComponentMetadata("Jira2628.Test");
		assertEquals(true,isDefined('meta.extends.functions'));
		assertEquals(2,arrayLen(meta.extends.functions));
		
		sleep(1000);
		
		// change interface
		fileCopy(variables.after&"AInterface.cfc",variables.path&"AInterface.cfc");
		//file action="touch" file="#variables.path#AInterface.cfc";
		
		
		local.meta = getComponentMetadata("Jira2628.Test");
		assertEquals(true,isDefined('meta.implements.AInterface.functions'));
		assertEquals(2,arrayLen(meta.implements.AInterface.functions));
		
		
	}
	
	
	
	
	/**
	* creates a file in the ram resource and returnthe absoulte path to this file
	* @filename name of the file, for example "test.txt"
	* @content string content for the file
	*/
	private string function createFile(required string filename, required string content) {
		local.path="ram:///"&filename;
		file action="write" file="#path#" output="#content#";
		return path;
	}
	
	/**
	* creates a file in the ram resource and returnthe absoulte path to this file
	* @filename name of the file, for example "test.txt"
	* @content string content for the file
	*/
	private void function deleteFile(required string filename) {
		local.path="ram:///"&filename;
		file action="delete" file="#path#";
	}
} 
</cfscript>