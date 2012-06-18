<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "F621E4CC22D80F4BD3042811EB3E9B75" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAACXBIWXMAAAsTAAALEwEAmpwYAAAABGdBTUEAANjr9RwUqgAAACBjSFJNAABuJwAAc68AAPd6AACFVQAAcPsAAOOkAAAyqwAAHJY/IjnCAAABgElEQVR42pTSzSvDARwG8AcbzRA28pKXtsMi89KQ0ArZJHmJjBZx2U3KJC8HxZaDXJzIvOTgMsNtXi7swE1JotDIeygvxeTl8Rf8fua5f3r69n2C4Gfa6sttk1aTK1VbVLx7dLLhFyoy1tkPNxfIDhVf+ltZUGYckPyFDIYK++j4VP/u5Tk27jTQBf/g7uriSxQZK2psj68e3ngnaDKZ2dzZS61O5wQQI4j0esOQ92GH/LSSjkjOdJupyihYBhAhiPLyS0bO7rdJXxc/JxPp7qilRqNxiaLsnMLh42sP+W6lz6HmkqWKypS0RQAKQaTNzLXte9fJ7x5+ONRcaDcwOl69CCBSEKVn6ex7p26SfeR0MufNpZQrk5YBhAsiiTSiesU1RnKQnFNxulHPkKh4l2gTEBhsqizZe3D38na2jI6mXIaExThFbwIgkcrCGuIUssy1zQNMrV7Tc3zj/Hl7sgB4Fn1yqFxuiY1L8AVIZVsAWgDI/N2vEoAeQCL+kd8BAJdvnSeNEzLrAAAAAElFTkSuQmCC')#" />
