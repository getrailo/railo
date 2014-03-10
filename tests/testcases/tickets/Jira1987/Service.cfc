<cfcomponent hint="Test web service" output="false">
	
	<cffunction name="getItems" access="remote" returntype="Container"
			output="false"
			hint="Gets two Item objects and two HyperlinkedItem
				objects, packaged in a Container object.">
		
		<cfset local.ITEM_COUNT = 4 />
		<cfset local.container = CreateObject( "component", "Container" ) />
		
		<cfloop from="1" to="4" index="local.i">
			<cfset local.item = CreateObject( "component", "Item" ) />
			<cfset local.item.id = local.i />
			<cfset local.item.name = "Item Number #local.i#" />
			<cfset ArrayAppend( local.container.items, local.item ) />
		</cfloop>
		
		<cfreturn local.container />
	</cffunction>

	<cffunction name="getItemsMixed" access="remote" returntype="Container"
			output="false"
			hint="Gets two Item objects and two HyperlinkedItem
				objects, packaged in a Container object.">
		
		<cfset local.container =CreateObject( "component", "Container" ) />
		
		<cfloop from="1" to="4" index="local.i">
			<cfif local.i mod 2 eq 0>
				<cfset local.item =
						CreateObject( "component", "Item" ) />
			<cfelse>
				<cfset local.item =
						CreateObject( "component",
							"HyperlinkedItem" ) />
				<cfset local.item.URL =
						"http://example.com/"
						& URLEncodedFormat( local.i ) />
			</cfif>
			
			<cfset local.item.id = local.i />
			<cfset local.item.name = "Item Number #local.i#" />
			
			<cfset ArrayAppend( local.container.items, local.item ) />
		</cfloop>
		
		<cfreturn local.container />
	</cffunction>

	<cffunction name="getItemsInterface" returntype="ContainerI" access="private"
			output="false"
			hint="Gets two Item objects and two HyperlinkedItem
				objects, packaged in a Container object.">
		
		<cfset local.container =CreateObject( "component", "ContainerI" ) />
		
		<cfloop from="1" to="4" index="local.i">
			<cfif local.i mod 2 eq 0>
				<cfset local.item =
						CreateObject( "component", "Item" ) />
			<cfelse>
				<cfset local.item =
						CreateObject( "component",
							"HyperlinkedItem" ) />
				<cfset local.item.URL =
						"http://example.com/"
						& URLEncodedFormat( local.i ) />
			</cfif>
			
			<cfset local.item.id = local.i />
			<cfset local.item.name = "Item Number #local.i#" />
			
			<cfset ArrayAppend( local.container.items, local.item ) />
		</cfloop>
		
		<cfreturn local.container />
	</cffunction>
	
</cfcomponent>