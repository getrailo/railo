<cfcomponent output="false" hint="The web service for the bug 40537 test.">

	<cffunction name="send" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>
	<cffunction name="sendArray" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[]" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>
	<cffunction name="sendArrayArray" access="remote" returntype="void"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[][]" required="true"
				hint="An array of MyItem objects." />
		
	</cffunction>

	<cffunction name="echoArray" access="remote" returntype="MyItem"
			output="false"
			hint="A function that accepts a typed array as an argument.">
		<cfargument name="myArray" type="MyItem[]" required="true"
				hint="An array of MyItem objects." />
		<cfreturn new MyItem() />
	</cffunction>
	
	<cffunction name="takeStructAsType" access="remote" returntype="MyItem">
		<cfargument name="myItem" type="MyItem" required="true" />
		<cfreturn arguments.myItem />
	</cffunction>

</cfcomponent>
