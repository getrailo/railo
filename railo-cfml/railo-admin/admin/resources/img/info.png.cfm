<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "B50E0C84E9992DA2E7D87AC7C4AF9950" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAjxJREFUeNpsU71rGmEcfu/19DwRIloFv9CKtiEdVCQ0Q7YMdki2DIGODaVTh1A69h8opIF0KHTpFEKnQoZCp5a2oyXichC0goo9/FYOix93eV7ryXm9F/Te39fz+3h+L6dpGjEfRVGc9Xr9AX6bNpuNV1V1kE6nr10uV1UUxTVfzgjQ6/VIs9k8aLVaxxzH7SLQsTRpAGpRSq8ikci7UCgk2e32dYB2u+0qlUpv5vP5U57nidVhvtPpVPZ6vSeZTOaC+S0ABoMBKRQK7+FzjCyrAFSgQlbwEfFdoc5ms6nH49nP5XJfKATSaDQO4PTEFEycTufXbDbr9fl8p0zWDzLb+/3+eafT2aAoWUT5z6Dk1obDcWQymURGo9E2koSYbGrnnizLh3y1Wt2CvGPuF4DfotHoZ4DsDYfD+2YADJWxledrtdomnAWjEVURv9//KR6Pny2rUcrl8rZxuKxdANyh/D+tZkYHnc8rlcorJoMym9W+AFigGM6IAVqwdhfsPFoOVLACgO4vxYYVkFG2MP5xu90/2B1s/IZPxdwm2JEonGowXpmNwWDwdSqVesnkQCBwgftjpjculSAIlzz+1HA4/FaSpCPcA/qAwPMe3kJXHxwoe2jcE/R/GYvFfi42EVQRrPER3sIHIyNsyfTeF2sLKpmMSn4lk8l9ADRXbwE7TorFYh6DO4Vui2UzZtTLZ5kTicQLUNz47zWyde12uxug8HA8HufBsw8BFD4TDOzG4XB8ROB3DHUVdCvAABZeLiizjvGPAAAAAElFTkSuQmCC')#" />
