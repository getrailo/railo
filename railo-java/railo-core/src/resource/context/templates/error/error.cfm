</TD></TD></TD></TH></TH></TH></TR></TR></TR></TABLE></TABLE></TABLE></A></ABBREV></ACRONYM></ADDRESS></APPLET></AU></B></BANNER></BIG></BLINK></BLOCKQUOTE></BQ></CAPTION></CENTER></CITE></CODE></COMMENT></DEL></DFN></DIR></DIV></DL></EM></FIG></FN></FONT></FORM></FRAME></FRAMESET></H1></H2></H3></H4></H5></H6></HEAD></I></INS></KBD></LISTING></MAP></MARQUEE></MENU></MULTICOL></NOBR></NOFRAMES></NOSCRIPT></NOTE></OL></P></PARAM></PERSON></PLAINTEXT></PRE></Q></S></SAMP></SCRIPT></SELECT></SMALL></STRIKE></STRONG></SUB></SUP></TABLE></TD></TEXTAREA></TH></TITLE></TR></TT></U></UL></VAR></WBR></XMP>
<cfoutput>

<!--- Plus/minus Image --->
<cfif structKeyExists(cgi,'http_user_agent') and findNocase('MSIE',cgi.http_user_agent)>
	<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/debug_plus.gif.cfm">
	<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/debug_minus.gif.cfm">
<cfelse>
    <cfsavecontent variable="plus"><cfinclude template="../../admin/resources/img/debug_plus.gif.cfm"></cfsavecontent>
    <cfsavecontent variable="minus"><cfinclude template="../../admin/resources/img/debug_minus.gif.cfm"></cfsavecontent>
</cfif> 

<script>
<cfoutput>
var plus='#plus#';
var minus='#minus#';
</cfoutput>
function oc(id) {
	var code=document.getElementById('__cp'+id);
	var button=document.images['__btn'+id];
	if(code.style) {
		if(code.style.position=='absolute') {
			code.style.position='relative';
			code.style.visibility='visible';
		}
		else {
			code.style.position='absolute';
			code.style.visibility='hidden';
		}
		if((button.src+"").indexOf(plus)!=-1)button.src=minus;
		else button.src=plus;
	}
}
</script>
<table border="0" cellpadding="4" cellspacing="2" style="font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;font-size : 11px;background-color:red;border : 1px solid black;;">
<tr>
	<td colspan="2" style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Railo #server.railo.version# Error (#(catch.type)#)</td>
</tr>
<cfparam name="catch.message" default="">
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Message</td>
	<td style="border : 1px solid ##350606;background-color :##FFCC00;">#replace(HTMLEditFormat(trim(catch.message)),'
','<br />','all')#</td>
</tr>
<cfparam name="catch.message" default="">
<cfif len(catch.detail)>
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Detail</td>
    <td style="border : 1px solid ##350606;background-color :##FFCC00;">#replace(HTMLEditFormat(trim(catch.detail)),'
','<br />','all')#</td>
</tr>
</cfif>
<cfif structkeyexists(catch,'errorcode') and len(catch.errorcode) and catch.errorcode NEQ 0>
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Error Code</td>
	<td style="border : 1px solid ##350606;background-color :##FFCC00;">#catch.errorcode#</td>
</tr>
</cfif>
<cfif structKeyExists(catch,'extendedinfo') and len(catch.extendedinfo)>
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Extended Info</td>
	<td style="border : 1px solid ##350606;background-color :##FFCC00;">#HTMLEditFormat(catch.extendedinfo)#</td>
</tr>
</cfif>

<cfif structKeyExists(catch,'additional')>
<cfloop collection="#catch.additional#" item="key">
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">#key#</td>
	<td style="border : 1px solid ##350606;background-color :##FFCC00;">#replace(HTMLEditFormat(catch.additional[key]),'
','<br />','all')#</td>
</tr>
</cfloop>
</cfif>

<cfif structKeyExists(catch,'tagcontext')>
	<cfset len=arrayLen(catch.tagcontext)>
	<cfif len>
	<tr>
		<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;">Stacktrace</td>
		<td style="border : 1px solid ##350606;background-color :##FFCC00;">
		The Error Occurred in<br />
		<cfloop index="idx" from="1" to="#len#">
			<cfset tc=catch.tagcontext[idx]>
			<cfparam name="tc.codeprinthtml" default="">
		<cfif len(tc.codeprinthtml)>
		<img src="#variables[idx EQ 1?'minus':'plus']#" 
			style="margin-top:2px;" 
			onclick="oc('#idx#');" 
			name="__btn#idx#"/>
		</cfif>
		<cfif idx EQ 1>
			<b> #tc.template#: line #tc.line#</b><br />
		<cfelse>
			<b>called from</b>#tc.template#: line #tc.line#<br />
		</cfif>
		<cfif len(tc.codeprinthtml)><blockquote style="font-size : 10;<cfif idx GT 1>position:absolute;visibility:hidden;</cfif>" id="__cp#idx#">
		#tc.codeprinthtml#<br />
		</blockquote></cfif>
		</cfloop>
		</td>
	</tr>
	</cfif>
</cfif>
<tr>
	<td style="border : 1px solid ##350606;background-color :##FFB200;font-weight:bold;" nowrap="nowrap">Java Stacktrace</td>
	<td style="border : 1px solid ##350606;background-color :##FFCC00;"><pre>#HTMLEditFormat(catch.stacktrace)#</pre></td>
</tr>
</table><br />
</cfoutput>