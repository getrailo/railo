<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">

<cfadmin 
	action="getApplicationSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="appSettings">
<cfadmin 
	action="getApplicationListener"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="listener">
	

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction1" default="none">
<cfparam name="form.mainAction2" default="none">
<cfparam name="form.subAction" default="none">

<cfif hasAccess>
	<cftry>
	<!--- generell --->
		<cfswitch expression="#form.mainAction1#">
		<!--- UPDATE --->
			<cfcase value="#stText.Buttons.Update#">
			
				<cfif form.scriptProtect EQ "custom">
					<cfparam name="form.scriptProtect_custom" default="none">
					<cfset form.scriptProtect=form.scriptProtect_custom>
				</cfif>
			
				<cfadmin 
					action="updateApplicationSetting"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					scriptProtect="#form.scriptProtect#"
					AllowURLRequestTimeout="#structKeyExists(form,'AllowURLRequestTimeout') and form.AllowURLRequestTimeout#"
					requestTimeout="#CreateTimeSpan(form.request_days,form.request_hours,form.request_minutes,form.request_seconds)#"
					remoteClients="#request.getRemoteClients()#">
				
			</cfcase>
		<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
			
				<cfadmin 
					action="updateApplicationSetting"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					scriptProtect=""
					AllowURLRequestTimeout=""
					requestTimeout=""
					
					remoteClients="#request.getRemoteClients()#">
				
			</cfcase>
		</cfswitch>
	
	<!--- listener --->
		<cfswitch expression="#form.mainAction2#">
		<!--- UPDATE --->
			<cfcase value="#stText.Buttons.Update#">
			
				<cfadmin 
					action="updateApplicationListener"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					listenerType="#form.type#"
					listenerMode="#form.mode#"
					remoteClients="#request.getRemoteClients()#">
				
			</cfcase>
		<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
			
				<cfadmin 
					action="updateApplicationListener"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					listenerType=""
					listenerMode=""
					
					remoteClients="#request.getRemoteClients()#">
				
			</cfcase>
		</cfswitch>
		<cfcatch>
			<cfset error.message=cfcatch.message>
			<cfset error.detail=cfcatch.Detail>
		</cfcatch>
	</cftry>
</cfif>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<!--- 
Error Output --->
<cfset printError(error)>

<!--- script to enable/disable script-protect 'custom' checkboxes --->
<cfsavecontent variable="headText">
	<script type="text/javascript">
		function sp_clicked()
		{
			var iscustom = $('#sp_radio_custom')[0].checked;
			var tbl = $('#customoptionstbl').css('opacity', (iscustom ? 1:.5));
			var inputs = $('input', tbl).prop('disabled', !iscustom);
			if (!iscustom)
			{
				inputs.prop('checked', false);
			}
		}
		$(function(){
			$('#sp_options input.radio').bind('click change', sp_clicked);
			sp_clicked();
		});
	</script>
</cfsavecontent>
<cfhtmlhead text="#headText#" />

<cfoutput>
	<cfif not hasAccess>
		<cfset noAccess(stText.setting.noAccess)>
	</cfif>
	
	<div class="pageintro">
		<cfif request.adminType EQ "server">
			#stText.application.Server#
		<cfelse>
			#stText.application.Web#
		</cfif>
	</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<!---<h3>Script-protect</h3>
		<div class="itemintro">#stText.application.scriptProtectDescription#</div>--->
		<table class="maintbl">
			<tbody>
				<!--- script-protect --->
				<tr>
					<th scope="row">
						#stText.application.scriptProtect#
						<div class="comment">#stText.application.scriptProtectDescription#</div>
					</th>
					<td>
						<cfif hasAccess>
							<cfset isNone=appSettings.scriptProtect EQ  "none">
							<cfset isAll=appSettings.scriptProtect EQ  "all">
							<cfset isCustom=not isNone and not isAll>
							<ul class="radiolist" id="sp_options">
								<li>
									<label>
										<input type="radio" class="radio" name="scriptProtect" value="none" <cfif isNone>checked="checked"</cfif>>
										<b>none</b>
									</label>
									<div class="comment">#stText.application.scriptProtectNone#</div>
								</li>
								<li>
									<label>
										<input type="radio" class="radio" name="scriptProtect" id="sp_radio_custom" value="custom" <cfif isCustom>checked="checked"</cfif>>
										<b>custom:</b>
									</label>
									<div class="comment">#stText.application.scriptProtectCustom#</div>
									<table class="maintbl autowidth" id="customoptionstbl">
										<thead>
											<tr>
												<th>cgi</th>
												<th>cookie</th>
												<th>form</th>
												<th>url</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><input type="checkbox" class="checkbox" name="scriptProtect_custom" 
												<cfif ListFindNoCase(appSettings.scriptProtect,'cgi')> checked="checked"</cfif> value="cgi"></td>
												<td><input type="checkbox" class="checkbox" name="scriptProtect_custom" 
												<cfif ListFindNoCase(appSettings.scriptProtect,'cookie')> checked="checked"</cfif> value="cookie"></td>
												<td><input type="checkbox" class="checkbox" name="scriptProtect_custom" 
												<cfif ListFindNoCase(appSettings.scriptProtect,'form')> checked="checked"</cfif> value="form"></td>
												<td><input type="checkbox" class="checkbox" name="scriptProtect_custom" 
												<cfif ListFindNoCase(appSettings.scriptProtect,'url')> checked="checked"</cfif> value="url"></td>
											</tr>
										</tbody>
									</table>
								</li>
								<li>
									<label>
										<input type="radio" class="radio" name="scriptProtect" value="all" <cfif isAll>checked="checked"</cfif>>
										<b>all</b>
									</label>
									<div class="comment">#stText.application.scriptProtectAll#</div>
								</li>
							</ul>
						<cfelse>
							<!---<input type="hidden" name="scriptProtect" value="#appSettings.scriptProtect#">--->
							<b>#appSettings.scriptProtect#</b>
						</cfif>
					</td>
				</tr>
<!---			</tbody>
		</table>
		
		<h3>Request timeout</h3>
		<table class="maintbl">
			<tbody>--->
				<!--- request timeout --->
				<tr>
					<th scope="row">#stText.application.RequestTimeout#</th>
					<td>
						<cfset timeout=appSettings.requestTimeout>
						<table class="maintbl" style="width:auto">
							<thead>
								<tr>
									<th>#stText.General.Days#</th>
									<th>#stText.General.Hours#</th>
									<th>#stText.General.Minutes#</th>
									<th>#stText.General.Seconds#</th>
								</tr>
							</thead>
							<tbody>
								<cfif hasAccess>
									<tr>
										<td><cfinput type="text" name="request_days" value="#appSettings.requestTimeout_day#" 
											class="number" required="yes" validate="integer" 
											message="#stText.Scopes.TimeoutDaysValue#request#stText.Scopes.TimeoutEndValue#"></td>
										<td><cfinput type="text" name="request_hours" value="#appSettings.requestTimeout_hour#" 
											class="number" required="yes" validate="integer" 
											message="#stText.Scopes.TimeoutHoursValue#request#stText.Scopes.TimeoutEndValue#"></td>
										<td><cfinput type="text" name="request_minutes" value="#appSettings.requestTimeout_minute#" 
											class="number" required="yes" validate="integer" 
											message="#stText.Scopes.TimeoutMinutesValue#request#stText.Scopes.TimeoutEndValue#"></td>
										<td><cfinput type="text" name="request_seconds" value="#appSettings.requestTimeout_second#" 
											class="number" required="yes" validate="integer" 
											message="#stText.Scopes.TimeoutSecondsValue#request#stText.Scopes.TimeoutEndValue#"></td>
									</tr>
								<cfelse>
									<tr>
										<td class="right"><b>#appSettings.requestTimeout_day#</b></td>
										<td class="right"><b>#appSettings.requestTimeout_hour#</b></td>
										<td class="right"><b>#appSettings.requestTimeout_minute#</b></td>
										<td class="right"><b>#appSettings.requestTimeout_second#</b></td>
									</tr>
								</cfif>
							</tbody>
						</table>
						<div class="comment">#stText.application.RequestTimeoutDescription#</div>
					</td>
				</tr>
				<!--- request timeout url --->
				<tr>
					<th scope="row">#stText.application.AllowURLRequestTimeout#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" name="AllowURLRequestTimeout" value="true" class="checkbox"
							<cfif appSettings.AllowURLRequestTimeout>  checked="checked"</cfif>>
						<cfelse>
							<!---<input type="hidden" name="AllowURLRequestTimeout" value="#appSettings.AllowURLRequestTimeout#">--->
							<b>#yesNoFormat(appSettings.AllowURLRequestTimeout)#</b>
						</cfif>
						<div class="comment">#stText.application.AllowURLRequestTimeoutDesc#</div>
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
							<input type="submit" class="button submit" name="mainAction1" value="#stText.Buttons.Update#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction1" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>

	<h2>#stText.application.listener#</h2>
	<div class="itemintro">#stText.application.listenerDescription#</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- listener type --->
				<tr>
					<th scope="row">
						#stText.application.listenerType#
						<cfif hasAccess>
							<!--- PK: disabled, because it only said "please select an option"
							<div class="comment">#stText.application.listenerTypeDescription#</div>
							--->
						</cfif>
					</th>
					<td>
						<cfif hasAccess>
							<ul class="radiolist">
								<cfloop index="key" list="none,classic,modern,mixed">
									<li>
										<label>
											<input type="radio" class="radio" name="type" value="#key#" <cfif listener.type EQ key>checked="checked"</cfif>>
											<b>#stText.application['listenerType_' & key]#</b>
										</label>
										<div class="comment">#stText.application['listenerTypeDescription_' & key]#</div>
									</li>
								</cfloop>
							</ul>
						<cfelse>
							<!---<input type="hidden" name="type" value="#listener.type#">--->
							<b>#listener.type#</b>
							<div class="comment">#stText.application['listenerTypeDescription_' & listener.type]#</div>
						</cfif>
					</td>
				</tr>
				
				<!--- listener mode --->
				<tr>
					<th>#stText.application.listenerMode#
						<cfif hasAccess>
							<div class="comment">#stText.application.listenerModeDescription#</div>
						</cfif>
					</th>
					<td>
						<cfif hasAccess>
							<ul class="radiolist">
								<cfloop index="key" list="curr,root,curr2root">
									<li>
										<label>
											<input type="radio" class="radio" name="mode" value="#key#" <cfif listener.mode EQ key>checked="checked"</cfif>>
											<b>#stText.application['listenerMode_' & key]#</b>
										</label>
										<div class="comment">#stText.application['listenerModeDescription_' & key]#</div>
									</li>
								</cfloop>
							</ul>
						<cfelse>
							<!---<input type="hidden" name="type" value="#listener.mode#">--->
							<b>#listener.mode#</b>
							<div class="comment">#stText.application['listenerModeDescription_' & listener.mode]#</div>
						</cfif>
					</td>
				</tr>
				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="3">
				</cfif>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="2">
							<input type="submit" class="button submit" name="mainAction2" value="#stText.Buttons.Update#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction2" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
</cfoutput>