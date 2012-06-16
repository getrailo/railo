


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
                        <th scope="row">#stText.ext.installedVersion#</th>
                        <td>#app.version#</td>
                    </tr>
                    <cfelse>
                    <tr>
                        <th scope="row">#stText.ext.availableVersion#</th>
                        <td>#app.version#</td>
                    </tr>
                    </cfif>
					<!--- category --->
                    <cfif len(trim(app.category))>
                    <tr>
                        <th scope="row">#stText.ext.category#</th>
                        <td>#app.category#</td>
                    </tr>
                    </cfif>
                    <!--- author --->
                    <cfif len(trim(app.author))>
                    <tr>
                        <th scope="row">#stText.ext.author#</th>
                        <td>#app.author#</td>
                    </tr>
                    </cfif>
                    <!--- codename --->
                    <cfif len(trim(app.codename))>
                    <tr>
                        <th scope="row">#stText.ext.codename#</th>
                        <td>#app.codename#</td>
                    </tr>
                    </cfif>
                    <!--- created --->
                    <cfif len(trim(app.created))>
                    <tr>
                        <th scope="row">#stText.ext.created#</th>
                        <td>#LSDateFormat(app.created)#</td>
                    </tr>
                    </cfif>
                    <!--- provider --->
                    <cfif len(trim(info.title))>
                    <tr>
                        <th scope="row">#stText.ext.provider#</th>
                        <td><a href="#info.url#" target="_blank">#info.title#</a></td>
                    </tr>
                    </cfif>
                    <tr>
                        <td colspan="2"></td>
                    </tr>
                    <!--- documentation --->
                    <cfif len(trim(app.documentation))>
                    <tr>
                        <th scope="row">#stText.ext.documentation#</th>
                        <td><a href="#app.documentation#" target="_blank">#replace(replace(app.documentation,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- support --->
                    <cfif len(trim(app.support))>
                    <tr>
                        <th scope="row">#stText.ext.support#</th>
                        <td><a href="#app.support#" target="_blank">#replace(replace(app.support,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- forum --->
                    <cfif len(trim(app.forum))>
                    <tr>
                        <th scope="row">#stText.ext.forum#</th>
                        <td><a href="#app.forum#" target="_blank">#replace(replace(app.forum,'http://',''),'https://','')#</a></td>
                    </tr>
                    </cfif>
                    <!--- mailinglist --->
                    <cfif len(trim(app.mailinglist))>
                    <tr>
                        <th scope="row">#stText.ext.mailinglist#</th>
                        <td><a href="#app.mailinglist#" target="_blank">#replace(replace(app.mailinglist,'http://',''),'https://','')#</a></td>
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
                    <th scope="row">#stText.ext.installedVersion#</th>
                    <td width="300">#detail.installed.version#</td>
                </tr>
                <tr>
                    <th scope="row">#stText.ext.availableVersion#</th>
                    <td>#detail.data.version#</td>
                </tr>
                <!---<tr>
                    <td colspan="2">
                    <textarea cols="80" rows="20">TODO get Update info</textarea>
                    
                    </td>
                </tr>--->
                </table>
                
            	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
                	<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.update#">
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            
            
            <!--- Install --->
            <cfelseif isInstalled and not hasUpdate>
            	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.uninstall#">
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            <cfelse>
            
				<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
            		<input type="hidden" name="uid" value="#url.uid#">
                    
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.install#">
            		<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.cancel#">
              	</cfform>
            </cfif>
            
            
        </cfoutput>
