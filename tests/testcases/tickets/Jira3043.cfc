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
	
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testMySQLWithBSTTimezone(){

		application action="update" timezone="BST";
		setTimeZone("BST");

		query name="local.qry" datasource="mysql" {
			echo("select 'a' as a");
		}
		//assertEquals("","");
		
	}
	public void function testMySQLWithLondonTimezone(){

		application action="update" timezone="Europe/London";
		setTimeZone("Europe/London");
		
		query name="local.qry" datasource="mysql" {
			echo("select 'a' as a");
		}
		//assertEquals("","");
		
	}
} 
</cfscript>