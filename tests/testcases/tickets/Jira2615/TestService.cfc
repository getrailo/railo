<cfcomponent output="false">
	<!--- 
	<cffunction name="myFunction" access="remote" returntype="void" output="false" >
		<cfargument name="myArray" type="MyItem[]" required="true" />
	</cffunction>
--->
	
	<cffunction name="setStruct" access="remote" returntype="void" output="false" >
		<cfargument name="argStruct" type="struct" required="true" />
	</cffunction>

	<cffunction name="getStruct" access="remote" returntype="struct" output="false">
		<cfreturn {a:1}>
	</cffunction>

	<cffunction name="echoStruct" access="remote" returntype="struct" output="false">
		<cfargument name="argStruct" type="struct" required="true" />
		<cfreturn arguments.argStruct>
	</cffunction>



	<cffunction name="setMyItem" access="remote" returntype="void" output="false" >
		<cfargument name="myitem" type="MyItem" required="true" />
	</cffunction>

	<cffunction name="getMyItem" access="remote" returntype="MyItem" output="false" >
		<cfreturn new MyItem("from:getMyItem")>
	</cffunction>
	
	<cffunction name="getMyItemArray" access="remote" returntype="MyItem[]" output="false" >
		<cfreturn [new MyItem("1:getMyItemArray"),new MyItem("2:getMyItemArray")]>
	</cffunction>

	<cffunction name="getArray" access="remote" returntype="Array" output="false" >
		<cfreturn [new MyItem("1:getArray"),new MyItem("2:getArray")]>
	</cffunction>
	


	<cffunction name="getAnyArray" access="remote" returntype="any" output="false" >
		<cfreturn [new MyItem("1:getAnyArray"),new MyItem("2:getAnyArray")]>
	</cffunction>

	<cffunction name="getAny" access="remote" returntype="any" output="false" >
		<cfreturn new MyItem("getAny")>
	</cffunction>
	
</cfcomponent>