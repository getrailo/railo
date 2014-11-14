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
 ---><cfif request.admintype EQ "server"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfset error.message="">
<cfset error.detail="">
<cfif request.adminType EQ "web">
<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.schedule.list.cfm"/></cfcase>
	<cfcase value="edit"><cfinclude template="services.schedule.edit.cfm"/></cfcase>
	<cfcase value="create,#stText.Buttons.Create#"><cfinclude template="services.schedule.create.cfm"/></cfcase>
</cfswitch>
</cfif>