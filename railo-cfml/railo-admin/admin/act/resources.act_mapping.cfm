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
 ---><cfparam name="url.job" default="" type="string">
<cfparam name="url.mapping_id" default="-1" type="numeric">
<cfset mappings=getPageContext().getConfig().getMappings()>
<cfif url.job eq "save" AND url.mapping_id gt -1>
	<cfset mappings=getPageContext().getConfig().getMappings()>
	<cfset mappings[url.mapping_id].physical = form.physical>
	<cfset mappings[url.mapping_id].virtual  = form.virtual>
	<cfset mappings[url.mapping_id].archive  = form.archive>
	<cfif isDefined("form.trusted")>
		<cfset mappings[url.mapping_id].trusted  = True>
	<cfelse>
		<cfset mappings[url.mapping_id].trusted  = False>
	</cfif>
</cfif>
<cfif url.job eq "#stText.Buttons.Delete#">
	<cfset tmp = ArrayDeleteAt(mappings, url.mapping_id)>
<cfelseif url.job eq "add">
	<cfset tmp = ArrayAppend(mappings, StructNew())>
	<cfset url.mapping_id = ArrayLen(mappings)>
	<cfset mappings[url.mapping_id].physical = form.physical>
	<cfset mappings[url.mapping_id].virtual  = form.virtual>
	<cfset mappings[url.mapping_id].archive  = form.archive>
	<cfif isDefined("form.trusted")>
		<cfset mappings[url.mapping_id].trusted  = True>
	<cfelse>
		<cfset mappings[url.mapping_id].trusted  = False>
	</cfif>
</cfif> 