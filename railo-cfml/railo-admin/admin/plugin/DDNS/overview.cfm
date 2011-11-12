<cfoutput>
<cfset color=iif(req.ddns.enabled,de('595F73'),de('595F73'))>

<cfform onerror="customError" action="#action('update')#">
<table class="tbl" width="600">
<tr>
	<td colspan="2">&nbsp;

	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="id">#lang.id#</label></td>
	<td class="tblContent">
		<span class="comment">#lang.commentId#</span><br>
		<cfinput required="yes" type="text" name="id" id="id" value="#req.ddns.id#" message="#lang.messageId#" size="40"/>
		
	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="enabled">#lang.onOff#</label></td>
	<td class="tblContent">
		<span class="comment">#lang.commentOnOff#</span><br>
		<input type="checkbox" name="enabled" id="enabled" value="yes" <cfif req.ddns.enabled>checked="checked"</cfif>  />
		
	</td>
</tr>
<tr>
	<td colspan="2">
		<h2>#lang.proxy#</h2>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="proxyserver">#lang.proxyserver#</label></td>
	<td class="tblContent">
		<cfinput required="no" type="text" name="proxyserver" id="proxyserver" value="#req.ddns.proxyserver#" size="40"/>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="proxyport">#lang.proxyport#</label></td>
	<td class="tblContent">
		<cfinput required="no" type="text" name="proxyport" id="proxyport" value="#req.ddns.proxyport#" size="4"/>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="proxyuser">#lang.proxyuser#</label></td>
	<td class="tblContent">
		<cfinput required="no" type="text" name="proxyuser" id="proxyuser" value="#req.ddns.proxyuser#" size="20"/>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150" style="background-color:#color#"><label for="proxypassword">#lang.proxypassword#</label></td>
	<td class="tblContent">
		<cfinput required="no" type="text" name="proxypassword" id="proxypassword" value="#req.ddns.proxypassword#" size="20"/>
	</td>
</tr>
<tr>
	<td colspan="2">&nbsp;

	</td>
</tr>

<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#lang.btnsubmit#">
		<input type="reset" class="reset" name="cancel" value="#lang.btnCancel#">
	</td>
</tr>
</table>
</cfform>
</cfoutput>