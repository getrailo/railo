<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfset error.message="">
<cfset error.detail="">

<cfset stText.setting.typeChecking="UDF Type Checking">
<cfset stText.setting.typeCheckingDesc="If disabled Railo ignores type defintions with function arguments and return values">
<cfparam name="stText.general.elements" default="item(s)">

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
	<cfobjectcache action="size" result="qrySize">
    <cfcatch>
    	<cfset qrySize=-1>
    </cfcatch>
</cftry>


<cfset btnClearTemplateCache  = stText.setting.templateCacheClearCount>
<cfset btnClearQueryCache     = stText.setting.queryCacheClear>
<cfset btnClearComponentCache = stText.setting.componentCacheClear>
<cfset btnClearCTCache        = stText.setting.ctCacheClear>

<cfif hasAccess>
	<cftry>
		<cfswitch expression="#form.mainAction#">
		
			<cfcase value="#btnClearComponentCache#">
				<cfset componentCacheClear()>
			</cfcase>
			<cfcase value="#btnClearCTCache#">
				<cfset ctCacheClear()>
			</cfcase>
			<cfcase value="#btnClearTemplateCache#">
				<cfset pagePoolClear()>
			</cfcase>
			<cfcase value="#btnClearQueryCache#">
				<cfobjectcache action="clear">
			</cfcase>
			<!--- Update ---->
			<cfcase value="#stText.Buttons.Update#">
				<cfadmin 
					action="updatePerformanceSettings"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					inspectTemplate="#form.inspectTemplate#"
					typeChecking="#!isNull(form.typeChecking) and form.typeChecking EQ true#"
					remoteClients="#request.getRemoteClients()#"
					>
			
			</cfcase>
		<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
				<cfadmin 
					action="updatePerformanceSettings"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					
					inspectTemplate=""
					typeChecking=""
					
					remoteClients="#request.getRemoteClients()#"
					>
			
			</cfcase>
		</cfswitch>
		<cfcatch>
			<cfset error.message=cfcatch.message>
			<cfset error.detail=cfcatch.Detail>
		</cfcatch>
	</cftry>
</cfif>

<cfadmin 
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	action="getPerformanceSettings"
	returnVariable="Settings">
	
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>
<!--- 
Create Datasource --->

<cfoutput>
	<cfif not hasAccess>
		<cfset noAccess(stText.setting.noAccess)>
	</cfif>

	<div class="pageintro">#stText.setting.cacheDesc#</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Template Cache for Request --->
				<tr>
					<th scope="row">#stText.setting.inspectTemplate#</th>
					<td>
						<cfif hasAccess>
							<ul class="radiolist">
								<li>
									<!--- never --->
									<label>
										<input class="radio" type="radio" name="inspectTemplate" value="never"<cfif settings.inspectTemplate EQ "never"> checked="checked"</cfif>>
										<b>#stText.setting.inspectTemplateNever#</b>
									</label>
									<div class="comment">#stText.setting.inspectTemplateNeverDesc#</div>
								</li>
								<li>
									<!--- once --->
									<label>
										<input class="radio" type="radio" name="inspectTemplate" value="once"<cfif settings.inspectTemplate EQ "once"> checked="checked"</cfif>>
										<b>#stText.setting.inspectTemplateOnce#</b>
									</label>
									<div class="comment">#stText.setting.inspectTemplateOnceDesc#</div>
								</li>
								<li>
									<!--- always --->
									<label>
										<input class="radio" type="radio" name="inspectTemplate" value="always"<cfif settings.inspectTemplate EQ "always"> checked="checked"</cfif>>
										<b>#stText.setting.inspectTemplateAlways#</b>
									</label>
									<div class="comment">#stText.setting.inspectTemplateAlwaysDesc#</div>
								</li>
							</ul>
						<cfelse>
							<cfif ListFindNoCase("never,once,always",settings.inspectTemplate)>
								<input type="hidden" name="inspectTemplate" value="#settings.inspectTemplate#">
								<b>#stText.setting["inspectTemplate"& settings.inspectTemplate]#</b><br />
								<div class="comment">#stText.setting["inspectTemplate#settings.inspectTemplate#Desc"]#</div>
							</cfif>
						</cfif>
					</td>
				</tr>
				
				
				<!--- Type Checking --->
				<tr>
					<th scope="row">#stText.setting.typeChecking#</th>
					<td class="fieldPadded">
						<label>
							<input class="checkbox" type="checkbox" name="typeChecking" value="true"<cfif settings.typeChecking EQ true> checked="checked"</cfif>>
						</label>
						<div class="comment">#stText.setting.typeCheckingDesc#</div>
						<cfset renderCodingTip( "this.typeChecking = "&settings.typeChecking&";" )>
					</td>
				</tr>
				
				<!--- PagePool --->
				<tr>
					<th scope="row">#stText.setting.templateCache#</th>
					<td class="fieldPadded">
						<input class="button submit" type="submit" name="mainAction" value="#btnClearTemplateCache#">
						<div class="comment">#stText.setting.templateCacheClearDesc#</div>


						<cfsavecontent variable="codeSample">
							pagePoolClear();
						</cfsavecontent>
						<cfset renderCodingTip( codeSample, stText.settings.codetip )>
					</td>
				</tr>
				
				<!--- Query Cache --->
				<tr>
					<th scope="row">#stText.setting.queryCache#</th>
					<td class="fieldPadded">
						<input class="button submit" type="submit" name="mainAction" value="#btnClearQueryCache#">
						<div class="comment">#stText.setting.queryCacheClearDesc#</div>


						<cfsavecontent variable="codeSample">
							&lt;cfobjectcache action="clear"&gt;
						</cfsavecontent>
						<cfset renderCodingTip( codeSample, stText.settings.codetip )>
					</td>
				</tr>
				
				<!--- Component Path Cache --->
				<tr>
					<th scope="row">#stText.setting.componentCache#</th>
					<td class="fieldPadded">
						<input class="button submit" type="submit" name="mainAction" value="#btnClearComponentCache#">
						<div class="comment">#stText.setting.componentCacheClearDesc#</div>


						<cfsavecontent variable="codeSample">
							componentCacheClear();
						</cfsavecontent>
						<cfset renderCodingTip( codeSample, stText.settings.codetip )>
					</td>
				</tr>
				
				<!--- Customtag Path Cache --->
				<tr>
					<th scope="row">#stText.setting.ctCache#</th>
					<td class="fieldPadded">
						<input class="button submit" type="submit" name="mainAction" value="#btnClearCTCache#">
						<div class="comment">#stText.setting.ctCacheClearDesc#</div>
						

						<cfsavecontent variable="codeSample">
							ctCacheClear();
						</cfsavecontent>
						<cfset renderCodingTip( codeSample, stText.settings.codetip )>
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
							<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.update#">
							<input class="button reset" type="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
</cfoutput>