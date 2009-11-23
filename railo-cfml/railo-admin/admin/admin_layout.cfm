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
		
		
		<cfset yellowColor=iif(request.adminType EQ "web",de('FFFF66'),de('FFFF66	'))>
		
		
		
		body {background-image:url('resources/img/#ad#-back.png.cfm');background-repeat:repeat-x;background-color:##f7f7f7;margin-top:0px;margin-left:0px;}
		body, tr, td,div {font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 9pt;color:##3c3e40;}
		h1 {font-weight:normal;font-family:'Helvetica Neue', Arial, Helvetica, sans-serif;font-size : 20pt;color:##568bc1;}
		h2 {height:6pt;font-size : 14pt;font-weight:normal;color:##568bc1;}
		
		div.navtop		{margin-top:8px;margin-bottom:3px;color:##333333;font-weight:bold;font-size : 9pt;}
    	a.navtop		{text-decoration:none;font-weight:bold;font-size : 9pt;}
		
    	a.navsub		{text-decoration:none;color:##568bc1;font-size : 8pt;}
    	a.navsub_active	{text-decoration:none;color:##568bc1;font-size : 8pt;font-weight:bold;}
		
		.comment{font-size : 10px;color:##787a7d;}
		.commentHead{font-size : 10px;color:##DFE9F6;}
		.copy { font-size : 8pt;color:##666666;}
		
		div.hr{border-color:red;border-style:solid;border-color:##e0e0e0;border-width:0px 0px 1px 0px;margin:0px 16px 4px 0px;}
		.tbl{empty-cells:show;}
		.tblHead{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;background-color:##f2f2f2;color:##3c3e40}
		.tblContent			{padding-left:5px;padding-right:5px;border:1px solid ##e0e0e0;}
		.tblContentRed		{padding-left:5px;padding-right:5px;border:1px solid ##cc0000;background-color:##f9e0e0;}
		.tblContentGreen	{padding-left:5px;padding-right:5px;border:1px solid ##009933;background-color:##e0f3e6;}
		.tblContentYellow	{padding-left:5px;padding-right:5px;border:1px solid ##ccad00;background-color:##fff9da;}
		
		td.inactivTab{border-style:solid;border-color:##e0e0e0;padding: 0px 5px 0px 5px;background-color:white;}
		a.inactivTab{color:##3c3e40;text-decoration:none;}
		
		td.activTab{border-style:solid;border-color:##e0e0e0;border-width:1px 1px 0px 1px ;padding: 2px 10px 2px 10px;background-color:##e0e0e0;}
		a.activTab{font-weight:bold;color:##3c3e40;text-decoration:none;}
		
		td.tab {border-color:##e0e0e0;border-width:1px;border-style:solid;border-top:0px;padding:10px;background-color:white;}
		td.tabtop {border-style:solid;border-color:##e0e0e0;border-width:0px 0px 1px 0px ;padding: 0px 1px 0px 0px;}
		
		
		.CheckOk{font-weight:bold;color:##009933;font-size : 12px;}
		.CheckError{font-weight:bold;color:##cc0000;font-size : 12px;}
		
		input{
		background: url('resources/img/input-shadow.png.cfm') repeat-x 0 0;
background-color:white;
		
		padding-left:3px;padding-right:2px;padding-top:3px;padding-bottom:3px;margin:3px 1px 3px 1px;color:##3c3e40;border-style:solid;border-width:1px;border-color:##e0e0e0;}
		.button,.submit,.reset {
		background: url('resources/img/input-button.png.cfm') repeat-x 0 0;
		background-color:##f2f2f2;color:##3c3e40;font-weight:bold;padding-left:10px;padding-right:10px;margin:0px;}
		select {font-size : 11px;color:##3c3e40;margin:3px 0px 3px 0px;}
		.checkbox,.radio {border:0px;}
		
		a{color:##568bc1;}
		
		
		<!---/*
		.darker{background-color:##e0e0e0;}
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
	<col width="1">
	<col width="9">
</colgroup>
<tr>
	<td colspan="6" align="right"><a href="#other#.cfm"><cfmodule template="img.cfm" src="#ad#-to.png" vspace="5" /></a></td>
</tr>
<tr>
	<td colspan="6" width="162" height="91" valign="bottom">
    <a href="#request.self#"><cfmodule template="img.cfm" src="railo.png" width="92" height="59" vspace="10"/></a>
    <cfmodule template="img.cfm" src="hr.png"  hspace="25" vspace="10" />
    <cfmodule template="img.cfm" src="#ad#-admin.png"  vspace="17" /></td>
</tr>
<tr>
	<td rowspan="2" width="4" valign="top"><cfmodule template="img.cfm" vspace="77" src="shadow-left.gif" /></td>
	<td valign="top"  width="158" style="background-image:url(resources/img/back-left.png.cfm);"><cfmodule template="img.cfm" src="left.png" />
		<cfif hasNavigation><div style="margin:10px 0px 0px 20px;">
			<cfoutput>#attributes.navigation#<div style="padding-top:30px;padding-bottom:30px;"><a class="navsub" style="font-size:9pt;" href="#request.self#?action=logout"><img border="0" src="resources/img/arrow.gif.cfm"  width="4" height="7" /> Logout</a></div></cfoutput>
		</div></cfif><br><br>
	</td>
	<cfif hasNavigation><td rowspan="1" width="1" style="background-color:##d2d2d2;"></td></cfif>
	<td width="815" height="31" valign="top" align="right" style="background-color:white" >
    <cfmodule template="img.cfm" src="tp.gif" width="8" height="8" align="top" style="margin:0px 4px 18px 0px;" />
		<div id="content" style="text-align:left;margin:0px 10px 10px 20px;">
			<cfoutput><cfif len(attributes.title) GT 0><h1>#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></h1></cfif>
					#thistag.generatedContent#</cfoutput>
		
		</div>
	</td>
    
	<td rowspan="1" width="1" style="background-color:##eeeeee;"></td>
	<td valign="top" width="7"><cfmodule vspace="77" template="img.cfm" src="shadow-right.gif" width="11" height="329" /></td>
</tr>

<tr>

	<td colspan="2" height="1" style="background-color:##e6e6e6;"></td>
	<td colspan="2" height="1" style="background-color:##eeeeee;"></td>
	<td colspan="2" height="1"></td>
</tr>
<tr>
			<td colspan="#iif(hasNavigation,de(6),de(5))#" align="center" class="copy"><cfoutput>&copy; #Year(Now())# <a href="http://www.getrailo.com" target="_blank">Railo Technologies GmbH Switzerland</a>. All Rights Reserved. | Designed by <a href="http://www.blueriver.com/from/railo/" target="_blank">Blue River Interactive Group, Inc.</a>
			</cfoutput><br><br></td>
		</tr>
</cfoutput>
</table>
</body>
</html>




<cfset thistag.generatedcontent="">

</cfif>
<cfparam name="url.showdebugoutput" default="no"><cfsetting showdebugoutput="#url.showdebugoutput#">