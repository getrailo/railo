<cfcomponent output="no">


	<cfset variables._dataFilePath = "internaldata.cfm" />
	<cfset variables._datastore = "" />
	
	
	<cffunction name="init" returntype="any" output="no">
		<cfset loadData() />
		<cfreturn this />
	</cffunction>
	

	<cffunction name="getfavorites" returntype="struct" output="no">
		<cfreturn getdata('favorites', {}) />
	</cffunction>
	
	
	<cffunction name="isfavorite" returntype="boolean" output="no">
		<cfargument name="action" type="string" required="yes" />
		<cfreturn structKeyExists(getfavorites(), arguments.action) />
	</cffunction>
	
	
	<cffunction name="addfavorite" returntype="void" output="no">
		<cfargument name="action" type="string" required="yes" />
		<cfset var data = getfavorites() />
		<cfset data[arguments.action] = "" />
		<cfset setdata('favorites', data) />
	</cffunction>
	
	
	<cffunction name="removefavorite" returntype="void" output="no">
		<cfargument name="action" type="string" required="yes" />
		<cfset var data = getfavorites() />
		<cfset structDelete(data, arguments.action, false) />
		<cfset setdata('favorites', data) />
	</cffunction>
	
	
	<cffunction name="getdata" returntype="any" output="no">
		<cfargument name="key" type="string" required="yes" />
		<cfargument name="defaultvalue" type="any" required="no" default="" />
		<cfif structKeyExists(variables._datastore, arguments.key)>
			<cfreturn variables._datastore[arguments.key] />
		</cfif>
		<cfreturn arguments.defaultvalue />
	</cffunction>
	
	
	<cffunction name="setdata" returntype="void" output="no">
		<cfargument name="key" type="string" required="yes" />
		<cfargument name="value" type="any" required="yes" />
		<cflock name="setdata_admin" timeout="1" throwontimeout="no">
			<cfset variables._datastore[arguments.key] = arguments.value />
			<cfset fileWrite(variables._dataFilePath, serialize(variables._datastore)) />
		</cflock>
	</cffunction>
	
	
	<cffunction name="loaddata" returntype="void" output="no">
		<cftry>
			<cfset variables._datastore = evaluate(fileRead(variables._dataFilePath)) />
			<cfcatch>
				<cfset variables._datastore = {} />
			</cfcatch>
		</cftry>
	</cffunction>
	
	
</cfcomponent>