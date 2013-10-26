<cfset stText=application.stText[session.railo_admin_lang]>
<cfoutput>
	<div class="coding-tip-trigger">#stText.settings.tip#</div>
	<div class="coding-tip">
		<div><cfif !isNull(attributes.ct) && attributes.ct ==true>#stText.settings.codetip#<cfelse>#stText.settings.appcfcdesc#</cfif>:</div>
		<code>#trim( Attributes.codeSample )#</code>
	</div>
</cfoutput>