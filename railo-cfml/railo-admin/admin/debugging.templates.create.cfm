<cffunction name="addZero">
	<cfargument name="str">
    <cfreturn str>
</cffunction>
<cfset isNew=false>
<cfif StructKeyExists(url,'id')>
    <cfloop query="debug" >
        <cfif debug.id EQ url.id>
            <cfset entry=querySlice(debug,debug.currentrow,1)>
			<cfset driver=drivers[entry.type]>
        </cfif> 
    </cfloop>
<cfelse>
	<cfset driver=drivers[trim(form.type)]>
	<cfset isNew=true>
	<cfset entry=struct()>
	<cfset entry.type=trim(form.type)>
	<cfset entry.label=form.label>
	<cfset entry.iprange="*">
	<cfset entry.custom=struct()>
</cfif>



<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.submit#">

        	<cfset custom=struct()>
        
			<!--- custom --->
            <cfloop collection="#form#" item="key">
                <cfif left(key,13) EQ "custompart_d_">
                	<cfset name=mid(key,14,10000)>
                    <cfset custom[name]=(form["custompart_d_"&name]*86400)+(form["custompart_h_"&name]*3600)+(form["custompart_m_"&name]*60)+form["custompart_s_"&name]>
                </cfif>
            </cfloop>       
            <cfloop collection="#form#" item="key">
                <cfif left(key,7) EQ "custom_">
                    <cfset custom[mid(key,8,10000)]=form[key]>
                </cfif>
            </cfloop>

            <cfset driver.onBeforeUpdate(custom)>
            <cfset meta=getMetaData(driver)>
            <cfadmin 
                action="updateDebugEntry"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                 
                label="#form.label#" 
                debugtype="#form.type#" 
                iprange="#form.iprange#"
                fullname="#meta.fullname#"
                path="#contractPath(meta.path)#"
                custom="#custom#"
                
                remoteClients="#request.getRemoteClients()#">
                    
		</cfcase>
	</cfswitch>
	<cfcatch>
    	<cfset driver.onBeforeError(cfcatch)>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq "none">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>


<cfoutput>

<script>
function add(field){
	var ip='#cgi.remote_addr#';
	var value=field.form.iprange.value;
	
	if(value && value.indexOf(ip)!=-1) return;
	
	
	if(value)
		field.form.iprange.value+=","+ip;
	else
		field.form.iprange.value=ip;
}
</script>

<!--- 
Error Output--->
<cfset printError(error)>

<h2>#driver.getLabel()#</h2>
#driver.getDescription()#
<table class="tbl" width="650">

<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create" method="post">
<cfinput type="hidden" name="type" value="#entry.type#" >
<cfinput type="hidden" name="label" value="#entry.label#" >
	<tr>
		<td width="150" class="tblHead" nowrap>#stText.debug.label#</td>
		<td width="450" nowrap>#entry.label#</td>
	</tr>
    <tr>
		<td width="150" class="tblHead" nowrap>#stText.debug.iprange#</td>
		<td width="450" nowrap>
        <cfinput type="text" 
                name="iprange" 
                value="#entry.iprange#" style="width:350px" required="yes"
                message="#stText.debug.iprangeMissing#"><input type="button" name="addmyip" value="#stText.debug.addMyIp#" onclick="add(this)" class="button submit">
        <br /><div class="comment">#replace(stText.debug.iprangeDesc,"
","<br />","all")#</div>


</td>
	</tr>
    
	<tr>
		<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="4"></td>
	</tr>
    
    <cfset custom=entry.custom>
    <cfloop array="#driver.getCustomFields()#" index="field">
    	<cfif isInstanceOf(field,"Group")>
        <tr>
            <td colspan="2"><cfmodule template="tp.cfm"  width="1" height="4"><h#field.getLevel()#>#field.getDisplayName()#</h#field.getLevel()#>#field.getDescription()#
            	</td>
        </tr>
		<cfcontinue>
		</cfif>
		
		
		<cfset doBR=true>
        <cfif StructKeyExists(custom,field.getName())>
            <cfset default=custom[field.getName()]>
        <cfelseif isNew>
            <cfset default=field.getDefaultValue()>
        <cfelse>
            <cfset default="">
        </cfif>
        <cfset type=field.getType()>
<cfif type NEQ "hidden">
        <tr>
            <th scope="row">#field.getDisplayName()#</th>
            <td width="300">

</cfif><cfif type EQ "text" or type EQ "password">
            <cfinput type="#type#" 
                name="custom_#field.getName()#" 
                value="#default#" style="width:300px" required="#field.getRequired()#" 
                message="Missing value for field #field.getDisplayName()#">
            
			
			
<cfelseif left(type,4) EQ "text">
            <cfinput type="text" 
                name="custom_#field.getName()#" 
                value="#default#" style="width:#mid(type,5)#px" required="#field.getRequired()#" 
                message="Missing value for field #field.getDisplayName()#"> 
            
			
			
<cfelseif type EQ "textarea">
			<textarea style="width:450px;height:100px;" name="custom_#field.getName()#">#default#</textarea>
<cfelseif type EQ "hidden">
			<cfinput type="hidden" name="custom_#field.getName()#" value="#default#">
<cfelseif type EQ "time">
			<cfsilent>
            <cfset doBR=false>
			<cfset default=default+0>
            <cfset s=default>
            <cfset m=0>
            <cfset h=0>
            <cfset d=0>
            
			<cfif s GT 0>
				<cfset m=int(s/60)>
                <cfset s-=m*60>
            </cfif>
			<cfif m GT 0>
				<cfset h=int(m/60)>
                <cfset m-=h*60>
            </cfif>
            <cfif h GT 0>
				<cfset d=int(h/24)>
                <cfset h-=d*24>
            </cfif>
            </cfsilent>
            
            
            <table class="tbl">
		<tr>
			<th scope="row">#stText.General.Days#</th>
			<th scope="row">#stText.General.Hours#</th>
			<th scope="row">#stText.General.Minutes#</th>
			<th scope="row">#stText.General.Seconds#</th>
		</tr>
		
		<tr>
			<td><cfinput type="text" 
                name="custompart_d_#field.getName()#" 
                value="#addZero(d)#" style="width:40px" required="#field.getRequired()#"   validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
			<td><cfinput type="text" 
                name="custompart_h_#field.getName()#" 
                value="#addZero(h)#" style="width:40px" required="#field.getRequired()#"  maxlength="2"  validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
			<td><cfinput type="text" 
                name="custompart_m_#field.getName()#" 
                value="#addZero(m)#" style="width:40px" required="#field.getRequired()#"  maxlength="2" validate="integer" 
                message="Missing value for field #field.getDisplayName()#"></td>
			<td><cfinput type="text" 
                name="custompart_s_#field.getName()#" 
                value="#addZero(s)#" style="width:40px" required="#field.getRequired()#"  maxlength="2"  validate="integer"
                message="Missing value for field #field.getDisplayName()#"></td>
		</tr>
		
		</table>
            
            
            
            
            
            
            
            
                
                
                
            <cfelseif type EQ "select">
                <cfoutput><br />
                <cfif default EQ field.getDefaultValue() and field.getRequired()><cfset default=listFirst(default)></cfif>
                <select name="custom_#field.getName()#">
                    <cfif not field.getRequired()><option value=""> ---------- </option></cfif>
                    <cfif len(trim(default))>
                    <cfloop index="item" list="#field.getValues()#">
                    <option <cfif item EQ default>selected="selected"</cfif> >#item#</option>
                    </cfloop>
                    </cfif>
                </select>
                </cfoutput>
            <cfelseif type EQ "radio" or type EQ "checkbox">
            	<cfset desc=field.getDescription()>
            	<cfoutput>
                <cfif isStruct(desc) and StructKeyExists(desc,'_top')><div class="comment" style="padding-bottom:4px">#desc._top#</div></cfif>
                <cfloop index="item" list="#field.getValues()#">
                	<cfif listLen(field.getValues()) GT 1>
                    <cfset doBR=false>
                    <table cellpadding="0" cellspacing="2">
                    <tr>
                    	<td valign="top"><cfinput type="#type#" name="custom_#field.getName()#" value="#item#" checked="#listFindNoCase(default,item)#">&nbsp;</td>
                        <td>
							#item#
                            <cfif isStruct(desc) and StructKeyExists(desc,item)><div class="comment" style="padding-bottom:4px">#desc[item]#</div></cfif>
                        </td>
                    </tr>
                    </table>
                    <cfelse>
                    	<cfinput type="#type#" name="custom_#field.getName()#" value="#item#" checked="#item EQ default#">
                    </cfif>
                </cfloop>
                <cfif isStruct(desc) and StructKeyExists(desc,'_bottom')><div class="comment" style="padding-top:4px">#desc._bottom#</div></cfif>
                </cfoutput>
            </cfif>
<cfif type NEQ "hidden">
			<cfset _desc=field.getDescription()>
			<cfset _comment="">
			<cfif isSimpleValue(_desc)>
            	<cfset _comment=_desc>
			<cfelse>
                <cfif StructKeyExists(_desc,'_appendix')>
				#_desc._appendix#
				</cfif>
				<cfif StructKeyExists(_desc,'_bottom')>
					<cfset _comment=_desc._bottom>
                </cfif>
			</cfif>
            <cfif len(trim(_comment))><cfif doBr><br /></cfif><div class="comment">#trim(_comment)#</div></cfif>
            </td>
        </tr>
</cfif>
        </cfloop>
    
    
	
    
    <tr>
	<td colspan="2">
	<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.submit#"></td>
</tr>



</cfform>
</table>
<br><br>
</cfoutput>