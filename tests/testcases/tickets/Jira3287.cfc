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
	
	variables.tableName="JIRA3287";

	variables.NL="
";

	public function beforeTests(){
		defineDatasource();



		query  {
			echo("CREATE OR REPLACE");
			echo(NL&"PACKAGE "&tableName&" AS");
			
				echo(NL&"TYPE "&tableName&"_rows IS REF CURSOR;");
				echo(NL&"FUNCTION rowsfunction");
				echo(NL&"return "&tableName&"_rows;");		
				echo(NL&"PROCEDURE rowsproc (");		
				echo(NL&"v_dummy        IN         INTEGER,");			
				echo(NL&"v_rows           OUT      "&tableName&"_rows);");			
			
			echo(NL&"END "&tableName&";");	
		}


		query  {
			echo("CREATE OR REPLACE");
			echo(NL&"PACKAGE BODY "&tableName&" AS");
				echo(NL&"FUNCTION rowsfunction");
				echo(NL&"return "&tableName&"_rows");
				echo(NL&"AS");
				echo(NL&"v_cursor   "&tableName&"_rows;");
				echo(NL&"BEGIN");
				echo(NL&"OPEN v_cursor FOR");
				echo(NL&"select 'eins',1 from dual");
				echo(NL&"union");
				echo(NL&"select 'zwei',2 from dual;");
				echo(NL&"RETURN v_cursor;");
				echo(NL&"END rowsfunction;");
				echo(NL&"PROCEDURE rowsproc (");
				echo(NL&"v_dummy        IN         INTEGER,");
				echo(NL&"v_rows           OUT      "&tableName&"_rows)");
				echo(NL&"is");
				echo(NL&"begin");
				echo(NL&"open v_rows for ");
				echo(NL&"select 'eins',1 from dual");
				echo(NL&"union");
				echo(NL&"select 'zwei',2 from dual;");
				echo(NL&"end rowsproc;");
			echo(NL&"END "&tableName&";");
		}
		
		
	}
	private string function defineDatasource(){
		application action="update" 
			datasource="#{
				class: 'oracle.jdbc.driver.OracleDriver'
				,connectionString: 'jdbc:oracle:thin:@localhost:1521:cdb1'
				,username: 'system'
				, password: "encrypted:8d78149e3ce9de5bac8c037861b28dc67538d2b288bef3bb"

		}#";
	}


	public void function test1() {
		storedproc procedure="#tableName#.rowsfunction" {
	    	procresult name="qry";
		}
		assertEquals("EINS,1",qry.columnlist());
		assertEquals(2,qry.recordcount());
		assertEquals('eins',qry['eins'][1]);
		assertEquals(2,qry['1'][2]);

	}

	public void function test2() {

		storedproc procedure="#tableName#.rowsproc" {
		    procparam cfsqltype="cf_sql_numeric" type="in" value="1";
		    procresult name="local.qry";
		}
		assertEquals("EINS,1",qry.columnlist());
		assertEquals(2,qry.recordcount());
		assertEquals('eins',qry['eins'][1]);
		assertEquals(2,qry['1'][2]);

	}
} 
</cfscript>