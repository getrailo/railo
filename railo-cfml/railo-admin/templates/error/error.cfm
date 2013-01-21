<cffunction name="isOldIE" output="true">
	<cfif structKeyExists(cgi,'http_user_agent')>
		<cfset var index=findNocase('MSIE',cgi.http_user_agent)>
		<cfif index GT 0>
			<cfset index+=4>
			<cfset var next=find(';',cgi.http_user_agent,index+1)>
			<cfif next GT 0>
				<cfset var sub=trim(mid(cgi.http_user_agent,index,next-index))>
				<cfif isNumeric(sub) and sub LT 8>
					<cfreturn true>
				</cfif>
			</cfif>
		</cfif>
	</cfif>
	<cfreturn false>
</cffunction><cfsilent>
<!--- Plus/minus Image --->
<cfif not isOldIE()>
	<cfset plus='data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw=='>
	<cfset minus='data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7'>
<cfelse>
	<cfset plus="#cgi.context_path#/railo-context/admin/resources/img/debug_plus.gif.cfm">
	<cfset minus="#cgi.context_path#/railo-context/admin/resources/img/debug_minus.gif.cfm">
</cfif>
	
</cfsilent></TD></TD></TD></TH></TH></TH></TR></TR></TR></TABLE></TABLE></TABLE></A></ABBREV></ACRONYM></ADDRESS></APPLET></AU></B></BANNER></BIG></BLINK></BLOCKQUOTE></BQ></CAPTION></CENTER></CITE></CODE></COMMENT></DEL></DFN></DIR></DIV></DL></EM></FIG></FN></FONT></FORM></FRAME></FRAMESET></H1></H2></H3></H4></H5></H6></HEAD></I></INS></KBD></LISTING></MAP></MARQUEE></MENU></MULTICOL></NOBR></NOFRAMES></NOSCRIPT></NOTE></OL></P></PARAM></PERSON></PLAINTEXT></PRE></Q></S></SAMP></SCRIPT></SELECT></SMALL></STRIKE></STRONG></SUB></SUP></TABLE></TD></TEXTAREA></TH></TITLE></TR></TT></U></UL></VAR></WBR></XMP>
<cfoutput>
<script>
var plus='#plus#';
var minus='#minus#';
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
<cfscript>
function convertST(st){
	arguments.st=replace(HTMLEditFormat(arguments.st),"
","<br>","all");

	arguments.st=replace(arguments.st,"  ","&nbsp; ","all");
	arguments.st=replace(arguments.st,"  ","&nbsp; ","all");
	arguments.st=replace(arguments.st,"  ","&nbsp; ","all");
	arguments.st=replace(arguments.st,"  ","&nbsp; ","all");
	arguments.st=replace(arguments.st,"  ","&nbsp; ","all");
	arguments.st=replace(arguments.st,"	","&nbsp;&nbsp;&nbsp;","all");
	
return arguments.st;

}
</cfscript>
<table border="0" cellpadding="4" cellspacing="2" style="font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;font-size : 11px;background-color:red;border : 1px solid black;;">
<tr>
	<td colspan="2" style="border: 1px solid ##350606; color: ##222; background-color: ##FFB200; font-weight: bold;">Railo #server.railo.version# Error (#(catch.type)#)</td>
</tr>
<cfparam name="catch.message" default="">
<tr>
	<td style="border: 1px solid ##350606; color: ##222; background-color:##FFB200; font-weight: bold;">Message</td>
	<td style="border: 1px solid ##350606; color: ##222; background-color:##FFCC00;">#replace(HTMLEditFormat(trim(catch.message)),'
','<br />','all')#</td>
</tr>
<cfparam name="catch.detail" default="">
<cfif len(catch.detail)>
<tr>
	<td style="border: 1px solid ##350606; color: ##222; background-color:##FFB200; font-weight:bold;">Detail</td>
    <td style="border: 1px solid ##350606; color: ##222; background-color:##FFCC00;">#replace(HTMLEditFormat(trim(catch.detail)),'
','<br />','all')#</td>
</tr>
</cfif>
<cfif structkeyexists(catch,'errorcode') and len(catch.errorcode) and catch.errorcode NEQ 0>
<tr>
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFB200; font-weight: bold;">Error Code</td>
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFCC00;">#catch.errorcode#</td>
</tr>
</cfif>
<cfif structKeyExists(catch,'extendedinfo') and len(catch.extendedinfo)>
<tr>
	<td style="border: 1px solid ##350606; color: ##222; background-color:##FFB200; font-weight: bold;">Extended Info</td>
	<td style="border: 1px solid ##350606; color: ##222; background-color:##FFCC00;">#HTMLEditFormat(catch.extendedinfo)#</td>
</tr>
</cfif>

<cfif structKeyExists(catch,'additional')>
<cfloop collection="#catch.additional#" item="key">
<tr>
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFB200; font-weight: bold;">#key#</td>
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFCC00;">#replace(HTMLEditFormat(catch.additional[key]),'
','<br />','all')#</td>
</tr>
</cfloop>
</cfif>

<cfif structKeyExists(catch,'tagcontext')>
	<cfset len=arrayLen(catch.tagcontext)>
	<cfif len>
	<tr>
		<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFB200; font-weight: bold;">Stacktrace</td>
		<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFCC00;">
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
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFB200; font-weight: bold;" nowrap="nowrap">Java Stacktrace</td>
	<td style="border: 1px solid ##350606; color: ##222; background-color: ##FFCC00;" class="">#convertST(catch.stacktrace)#</td>
</tr>
</table><br />
</cfoutput>