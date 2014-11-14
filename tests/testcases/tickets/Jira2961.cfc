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
component extends="org.railo.cfml.test.RailoTestCase" {



	function testChildWithExtends() {
		local.service = CreateObject( 'webservice', createURL("Jira2961/service.cfc?wsdl"));
		local.membersObj = CreateObject( 'webservice', createURL("Jira2961/service.cfc?wsdl")).getMembers();
		local.members=membersObj.members;
		
		for(local.i = 1; i <= arrayLen(local.members) ; i++) {
			local.member = local.members[local.i];
			assertEquals(expected:"Member #local.i#",actual:member.name);
		}
			
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}
