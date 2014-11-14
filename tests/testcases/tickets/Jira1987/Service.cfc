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
 ---><cfcomponent hint="Test web service" output="false">
	
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