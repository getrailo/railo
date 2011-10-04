


<cfset detail=getDetailByUid(url.uid)>


<cfset isInstalled=structKeyExists(detail,'installed')>
<cfif StructKeyExists(detail,'installed')>
	<cfset app=detail.installed>
    <cfset hasUpdate=updateAvailable(detail.installed)>
<cfelse>
	<cfset app=detail.data>
    <cfset hasUpdate=false>
</cfif>


<cfif arrayLen(detail.all) GT 0>
	<cfset info=detail.data.info>
	<cfloop array="#detail.all#" index="ap">
    	<cfif ap.provider EQ app.provider>
        	<cfset info=ap.info>
        </cfif>
    </cfloop>
<cfelse>
	<cfset info={title:""}>
</cfif>
        
        <cfoutput query="app">
        	
            <!--- Info --->
            <h2>#app.label# (#iif(isInstalled,de(stText.ext.installed),de(stText.ext.notInstalled))#)</h2>
            
            <table width="600">
            
            <tr>
            	<td colspan="2">#replace(replace(app.description,'<','&lt;',"all"),"
","<br>","all")#<br /><br /></td>
            </tr>
            <tr>
            	<cfset attrs.bgcolor="##595F73">
                <cfset attrs.fgcolor="##DFE9F6">
                
                	<cfif len(app.video) and len(app.image)>
                    	<td><cfvideoplayer attributeCollection="#attrs#" video="#app.video#"  preview="#app.image#" width="320" height="256"></td>
                    <cfelseif len(app.video)>
                    	<td><cfvideoplayer attributeCollection="#attrs#" video="#app.video#" width="320" height="256" allowfullscreen="true"></td>
                    <cfelseif len(app.image)>
                    	<cfset dn=getDumpNail(app.image,700,600)>
                    	<cfif len(dn)><img src="#dn#" /></td></cfif>
                    </cfif>
                
                
                <td valign="top" <cfif len(app.video&app.image)>align="right"</cfif>>
                	
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
					<!--- category --->
                    <cfif len(trim(app.category))>
                    <tr>
                        <td class="tblHead">#stText.ext.category#</td>
                        <td class="tblContent">#app.category#</td>
                    </tr>
                    </cfif>
                    <!--- author --->
                    <cfif len(trim(app.author))>
                    <tr>
                        <td class="tblHead">#stText.ext.author#</td>
                        <td class="tblContent">#app.author#</td>
                    </tr>
                    </cfif>
                    <!--- codename --->
                    <cfif len(trim(app.codename))>
                    <tr>
                        <td class="tblHead">#stText.ext.codename#</td>
                        <td class="tblContent">#app.codename#</td>
                    </tr>
                    </cfif>
                    <!--- created --->
                    <cfif len(trim(app.created))>
                    <tr>
                        <td class="tblHead">#stText.ext.created#</td>
                        <td class="tblContent">#LSDateFormat(app.created)#</td>
                    </tr>
                    </cfif>
                    <!--- provider --->
                    <cfif len(trim(info.title))>
                    <tr>
                        <td class="tblHead">#stText.ext.provider#</td>
                        <td class="tblContent"><a href="#info.url#" target="_blank">#info.title#</a></td>
                    </tr>
                    </cfif>
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    <!--- documentation --->
                    <cfif len(trim(app.documentation))>
                    <tr>
                        <td class="tblHead">#stText.ext.documentation#</td>
                        <td class="tblContent"><a href="#app.documentation#" target="_blank">#replace(replace(app.documentation,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- support --->
                    <cfif len(trim(app.support))>
                    <tr>
                        <td class="tblHead">#stText.ext.support#</td>
                        <td class="tblContent"><a href="#app.support#" target="_blank">#replace(replace(app.support,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- forum --->
                    <cfif len(trim(app.forum))>
                    <tr>
                        <td class="tblHead">#stText.ext.forum#</td>
                        <td class="tblContent"><a href="#app.forum#" target="_blank">#replace(replace(app.forum,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- mailinglist --->
                    <cfif len(trim(app.mailinglist))>
                    <tr>
                        <td class="tblHead">#stText.ext.mailinglist#</td>
                        <td class="tblContent"><a href="#app.mailinglist#" target="_blank">#replace(replace(app.mailinglist,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    </table>
                
                </td>
            </tr>
            </table>
            <!--- Update --->
            <cfif isInstalled and hasUpdate>
            	<h2>#stText.ext.updateAvailable#</h2>
                <cfset updateAvailableDesc=replace(stText.ext.updateAvailableDesc,'{installed}',app.version)>
                <cfset updateAvailableDesc=replace(updateAvailableDesc,'{update}',detail.data.version)>
                <!--- #updateAvailableDesc#--->
                
                <table class="tbl" width="600">
                <tr>
                    <td class="tblHead">#stText.ext.installedVersion#</td>
                    <td class="tblContent" width="300">#detail.installed.version#</td>
                </tr>
                <tr>
                    <td class="tblHead">#stText.ext.availableVersion#</td>
                    <td class="tblContent">#detail.data.version#</td>
                </tr>
                <!---<tr>
                    <td class="tblContent" colspan="2">
                    <textarea cols="80" rows="20">TODO get Update info</textarea>
                    
                    </td>
                </tr>--->
                </table>
                
            	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
                	<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.update#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            
            
            <!--- Install --->
            <cfelseif isInstalled and not hasUpdate>
            	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            <cfelse>
            
				<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.install#">
            		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            </cfif>
            
            
        </cfoutput>
