
<cfoutput>
	<br /><br /><br />
	<div class="center">
		<cfif structKeyExists(session,'confirm')>
			<cfif session.confirm.success>
				#session.confirm.text#
			<cfelse>
				<div class="error">#session.confirm.text#</div>
			</cfif>
		</cfif>
		
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
			<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.ok#">
		</cfform>
	</div>
</cfoutput>