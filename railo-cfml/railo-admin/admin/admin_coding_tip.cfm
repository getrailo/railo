<cfset stText=application.stText[session.railo_admin_lang]>
<cfoutput>
	<div class="coding-tip-trigger">#stText.settings.tip#</div>
	<div class="coding-tip">
		<div>#stText.settings.appcfcdesc#:</div>
		<code>#trim( Attributes.codeSample )#</code>
	</div>
</cfoutput>