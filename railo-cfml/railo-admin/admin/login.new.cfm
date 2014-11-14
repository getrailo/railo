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
 ---><cfparam name="cookie.railo_admin_lang" default="en">
<cfset session.railo_admin_lang = cookie.railo_admin_lang>
<cfparam name="languages" default="#{en:'English',de:'Deutsch'}#">

<cfoutput>
	<cfform name="login" action="#request.self#" method="post"><!--- onerror="customError"--->
		<table class="maintbl" style="width:300px">
			<tbody> 
				<tr>
					<th scope="row" class="right" nowrap="nowrap">#stText.Login.Password#</th>
					<td><cfinput type="password" name="new_password" id="new_password" value="" passthrough='autocomplete="off"'
						class="xlarge" required="yes" message="#stText.Login.PasswordMissing#" />
					</td>
				</tr>
				<tr>
					<th scope="row" class="right" nowrap="nowrap">#stText.Login.RetypePassword#</th>
					<td><cfinput type="password" name="new_password_re" value="" passthrough='autocomplete="off"'
						class="xlarge" required="yes" message="#stText.Login.RetypePasswordMissing#" />
					</td>
				</tr>
				<cfset f="">
				<cfloop collection="#languages#" item="key">
					<cfif f EQ "" or key EQ session.railo_admin_lang>
						<cfset f=key>
					</cfif>
				</cfloop>
				<tr>
					<th scope="row" class="right" nowrap="nowrap">#stText.Login.language#</th>
					<td>
						<select name="lang" class="xlarge"><!--- onchange="changePic(this.options[this.selectedIndex].value)"--->
							<cfloop collection="#languages#" item="key">
								<option value="#key#" <cfif key EQ session.railo_admin_lang>selected</cfif>>#languages[key]#</option>
							</cfloop>
						</select>
					</td>
				</tr>
				<tr>
					<th scope="row" class="right" nowrap="nowrap">#stText.Login.rememberMe#</th>
					<td>
						<select name="rememberMe" class="xlarge">
							<cfloop list="s,d,ww,m,yyyy" index="i">
								<option value="#i#"<cfif i eq form.rememberMe> selected</cfif>>#stText.Login[i]#</option>
							</cfloop>
						</select>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" class="right"><input class="button submit" type="submit" name="submit" value="#stText.Buttons.Submit#"></td>
				</tr>
			</tfoot>
		</table>
	</cfform>
</cfoutput>

<script type="text/javascript">
	$( function() {
		$( '#new_password' ).focus();
	});
</script>