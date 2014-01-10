<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="setUp"></cffunction>
	<cffunction name="testNull">
		<cfscript>
		local.dataQuery = queryNew("colA,colB", "Integer,VarChar");
		queryAddRow(dataQuery, 4);
		querySetCell(dataQuery, "colA", "3",1);
		querySetCell(dataQuery, "colB", "dummy1",1);
		querySetCell(dataQuery, "colA", "1", 2);
		querySetCell(dataQuery, "colB", "dummy2", 2);
		querySetCell(dataQuery, "colA", "11", 3);
		querySetCell(dataQuery, "colB", "dummy3", 3);
		querySetCell(dataQuery, "colA", nullValue(), 4);
		querySetCell(dataQuery, "colB", "dummy3", 4);
		</cfscript>
		
		
		<cfquery name="dataQuery" dbtype="query">
		select	 *
		from	 dataQuery
		order by	 colA
		</cfquery>
		<cfset assertEquals(",1,3,11",arrayToList(queryColumnData(local.dataQuery,'colA')))>
	</cffunction>
	
	<cffunction name="testEmptyString">
		<cfscript>
		local.dataQuery = queryNew("colA,colB", "Integer,VarChar");
		queryAddRow(dataQuery, 4);
		querySetCell(dataQuery, "colA", "3",1);
		querySetCell(dataQuery, "colB", "dummy1",1);
		querySetCell(dataQuery, "colA", "1", 2);
		querySetCell(dataQuery, "colB", "dummy2", 2);
		querySetCell(dataQuery, "colA", "11", 3);
		querySetCell(dataQuery, "colB", "dummy3", 3);
		querySetCell(dataQuery, "colA", "", 4);
		querySetCell(dataQuery, "colB", "dummy3", 4);
		</cfscript>
		
		
		<cfquery name="dataQuery" dbtype="query">
		select	 *
		from	 dataQuery
		order by	 colA
		</cfquery>
		<cfset assertEquals(",1,3,11",arrayToList(queryColumnData(local.dataQuery,'colA')))>
	</cffunction>
</cfcomponent>