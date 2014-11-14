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
 ---><cfoutput>
<cfset color=iif(req.ddns.enabled,de('595F73'),de('595F73'))>

<cfform onerror="customError" action="#action('update')#">
<table class="tbl" width="600">
<tr>
	<td colspan="2">&nbsp;

	</td>
</tr>
<tr>
	<th scope="row">#lang.id#</th>
	<td>
		<div class="comment">#lang.commentId#</div><br>
		<cfinput required="yes" type="text" name="id" value="#req.ddns.id#" message="#lang.messageId#" size="40"/>
		
	</td>
</tr>
<tr>
	<th scope="row">#lang.onOff#</th>
	<td>
		<div class="comment">#lang.commentOnOff#</div><br>
		<input type="checkbox" class="checkbox" name="enabled" value="yes" <cfif req.ddns.enabled>checked="checked"</cfif>  />
		
	</td>
</tr>
<tr>
	<td colspan="2">
		<h2>#lang.proxy#</h2>
	</td>
</tr>
<tr>
	<th scope="row">#lang.proxyserver#</th>
	<td>
		<cfinput required="no" type="text" name="proxyserver" value="#req.ddns.proxyserver#" size="40"/>
	</td>
</tr>
<tr>
	<th scope="row">#lang.proxyport#</th>
	<td>
		<cfinput required="no" type="text" name="proxyport" value="#req.ddns.proxyport#" size="4"/>
	</td>
</tr>
<tr>
	<th scope="row">#lang.proxyuser#</th>
	<td>
		<cfinput required="no" type="text" name="proxyuser" value="#req.ddns.proxyuser#" size="20"/>
	</td>
</tr>
<tr>
	<th scope="row">#lang.proxypassword#</th>
	<td>
		<cfinput required="no" type="text" name="proxypassword" value="#req.ddns.proxypassword#" size="20"/>
	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;

	</td>
</tr>

<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="mainAction" value="#lang.btnsubmit#">
		<input type="reset" class="reset" name="cancel" value="#lang.btnCancel#">
	</td>
</tr>
</table>
</cfform>
</cfoutput>