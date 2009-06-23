<cfif thistag.executionmode EQ "start"><cfsilent>

<cfset width="">
<cfif isDefined("attributes.width")><cfset width=" width="""&attributes.width&""""></cfif>
<cfset height="">
<cfif isDefined("attributes.height")><cfset height=" height="""&attributes.height&""""></cfif>
<cfset title="">
<cfif isDefined("attributes.title")><cfset title=" title="""&attributes.title&""""></cfif>
<cfset hspace="">
<cfif isDefined("attributes.hspace")><cfset hspace=" hspace="""&attributes.hspace&""""></cfif>
<cfset vspace="">
<cfif isDefined("attributes.vspace")><cfset vspace=" vspace="""&attributes.vspace&""""></cfif>
<cfset valign="">
<cfif isDefined("attributes.valign")><cfset valign=" valign="""&attributes.valign&""""></cfif>
<cfset style="">
<cfif isDefined("attributes.style")><cfset style=" style="""&attributes.style&""""></cfif>

<cfparam name="attributes.border" default="0">

</cfsilent><cfoutput><img src="resources/img/#attributes.src#.cfm" #width##height##hspace##vspace##title##valign##style# border="#attributes.border#" /></cfoutput></cfif>