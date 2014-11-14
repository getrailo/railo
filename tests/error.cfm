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
 --->



<cfoutput>
<table id="-railo-err" cellpadding="4" cellspacing="2">
	<tr>
		<td colspan="2" class="label">Railo #server.railo.version# Error (#catch.type#)</td>
	</tr>
	<cfparam name="catch.message" default="">
	<tr>
		<td class="label">Message</td>
		<td>#replace( HTMLEditFormat( trim( catch.message ) ), chr(10), '<br>', 'all' )#</td>
	</tr>
	<cfparam name="catch.detail" default="">
	<cfif len( catch.detail )>
		<tr>
			<td class="label">Detail</td>
		    <td>#replace( HTMLEditFormat( trim( catch.detail ) ), chr(10), '<br>', 'all' )#</td>
		</tr>
	</cfif>
	<cfif structkeyexists( catch, 'errorcode' ) && len( catch.errorcode ) && catch.errorcode NEQ 0>
		<tr>
			<td class="label">Error Code</td>
			<td>#catch.errorcode#</td>
		</tr>
	</cfif>
	<cfif structKeyExists( catch, 'extendedinfo' ) && len( catch.extendedinfo )>
		<tr>
			<td class="label">Extended Info</td>
			<td>#HTMLEditFormat( catch.extendedinfo )#</td>
		</tr>
	</cfif>
	<cfif structKeyExists( catch, 'additional' )>
		<cfloop collection="#catch.additional#" item="key">
			<tr>
				<td class="label">#key#</td>
				<td>#replace( HTMLEditFormat( catch.additional[key] ), chr(10),'<br>', 'all' )#</td>
			</tr>
		</cfloop>
	</cfif>
	<cfif structKeyExists( catch, 'tagcontext' )>
		<cfset len=arrayLen( catch.tagcontext )>
		<cfif len>
			<tr>
				<td class="label">Stacktrace</td>
				<td>The Error Occurred in<br>
					<cfloop index="idx" from="1" to="#len#">
						<cfset tc = catch.tagcontext[ idx ]>
						<cfparam name="tc.codeprinthtml" default="">
						<cfif len( tc.codeprinthtml )>

							<cfset isFirst = ( idx == 1 )>

							<a class="-railo-icon-#isFirst ? 'minus' : 'plus'#" id="__btn$#idx#" onclick="__RAILO.oc( this );" style="cursor: pointer;">
								#isFirst ? "<b>#tc.template#: line #tc.line#</b>" : "<b>called from</b> #tc.template#: line #tc.line#"#
							</a>
							<br>

							<blockquote class="#isFirst ? 'expanded' : 'collapsed'#" id="__cst$#idx#">
								#tc.codeprinthtml#<br>
							</blockquote>
						</cfif>
					</cfloop>
				</td>
			</tr>
		</cfif>
	</cfif>
	<tr>
		<td class="label">Java Stacktrace</td>
		<td>#replace( catch.stacktrace, chr(10), "<br><span style='margin-right: 1em;'>&nbsp;</span>", "all" )#</td>
	</tr>
	<tr>
		<td class="label">Timestamp</td>
		<td>
			<cfset timestamp = now()>
			#LsDateFormat( timestamp, 'short' )# #LsTimeFormat( timestamp, 'long' )#
		</td>
	</tr>
</table>
</cfoutput>
