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
	
	variables.base=getDirectoryFromPath(getCurrentTemplatePath())&"Jira3236/";
	variables.src=variables.base&"src/";
	variables.trg=variables.base&"trg/";


	public void function test(){
		try {
			directoryCopy( variables.src, variables.trg, true, function( path ){
					dump(arguments.path);
					return path.find("tmp1"); // only copy the "tmp1" path
								
				});

			assertTrue(arrayToList(directoryList(variables.trg)).find("tmp1")>0);
			assertTrue(arrayToList(directoryList(variables.trg)).find("tmp2")==0);
		}
		finally {
			directoryDelete(variables.trg,true);
		}

		try {
			directoryCopy( variables.src, variables.trg, false, function( path ){
					dump(arguments.path);
					return path.find("tmp1"); // only copy the "tmp1" path
								
				});

			assertTrue(arrayToList(directoryList(variables.trg)).find("tmp1")==0);
			assertTrue(arrayToList(directoryList(variables.trg)).find("tmp2")==0);
		}
		finally {
			directoryDelete(variables.trg,true);
		}
	}
} 
</cfscript>