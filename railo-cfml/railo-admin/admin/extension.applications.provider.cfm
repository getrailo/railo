
<cfset data=getProviderData(url.hashProvider,true)>
<cfset info=data.info>



<cfoutput>
<h2>#info.title#</h2>
<table width="600">   
<tr>
    <td colspan="2">#info.description#<br /><br /></td>
</tr>
<tr>
    <td><cfif structKeyExists(info,'image')><img src="#info.image#" /></cfif></td>
    <td valign="top" align="right">
        
        <table class="tbl" width="300">
        
        <tr>
            <td class="tblHead">#stText.ext.provider#</td>
            <td class="tblContent"><a href="#info.url#" target="_blank">#info.url#</a></td>
        </tr>
        </table>
    
    </td>
</tr>
</table>
</cfoutput>





<!----
<cfset detail=getDetail(url.hashProvider,url.id)>
<cfset isInstalled=structKeyExists(detail,'installed')>
<cfif StructKeyExists(detail,'installed')>
	<cfset app=detail.installed>
    <cfset hasUpdate=updateAvailable(detail.installed)>
<cfelse>
	<cfset app=detail.app>
    <cfset hasUpdate=false>
</cfif>

<cfset info=detail.info>

        
        <cfoutput query="app">
        	
            <!--- Info --->
            <h2>#app.label# (#iif(isInstalled,de(stText.ext.installed),de(stText.ext.notInstalled))#)</h2>
            
            <table width="600">
            
            <tr>
            	<td colspan="2">#app.description#<br /><br /></td>
            </tr>
            <tr>
            	<td><img src="#app.image#" /></td>
                <td valign="top" align="right">
                	
                	<table class="tbl" width="300">
                    <cfif isInstalled>
                    <tr>
                        <td class="tblHead">#stText.ext.installedVersion#</td>
                        <td class="tblContent">#app.version#</td>
                    </tr>
                    <cfelse>
                    <tr>
                        <td class="tblHead">#stText.ext.availableVersion#</td>
                        <td class="tblContent">#app.version#</td>
                    </tr>
                    </cfif>
                    <tr>
                        <td class="tblHead">#stText.ext.category#</td>
                        <td class="tblContent">#app.category#</td>
                    </tr>
                    
                    <tr>
                        <td class="tblHead">#stText.ext.provider#</td>
                        <td class="tblContent"><a href="#info.url#" target="_blank">#info.title#</a></td>
                    </tr>
                    </table>
                
                </td>
            </tr>
            </table>
            
            <!--- Update --->
            <cfif isInstalled and hasUpdate>
            	<h2>#stText.ext.updateAvailable#</h2>
                <cfset updateAvailableDesc=replace(stText.ext.updateAvailableDesc,'{installed}',app.version)>
                <cfset updateAvailableDesc=replace(updateAvailableDesc,'{update}',detail.app.version)>
                <!--- #updateAvailableDesc#--->
                
                <table class="tbl" width="600">
                <tr>
                    <td class="tblHead">#stText.ext.installedVersion#</td>
                    <td class="tblContent" width="300">#detail.installed.version#</td>
                </tr>
                <tr>
                    <td class="tblHead">#stText.ext.availableVersion#</td>
                    <td class="tblContent">#detail.app.version#</td>
                </tr>
                <tr>
                    <td class="tblContent" colspan="2">
                    <textarea cols="80" rows="20">TODO get Update info</textarea>
                    
                    </td>
                </tr>
                </table>
                
            	<cfform action="#request.self#?action=#url.action#" method="post">
                	<input type="hidden" name="hashProvider_1" value="#url.hashProvider#">
                	<input type="hidden" name="id_1" value="#url.id#">
                	<input type="hidden" name="row" value="1">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.update#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            
            
            <!--- Install --->
            <cfelseif isInstalled and not hasUpdate>
            	<cfform action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="hashProvider_1" value="#url.hashProvider#">
                	<input type="hidden" name="id_1" value="#url.id#">
                	<input type="hidden" name="row" value="1">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            <cfelse>
            
				<cfform action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="hashProvider_1" value="#url.hashProvider#">
                	<input type="hidden" name="id_1" value="#url.id#">
                	<input type="hidden" name="row" value="1">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.install#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            </cfif>
            
            
        </cfoutput>
--->