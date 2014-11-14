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

	function giveIntegerPropertyAsIntegerTest () {
		local.thingWithDate = CreateObject( 'webservice', createURL("Jira3044/service.cfc?wsdl")).returnThingWithDate();
		local.struct = {key1:2, key2: local.thingwithdate.number, key3:local.thingwithdate.date}
		//dump(year(local.thingwithdate.date));abort;
		myStruct.sortedKeys = StructSort(local.struct , 'numeric');
		myStruct.sortedKeysUsingMember = local.struct.sort( 'numeric' );
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}