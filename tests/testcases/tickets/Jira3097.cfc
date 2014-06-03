<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
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