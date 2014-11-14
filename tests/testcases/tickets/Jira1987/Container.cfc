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
 ---><cfcomponent hint="Holds Item objects" output="false">
	<cfproperty name="items" type="Item[]" />
	
	<!--- Under ColdFusion, this ensures the HyperlinkedItem object
	will be defined if it appears in the items array --->
	<cfproperty name="_HI" type="HyperlinkedItem" />
	
	<cfset this.items = ArrayNew( 1 ) />
</cfcomponent>