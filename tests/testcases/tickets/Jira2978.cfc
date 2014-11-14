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
		local.result = CreateObject( 'webservice', createURL("Jira2978/service.cfc?wsdl")).acceptNestedArray({items:[{name:"item1",id:1},{name:"item2",id:2}]});

		assertEquals(expected:"item1",actual:local.result.getItems()[1].getName());
		assertEquals(expected:"1",actual:local.result.getItems()[1].getID());
		assertEquals(expected:"item2",actual:local.result.getItems()[2].getName());
		assertEquals(expected:"2",actual:local.result.getItems()[2].getID());
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}