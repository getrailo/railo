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
	
	variables.tableName="TXXXX";

	public function beforeTests(){
		defineDatasource();

		try{
			query {
				echo("drop TABLE "&variables.tableName);
			}
		}
		catch(local.e){}
		
		
		query  {
			echo("CREATE TABLE "&variables.tableName&" (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("vc varchar(255),");		
			echo("c char(1)");		
			echo(") ");
		}
		
	}
	private string function defineDatasource(){
		application action="update" 
			datasource="#{
	  		class: 'org.hsqldb.jdbcDriver'
			, connectionString: 'jdbc:hsqldb:file:#getDirectoryFromPath(getCurrentTemplatePath())#/datasource/db'
		}#";
	}

	public void function testNoSpace() {
		var qry=query(a:[1,2,3]);

		var qry=queryExecute(
		"insert into "&variables.tableName&"( i, vc,c) values( :col1,:col2,:col3)",
		{ col1 = 1, col2 = 2, col3 = 3 }
		); 
	}
} 
</cfscript>