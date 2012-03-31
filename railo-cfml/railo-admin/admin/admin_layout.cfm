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
		otherURL=other&".cfm"
		if(structKeyExists(url,'action'))otherURL&="?action="&url.action;
	</cfscript>
	<cfset request.mode="full">
	
<cfcontent reset="yes" /><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<cfoutput>
<html>
<head>
	<title>Railo #ucFirst(request.adminType)# Administrator</title>
	<link rel="stylesheet" href="../getfile.cfm?file=css/style.css" type="text/css" />
	<script src="../getfile.cfm?file=js/jquery-1.7.2.min.js"></script>
	<script src="../getfile.cfm?file=js/jquery.blockUI.js"></script>
	<script src="../getfile.cfm?file=js/admin.js"></script>
</head>
<body id="body" class="#request.adminType#" <cfif structKeyExists(attributes,"onload")>onload="#attributes.onload#"</cfif>>
	<center>
		<table align="center" cellpadding="0" cellspacing="0" border="0"<cfif session.screenMode EQ "full"> width="100%"</cfif>>
			<!---<colgroup>
				<col width="11">
				<col width="170">
				<cfif hasNavigation><col width="1"></cfif>
				<col width="100%">
				<col width="1">
				<col width="9">
			</colgroup>
			--->
			<colgroup>
				<col width="11">
				<col width="170">
				<cfif hasNavigation><col width="1"></cfif>
				<cfif session.screenMode EQ "full"><col width="100%"><cfelse><col width="825"></cfif>
				<col width="11">
			</colgroup>
			<tr>
				<td width="#hasNavigation?182:181#" colspan="#hasNavigation?3:2#" valign="bottom"><cfif session.screenMode NEQ "full"><img src="../getfile.cfm?file=img/tp.gif" alt="" width="1" height="34" /><br></cfif>
				<a href="#request.self#"><cfif session.screenMode EQ "full"><img src="../getfile.cfm?file=img/#ad#-railo-small.png" hspace="5" vspace="3"/><cfelse><img src="../getfile.cfm?file=img/#ad#-railo.png" width="102" height="69" vspace="5"/></cfif></a></td>
				
				<td align="right" valign="bottom"><a <cfif ad EQ "web">href="#otherURL#"</cfif>><img src="../getfile.cfm?file=img/left-tab-#ad#.png" /></a><a <cfif ad EQ "server">href="#otherURL#"</cfif>><img src="../getfile.cfm?file=img/right-tab-#ad#.png" /></a></td>
				<td width="11" rowspan="2"><img src="../getfile.cfm?file=img/tp.gif" alt="" width="11" height="1" /></td>
			</tr>
			<tr>
				<td width="11"><img src="../getfile.cfm?file=img/tp.gif" alt="" width="11" height="1" /></td>
				<td width="170" style="background-image:url('../getfile.cfm?file=img/left.png');"><img src="../getfile.cfm?file=img/left.png" hspace="6" alt="" /></td>
				<cfif hasNavigation><td width="1" style="background-color:##d2d2d2;"></td></cfif>
				<td style="background-color:white"></td>
			</tr>
			
			<tr>
				<td width="11" valign="top"><img src="../getfile.cfm?file=img/shadow-left.gif" vspace="#session.screenMode EQ "full"?140:67#" /></td>
				<td valign="top"  width="170" style="background-color:##e6e6e6;">
				<script type="text/javascript">
					document.write('<a href="#request.self#?realScreenSize='+$(document).width()+'&screenmode=#session.screenmode=='compact'?'full':'compact'#<cfif isDefined('url.action')>&action=#url.action#</cfif>">');
				</script>
				
					<img src="../getfile.cfm?file=img/#session.screenMode EQ 'full'?'min':'max'#l.png" hspace="8" width="22" height="22" border="0"/></a><br>
					<cfif hasNavigation>
						<div style="margin:10px 0px 0px 20px;">
							#attributes.navigation#
						</div>
					</cfif>
					<br><br>
				</td>
				<cfif hasNavigation>
					<td width="1" style="background-color:##d2d2d2;"></td>
				</cfif>
				<td height="31" valign="top" align="right" style="background-color:white">
					<img hspace="8" src="../getfile.cfm?file=img/tp.gif" width="22" height="22"/><br>
						<div id="title" style="text-align:left;margin:0px 30px 10px 20px;">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
						
						<colgroup>
							<col width="4">
							<col>
							<cfif hasNavigation><col>
							<col></cfif>
							<col width="4">
							<col width="20">
						</colgroup>
						<tr>
							<td><img src="../getfile.cfm?file=img/box-left.png" /></td>
							<td style="background-image:url('../getfile.cfm?file=img/box-bg.png');background-repeat:repeat-x;">
								<cfif len(attributes.title) GT 0><span class="box"><img src="../getfile.cfm?file=img/tp.gif" width="7" height="1" />#attributes.title#<cfif structKeyExists(request,'subTitle')> - #request.subTitle#</cfif></span></cfif></td>
							<cfif hasNavigation>
							<td width="352" align="right" style="background-image:url('../getfile.cfm?file=img/box-bg.png');background-repeat:repeat-x;"><img src="../getfile.cfm?file=img/box-del.png" /></td>
							<td width="65" align="center" style="background-image:url('../getfile.cfm?file=img/box-bg.png');background-repeat:repeat-x;"><a class="navsub" style="font-size:9pt;" href="#request.self#?action=logout">Logout</a></td>
							</cfif>
							
							<td><img src="../getfile.cfm?file=img/box-right.png" /></td>
							<td><img src="../getfile.cfm?file=img/tp.gif" width="20" height="1" /></td>
						</tr>
						</table><br><br>
						</div>
					 <div id="content" style="text-align:left;margin:0px 30px 10px 30px;">         
					   #thistag.generatedContent#
					
					</div>
				</td>
				
				<td width="11" valign="top"><img vspace="#session.screenMode EQ "full"?140:67#" src="../getfile.cfm?file=img/shadow-right.gif" /></td>
			</tr>
			<tr>
				<td colspan="#iif(hasNavigation,de(5),de(4))#" align="center" class="copy">
					&copy; #Year(Now())#
					<a href="http://www.getrailo.com" target="_blank">Railo Technologies GmbH Switzerland</a>.
					All Rights Reserved. |
					Designed by <a href="http://www.blueriver.com/from/railo/" target="_blank">Blue River Interactive Group, Inc.</a>
					<br><br>
				</td>
			</tr>
		</table>
	</center>
	<cfif false and StructKeyExists(application,'notTranslated')>
		<cfset keys=structKeyArray(application.notTranslated)>
		<cfset ArraySort(keys,'textnocase')>
	<!--
The following text is not translated the the current language
<cfloop  array="#keys#" index="key"><data key="#key#">#trim(application.notTranslated[key])#</data>
</cfloop>
	-->
	</cfif>
</body>
</html>
</cfoutput>
	<cfset thistag.generatedcontent="">
</cfif>
<cfparam name="url.showdebugoutput" default="no">
<cfsetting showdebugoutput="#url.showdebugoutput#">