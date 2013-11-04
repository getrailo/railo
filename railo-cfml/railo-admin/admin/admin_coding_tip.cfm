<cfscript>
	stText=application.stText[session.railo_admin_lang];
	if(!isNull(attributes.text))desc=attributes.text;
	else if(!isNull(attributes.ct) && attributes.ct ==true) desc=stText.settings.codetip;
	else desc=stText.settings.appcfcdesc;

	isExpand = attributes.keyExists( "expand" ) && attributes.expand;
</cfscript>
<cfoutput>
	<cfif !isExpand>
		<div class="coding-tip-trigger">#stText.settings.tip#</div>		
	</cfif>
	<div class="coding-tip #isExpand ? 'expanded' : ''#">
		<div>#desc#:</div>
		<code>#trim( Attributes.codeSample )#</code>
	</div>
</cfoutput>