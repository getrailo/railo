<cfif thistag.executionmode EQ "end" or not thistag.hasendtag>
<cfparam name="attributes.navigation" default="">
<cfparam name="attributes.title" default="">
<cfparam name="attributes.content" default="">
<cfparam name="attributes.right" default="">
<cfparam name="attributes.width" default="780">

<cfscript>
ad=request.adminType;

hasNavigation=len(attributes.navigation) GT 0;

other=iif(request.adminType EQ "web","'server'","'web'");
</cfscript>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<cfoutput><title>Railo #ucFirst(request.adminType)# Administrator</title>
		<style>
		<cfset bgColor=iif(request.adminType EQ "web",de('595F73'),de('74615A'))>
		<cfset bgDarkColor=iif(request.adminType EQ "web",de('595F73'),de('74615A'))>
		<cfset bgBrightColor=iif(request.adminType EQ "web",de('DFE9F6'),de('DFE9F6'))>
		
		<cfset txtBrightColor=iif(request.adminType EQ "web",de('DFE9F6'),de('DFE9F6'))>
		<cfset txtDarkColor=iif(request.adminType EQ "web",de('595F73'),de('74615A'))>
		
		
		<cfset btnColor=iif(request.adminType EQ "web",de('666666'),de('666666'))>
		<cfset borderColor=iif(request.adminType EQ "web",de('ff0000'),de('DFE9F6'))>
		
		
		<cfset redColor=iif(request.adminType EQ "web",de('FF9999'),de('FF9999'))>
		<cfset greenColor=iif(request.adminType EQ "web",de('99FF66'),de('99FF66'))>
		<cfset yellowColor=iif(request.adminType EQ "web",de('FFFF66'),de('FFFF66	'))>
		
		
		
		body {background-image:url('resources/img/#ad#-bg.gif.cfm');background-repeat:repeat-x;background-color:###bgColor#;margin-top:0px;margin-left:0px;}
		body, tr, td,div {font-family:Arial, Helvetica, sans-serif;font-size : 9pt;color:###txtDarkColor#;}
		h1 {font-family:Arial, Helvetica, sans-serif;font-size : 20px;font-style:italic;color:###txtDarkColor#;}
		h2 {font-size : 16px;font-style:italic;font-weight:bold;color:###txtDarkColor#;}
		
		div.navtop		{margin-top:8px;color:###txtDarkColor#;font-weight:bold;font-size : 12px;}
    	a.navtop		{text-decoration:none;font-weight:bold;font-size : 12px;}
		
    	div.navsub		{margin-left:12px;}
    	a.navsub		{text-decoration:none;color:###txtDarkColor#;font-size : 11px;}
    	a.navsub_active	{text-decoration:none;color:###txtDarkColor#;font-size : 11px;font-weight:bold;}
		
		.comment{font-size : 10px;color:###txtDarkColor#;}
		.commentHead{font-size : 10px;color:###txtBrightColor#;}
		.copy { font-style:italic;font-size : 8pt;color:###txtBrightColor#;}
		
		div.hr{border-color:red;border-style:solid;border-color:###bgDarkColor#;border-width:0px 0px 1px 0px;margin:0px 16px 4px 0px;}
		.tbl{empty-cells:show;}
		.tblHead{padding-left:5px;padding-right:5px;background-color:###bgDarkColor#;color:###txtBrightColor#}
		.tblContent			{padding-left:5px;padding-right:5px;border:1px solid ###bgDarkColor#;background-color:###bgBrightColor#;}
		.tblContentRed		{padding-left:5px;padding-right:5px;border:1px solid ###bgDarkColor#;background-color:###redColor#;}
		.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid ###bgDarkColor#;background-color:###greenColor#;}
		.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid ###bgDarkColor#;background-color:###yellowColor#;}
		
		td.inactivTab{border-style:solid;border-color:###bgDarkColor#;padding: 0px 5px 0px 5px;background-color:###bgBrightColor#;}
		a.inactivTab{color:###txtDarkColor#;text-decoration:none;}
		td.activTab{border-style:solid;border-color:###bgDarkColor#;border-width:1px 1px 0px 1px ;padding: 2px 10px 2px 10px;background-color:###bgDarkColor#;}
		a.activTab{color:###txtBrightColor#;text-decoration:none;}
		td.tab {border-color:###bgDarkColor#;border-width:1px;border-style:solid;border-top:0px;padding:10px;background-color:###bgBrightColor#;}
		td.tabtop {border-style:solid;border-color:###bgDarkColor#;border-width:0px 0px 1px 0px ;padding: 0px 1px 0px 0px;}
		
		
		.CheckOk{color:##33AA33;font-size : 12px;}
		.CheckError{color:##DD3333;font-size : 12px;}
		
		input{padding-left:3px;padding-right:2px;margin:3px 1px 3px 1px;color:###bgDarkColor#;border-style:solid;border-width:1px;border-color:###btnColor#;}
		.button,.submit,.reset {background-color:###bgDarkColor#;color:###txtBrightColor#;font-weight:bold;padding-left:10px;padding-right:10px;margin:0px;}
		select {font-size : 11px;color:###txtDarkColor#;margin:3px 0px 3px 0px;}
		.checkbox,.radio {border:0px;border-color:###borderColor#;}
		a{color:###txtDarkColor#;}
		
		
		<!---/*
		.darker{background-color:###bgDarkColor#;}
		.brigther{background-color:###bgBrightColor#;}
		
		*/--->
		</style></cfoutput>
	</head>
	<body <cfif structKeyExists(attributes,"onload")>onload="<cfoutput>#attributes.onload#</cfoutput>"</cfif>>

<cfoutput>
<table align="center" cellpadding="0" cellspacing="0" border="0">
<colgroup>
    <col width="4">
    <col width="158">
	<cfif hasNavigation><col width="1"></cfif>
	<col width="815">
	<col width="9">
</colgroup>
<tr>
	<td colspan="5" align="right"><a href="#other#.cfm"><cfmodule template="img.cfm" src="#ad#-to.gif" vspace="5" /></a></td>
</tr>
<tr>
	<td colspan="2" width="162" height="91" valign="bottom"><a href="#request.self#"><cfmodule template="img.cfm" src="#ad#-railo.gif" hspace="22" vspace="5"  /></a></td>
	<td colspan="3" width="823" height="91" valign="bottom" align="right"><cfmodule template="img.cfm" src="#ad#-admin.gif" vspace="3" /><cfmodule template="img.cfm" src="#ad#-aloha-#server.ColdFusion.ProductLevel#-1.gif" hspace="4"/></td>
</tr>
<tr>
	<td rowspan="2" width="4" valign="top" background="resources/img/#ad#-shadow-left-2.gif.cfm"><cfmodule template="img.cfm" src="#ad#-shadow-left-1.gif" width="4" height="311" /></td>
	<td valign="top"  width="158" background="resources/img/bg-nav.gif.cfm"><cfmodule template="img.cfm" src="#ad#-left.gif" />
		<cfif hasNavigation><div style="margin:10px 0px 0px 20px;">
			<cfoutput>#attributes.navigation#<div class="navtop"><a class="navtop" href="#request.self#?action=logout">Logout</a></div></cfoutput>
		</div></cfif>
	</td>
	<cfif hasNavigation><td rowspan="2" width="1"></td></cfif>
	<td width="815" height="31" valign="top" align="right" style="background-image:url('resources/img/bg-content.gif.cfm');background-repeat:repeat-y;background-color:###bgBrightColor#" background="resources/img/bg-content.gif.cfm"><cfmodule template="img.cfm" src="tp.gif" width="8" height="8" align="top" style="margin:0px 4px 18px 0px;" /><cfmodule template="img.cfm" src="#ad#-aloha-#server.ColdFusion.ProductLevel#-2.gif" width="44" height="31" align=top />
		<div id="content" style="text-align:left;margin:0px 10px 10px 20px;">
			<cfoutput><cfif len(attributes.title) GT 0><h1>#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></h1></cfif>
					#thistag.generatedContent#</cfoutput>
		
		</div>
	</td>
	<td valign="top" width="7" background="resources/img/#ad#-shadow-right-2.gif.cfm" style="background-repeat:repeat-y;"><cfmodule template="img.cfm" src="#ad#-aloha-#server.ColdFusion.ProductLevel#-3.gif" /><cfmodule template="img.cfm" src="#ad#-shadow-right-1.gif" width="4" height="311" /></td>
</tr>
<tr>
	<td width="158" height="22" background="resources/img/bg-nav.gif.cfm"></td>
	<td width="815" height="22" background="resources/img/bg-content.gif.cfm" align="right"><cfmodule template="img.cfm" src="#ad#-right.gif" width="23" height="22" /></td>
	<td height="22"><cfmodule template="img.cfm" src="#ad#-shadow-right-3.gif" /></td>
</tr>
<tr>
	<td colspan="2" width="162" height="4" background="resources/img/#ad#-shadow-bottom-2.gif.cfm" style="background-repeat:repeat-x;"><cfmodule template="img.cfm" src="#ad#-shadow-bottom-1.gif" /></td>
	<td colspan="#iif(hasNavigation,de(3),de(2))#" width="823" height="4" background="resources/img/#ad#-shadow-bottom-2.gif.cfm" style="background-repeat:repeat-x;" align="right"><cfmodule template="img.cfm" src="#ad#-shadow-bottom-3.gif" /></td>
</tr>
<tr>
			<td colspan="#iif(hasNavigation,de(5),de(4))#" class="copy"><cfoutput>&copy; #Year(Now())# by Railo Technologies GmbH Switzerland</cfoutput><br><br></td>
		</tr>
</cfoutput>
</table>
</body>
</html>




<cfset thistag.generatedcontent="">

</cfif>
<cfparam name="url.showdebugoutput" default="no"><cfsetting showdebugoutput="#url.showdebugoutput#">