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
 ---><cfif request.admintype EQ "web"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfparam name="url.action2" default="none">
<cfset error.message="">
<cfset error.detail="">

<cftry>
<cfswitch expression="#url.action2#">
	<cfcase value="restart">
		<cfadmin 
			action="restart"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			remoteClients="#request.getRemoteClients()#">
	</cfcase>
</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>

<!--- 
restart --->
<cfoutput>
<table class="tbl" width="740">
<tr>
	<td colspan="2">#stText.services.update.restartDesc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>


<script type="text/javascript" language="JavaScript"><!--
var submitted = false;
function submitTheForm(field) {
	if(submitted == true) { return; }
	field.form.submit();
	//field.value = "in progress";
	field.disabled = true;
	submitted = true;
}
//--></script>

<cfform onerror="customError" action="#go(url.action,"restart")#" method="post">
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="button" class="button submit" name="mainAction" value="#stText.services.update.restart#" onclick="submitTheForm(this)">
	</td>
</tr>
</cfform>
</table>
</cfoutput>