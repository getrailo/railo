
<cfoutput><center>
<br /><br /><br />
<table class="tbl" width="600">
<tr>
	<td align="center">
<cfif structKeyExists(session,'confirm')>
	<cfif session.confirm.success>
    	#session.confirm.text#
    <cfelse>
    	<span class="CheckError">#session.confirm.text#</span>
    </cfif>
</cfif>
	</td>
</tr>
<tr>
	<td align="center">
        <cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
        <input type="submit" class="submit" name="mainAction" value="#stText.Buttons.ok#">
        </cfform>
    </td>
</tr>
</table></center>
</cfoutput>