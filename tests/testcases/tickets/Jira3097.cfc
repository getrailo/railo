<!--- 
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
 ---><cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="setUp">
		<cftry>
			<cfquery name="qInsert" datasource="mysql">
			drop TABLE T3097
			</cfquery>
			<cfcatch></cfcatch>
		</cftry>

		<cfquery name="qInsert" datasource="mysql">
			CREATE TABLE T3097 (
		    blobi BLOB
		    ,clobi TEXT
		    );
		</cfquery>

	</cffunction>
	
	<cffunction name="testBlobClob">
		<cfquery datasource="mysql">
		INSERT INTO T3097(blobi,clobi)
		VALUES(
			<cfqueryparam cfsqltype="cf_sql_blob" value="#"abc".getBytes()#"> 
			,<cfqueryparam cfsqltype="cf_sql_clob" value="abc"> 
		)
		</cfquery>


		<cfquery datasource="mySQL" name="local.qry">
			select * from T3097
		</cfquery>


		<cfset assertEquals("abc",toString(qry.blobi))>
		<cfset assertEquals("abc",qry.clobi)>
	</cffunction>
</cfcomponent>