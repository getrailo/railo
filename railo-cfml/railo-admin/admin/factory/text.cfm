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
 ---><cfinclude template="../resources/resources.cfm">

<cfoutput>
<cfsavecontent variable="content">
#chr(60)#cfprocessingdirective pageencoding="utf-8">#chr(60)#cfscript>
if(not StructKeyExists(session,'railo_admin_lang'))session.railo_admin_lang='en';
if(session.railo_admin_lang EQ 'de') {
	stText=#serialize(stText['de'])#;
}
else {
	stText=#serialize(stText['en'])#;
}
#chr(60)#/cfscript>

</cfsavecontent>
</cfoutput>
<cffile action="write" file="../resources/text.cfm" output="#trim(content)#" addnewline="yes" charset="UTF-8">
<!--- 
<cfloop collection="#stText#" item="key">
	<cfoutput>
	<cfsavecontent variable="content">
	#chr(60)#cfscript>
	stText=#serialize(stText[key])#;
	#chr(60)#/cfscript>
	</cfsavecontent>
	<cffile action="write" file="../resources/text_#key#.cfm" output="#trim(content)#" addnewline="no">
	</cfoutput>
</cfloop> --->