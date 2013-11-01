<cfscript>
stText=application.stText[session.railo_admin_lang];
if(!isNull(attributes.text))desc=attributes.text;
else if(!isNull(attributes.ct) && attributes.ct ==true) desc=stText.settings.codetip;
else desc=stText.settings.appcfcdesc;

</cfscript><cfoutput>
	<div class="coding-tip-trigger">#stText.settings.tip#</div>
	<div class="coding-tip">
		<div>#desc#:</div>
		<code>#trim( Attributes.codeSample )#</code>
	</div>
</cfoutput>