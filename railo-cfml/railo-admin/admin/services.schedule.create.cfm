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
 ---><cfset more=struct()>
<cfif isDefined('form.port')>
	<cfset more.port=form.port>
</cfif>

<cfset form.name = trim( form.name )>

<cfadmin action="schedule" type="#request.adminType#" password="#session["password"&request.adminType]#"

	attributeCollection="#more#"
	
	scheduleAction="update" 
	operation="httprequest"
	task="#form.name#"
	url="#form.url#"
	interval="#form.interval#" 
	startdate="#nullIfNoDate('start')#" 
	starttime="#nullIfNoTime('start')#"
	remoteClients="#request.getRemoteClients()#">


<cfif StructKeyExists(form,"paused") && form.paused>

	<cfadmin action="schedule" type="#request.adminType#" password="#session["password"&request.adminType]#"

        scheduleAction="pause"
        task="#form.name#"
        remoteClients="#request.getRemoteClients()#">
</cfif>

			
<cflocation url="#request.self#?action=#url.action#&action2=edit&task=#hash(form.name)#" addtoken="no">