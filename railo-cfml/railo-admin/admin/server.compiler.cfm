<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">


<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfset dotNotUpper=true>
			<cfif isDefined('form.dotNotation') and form.dotNotation EQ "oc">
            	<cfset dotNotUpper=false>
            </cfif>
            <cfif not isDefined('form.supressWSBeforeArg')>
            	<cfset form.supressWSBeforeArg=false>
            </cfif>
            <cfif not isDefined('form.nullSupport')>
            	<cfset form.nullSupport=false>
            </cfif>
			
            
			<cfadmin 
				action="updateCompilerSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				nullSupport="#form.nullSupport#"
				dotNotationUpperCase="#dotNotUpper#"
                supressWSBeforeArg="#form.supressWSBeforeArg#"
				remoteClients="#request.getRemoteClients()#">
	
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			
			<cfadmin 
				action="updateCompilerSettings"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				nullSupport=""
				dotNotationUpperCase=""
				supressWSBeforeArg=""
				
				remoteClients="#request.getRemoteClients()#">
	
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Error Output --->
<cfset printError(error)>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>




<cfadmin 
	action="getCompilerSettings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="setting">


<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>

<cfoutput>
	<div class="pageintro">#stText.setting.compiler#</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Null Support --->
				<tr>
					<th scope="row">#stText.compiler.nullSupport#</th>
					<td>
						<cfif hasAccess && request.admintype EQ "server">
							<ul class="radiolist">
								<li>
									<!--- full --->
									<label>
										<input class="radio" type="radio" name="nullSupport" value="true"<cfif setting.nullSupport> checked="checked"</cfif>>
										<b>#stText.compiler.nullSupportFull#</b>
									</label>
									<div class="comment">#stText.compiler.nullSupportFullDesc#</div>
								</li>
								<li>
									<!--- partial --->
									<label>
										<input class="radio" type="radio" name="nullSupport" value="false"<cfif !setting.nullSupport> checked="checked"</cfif>>
										<b>#stText.compiler.nullSupportPartial#</b>
									</label>
									<div class="comment">#stText.compiler.nullSupportPartialDesc#</div>
								</li>
							</ul>
						<cfelse>
							<cfset strNullSupport=setting.nullSupport?"full":"partial">
							<input type="hidden" name="nullSupport" value="#setting.nullSupport#">
							<b>#stText.compiler["nullSupport"& strNullSupport]#</b><br />
							<div class="comment">#stText.compiler["nullSupport"& strNullSupport&"Desc"]#</div>
							<cfif request.admintype EQ "web"><div class="warning nofocus">#stText.compiler.nullSupportOnlyServer#</div></cfif>
						</cfif>
					</td>
				</tr>
				
				
				<!--- Dot Notation --->
				<tr>
					<th scope="row">#stText.setting.dotNotation#</th>
					<td>
						<cfif hasAccess>
							<ul class="radiolist">
								<li>
									<!--- original case --->
									<label>
										<input class="radio" type="radio" name="dotNotation" value="oc"<cfif !setting.dotNotationUpperCase> checked="checked"</cfif>>
										<b>#stText.setting.dotNotationOriginalCase#</b>
									</label>
									<div class="comment">#replace(stText.setting.dotNotationOriginalCaseDesc, server.separator.line, '<br />', 'all')#</div>
								</li>
								<li>
									<!--- upper case --->
									<label>
										<input class="radio" type="radio" name="dotNotation" value="uc"<cfif setting.dotNotationUpperCase> checked="checked"</cfif>>
										<b>#stText.setting.dotNotationUpperCase#</b>
									</label>
									<div class="comment">#replace(stText.setting.dotNotationUpperCaseDesc, server.separator.line, '<br />', 'all')#</div>
								</li>
							</ul>
						<cfelse>
							<cfset strDotNotation=setting.dotNotationUpperCase?"uc":"oc">
							<cfset strDotNotationID=setting.dotNotationUpperCase?"Upper":"Original">
							<input type="hidden" name="dotNotation" value="#strDotNotation#">
							<b>#stText.setting["dotNotation"& strDotNotationID &"Case"]#</b><br />
							<div class="comment">#replace(stText.setting["dotNotation"& strDotNotationID &"CaseDesc"], server.separator.line, '<br />', 'all')#</div>
						</cfif>
					</td>
				</tr>
				
				<!--- Supress Whitespace in front of cfargument --->
				<tr>
					<th scope="row">#stText.setting.supressWSBeforeArg#</th>
					<td>
						<cfif hasAccess>
        					<input type="checkbox" name="supressWSBeforeArg" value="true" <cfif setting.supressWSBeforeArg>checked="checked"</cfif> />
						<cfelse>
							<b>#yesNoFormat(setting.supressWSBeforeArg)#</b><br /><input type="hidden" name="supressWSBeforeArg" value="#setting.supressWSBeforeArg#">
						</cfif>
						<div class="comment">#stText.setting.supressWSBeforeArgDesc#</div>
					</td>
				</tr>

				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="2">
				</cfif>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="2">
							<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input type="reset" class="button reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
</cfoutput>