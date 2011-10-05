<cfset sHeight = 73>
<cfoutput>

<cfset sClose = createHeader(stText.help.logotitle)>

<div id="boxes">
	<cfif len(trim(sVideo))>
		<div id="movie" class="window">
			<div style="border:0px solid green;height:#sHeight#px;width:720px">
				#createHeader(stText.help.videohelp)#
			</div>
			<div id="innerHelp" align="center">
				<cfvideoplayer video="#sVideo#" width="450" height="360" allowfullscreen="true"><br>
				#sVideoDescription#
				<br style="margin-bottom:10px">
			</div>
		</div>
	</cfif>
	
	<cfif len(trim(sGlobal))>
		<div id="global" class="window" align="left">
			<div style="border:0px solid green;height:#sHeight#px;width:710px">
				#createHeader(stText.help.globalhelp)#
			</div>
			<div id="innerHelp">
				<cftry>
					<cfhttp method="get" url="#sGlobal#" timeout="1"></cfhttp>
					<cfcatch>
						<cfset sHelp = "<b>#stText.help.globalhelpnotconnected#</b>">
					</cfcatch>
				</cftry>
				<div style="clear:both"></div>
				#cfhttp.fileContent#
				<br style="margin-bottom:10px">
			</div>
			<div style="background-color:##ffffff;width:710px;height:20px;">
			</div>
		</div>
	</cfif>

	<div id="localHelp" class="window" align="left">
		<div style="border:0px solid green;height:#sHeight#px;width:710px">
			#createHeader(stText.help.localhelp)#
		</div>
		<div id="innerHelp">
			<form action="#sHelpKey#" method="post">
			#sContent#
			<b>#stText.help.addHelpNote#</b>
				<input type="hidden" name="addHelp" value="done">
				<textarea name="addLocalHelp" style="width:670px;height:50px"><!---
				---></textarea><br>
				<input type="Submit" value="#stText.help.addNote#">
			</form>
			<br style="margin-bottom:10px">
		</div>
	</div>
	<!-- Mask to cover the whole screen -->
	<div id="mask"></div>
</div>

</cfoutput>

<cffunction name="createHeader" output="No" returntype="string">
	<cfargument name="sTitle" required="Yes">
	<cfsavecontent variable="local.sClose"><cfoutput>
		<div style="float:left;">
			<cfmodule template="../img.cfm" src="corner-top-left.png">
		</div>
		<div style="background-color:##ffffff;float:left;height:#sHeight#px;">
			<div style="float:left;margin-left:15px;margin-top:20px">
				<cfmodule template="../img.cfm" src="railo_small.png" alt="#stText.help.logotitle#" title="#stText.help.logotitle#">
			</div>
			<div style="float:left;width:600px;height:31px;padding-top:20px;text-align:center;"><h2 class="headline_2"><b>#arguments.sTitle#</b></h2></div>
			<div style="float:right;margin-top:-8px;margin-right:0px"><a href="##" class="close"/>
				<cfmodule template="../img.cfm" src="close.png" alt="#stText.help.closeWindow#" title="#stText.help.closeWindow#">
			</a></div>
		</div>
	</cfoutput></cfsavecontent>
	<cfreturn sClose>
</cffunction>