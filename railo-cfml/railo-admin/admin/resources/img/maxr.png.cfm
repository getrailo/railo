<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "5073C140B8AF0B6693D9E520121ECC7E" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAABYAAAAWCAYAAADEtGw7AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAr9JREFUeNq0VUtvElEYnWGmPMa2Q6athlcb2DQsgE2XNgaj0oW/w/gDXZRYF+rKuKNJK5uaFChQHxFRnjOM52vOTW6wPuLjJidcZr7v3PM97jdmGIamYRgWsAJEAZv/5fnvrBAIAB+YAXP5r0jigAOscW+TOPwFqbIR0gkwBEayt6lUSD1gC0jwmcHTxTlCASoidbBJ0jHwGejS70pxlEq36vX6C+MvVq1Wu0sxU3V6nErl5SPmSkIaaKlKAmlgG0jR3mb4HYh6rKdS5djWwp/R+BPwge/FwWk0Gg96vd5+JBJZSG6l8LZtz6vV6lP6Oqr4eq7UGpH0PdCn4VQUl8vl55Zl5dvtdk4ZB0EQ8mBDz31Ea5k59wMq7ROXwEc+H6dSqS4UB0vpVb4L1Un2UusYDF319QpzdgNY7ff7hZOTk/JisYBwC2IDiz6T5UJGrtnHGZoLbAI3BSBzm83mwWw2i8disfHe3t7rbDZ7zgt2QV/f0HJiaEplJWHsmKbpsq9Xfd9PQuFqJpM5arVaB8Vi8UkymTxzXdeB6n3YtH5GHOVv+vj4+H6pVHoJ8pGEf3p6+jCXyx0WCoVXOzs7z9AJUtwveL8Ouzea7+w6YrXPgey2VB8KL0BamU6niU6ncy+fzx+B9C2LKt3jgXyTt9bgzPghcRrGIVpqu9vtZqRA0Wh0sru7e4huGLBLLlkwSV+M/au64jtiQyue6lFLWqtSqTQ8zzuT8IGvJJ2rSaYpDZc7wdT2K/op0lpQrq6wy7ClU24RG3yufE2lONRGn6wh8utLG0lKxBD5zUL9Hdy8JlswRpUbnCHO0nwObW1Ij/iiw7u/po1EGYfnrL5HooBKHa0r5vS5Gpv6kDY4pf50jcnlmwg5QXUex+G6NhL1NKlPT6DNBP2DIKTvOFeGNh+ONIN/8Wmam//rY/pNgAEAMLYaUYgJ0DoAAAAASUVORK5CYII=')#" />
