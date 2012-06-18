<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "34FF164F41C6A2FB19DCBE4C8CE570CF" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAActJREFUeNpcUrtKA0EUnd1MNjEkm0RCkDQWgaRSIY2NiJ+Qj7AQEYSUgoWdjfggCP5ATKl1bBRt7MROsLFxIYQNG7Kb3bw8Z9kJwYHL3Llzzpn7GK3dbguuRCIR2nw+F5qmMXQB38F+pmKTyUQMh0MhpRSSCBVUPoA7hUKhMZvNRK/Xe9B1/YNkhaGvi2jxMJ1OxXg8JuDYNE2RyWQYbzBOUy/TdB4UkQuA3VQqVec5FouJZDJZR6y2jOHOVPdg2wCVsa/F4/FaNpuVvu+Hyvl83nRd9x53n0j5FyI/8F8kgI+GYZi5XE7AF6yLiqPRKFTnq6VSqQJSxfO8sDko50sCeArgJQ466/ufEhdICx8rAOecqd44juMjnSaaIfmCIkUdXhgwHsT3kWFLUg12B7KGi+tisWgo4NJ4RLfbdbEfpNPpFsWlZVnhRRAEr6jRR/GGaj9FFRGiI/gd9oAxiUDoILCBMWQgsPhJJLC7FMBYVpFVGYJWOA52kkQAqgRHvrBt+xt8iY+wTiIzgOgmHnijsFwMVMoqAfhiFtreRPgWIito/xHqOkRdWQhvqbrlUhOe+v3+HMpXEHiPPrqN/WQwGHQQI/lZ4f8EGADkNjnBGv5i7QAAAABJRU5ErkJggg==')#" />
