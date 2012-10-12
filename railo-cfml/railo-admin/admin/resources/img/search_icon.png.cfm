<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "FC13EF48A0C46AEB21513F4BE0532B57" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAkFJREFUeNp8UjtvE0EQnt2zfT774iexCRJRYruBBFERQuSEWCSRaAJOiogS+AFQ0lPzD5BogIIWKRUCwkNKESFFsuwCjO00+Ow728iPe3n3mMM4AhSYZndn59PM981HVldzMBwyEAQhnsmkH4TCkU3G+CnLtgadTvtNs648wvsh/sPvQbLZFYjFIguXF5ee4WfG4/GAKIpgWiaoagt6vW6rUv5yr9vtPqWUHgOF1GwqdjWXe2XoRmpu7hzcvXMbFhYuwdKVRWDMBkVpSNF4fKultfZs264SQkbAa2vrD+Pxyevz83Owlb/5xziZdBppDKFSqQJ32ESn3X4x7krD4fC21+uBfP4GnBQbG+sQDAYgKMtrgkeYHOfpkPGo6BfhfxEKTQAlVEYNko7zC2jblm6a1j9ByAu8Xh9gvcW503UpujwpSv5aVTXY3d09EVgqlaDf74Omqu8YGx65OQfbCslE8nMkEt2pNxrSAAump89iBy/oug6FQgEODj5BsVhEoPbYNI0PY3HI8vIKcgjdOj9/4TkB8nOHgaAEciAAA92AarXm7hI459+Uen3Hsqz3lBLcYyoFpmkWNE376DhcRNecxmX7mqo2+Fouv1XV5pOAJF0UBE9CkqQ8dt3HFdWIazk3kDg4nINP9LmSJ5BHj3Fe44yB6Pdnp6bOvMSuEc5ZG/luCzMzs6OZXaVwfjwH6N0mnt/J6O16+cgw9H1ZljdRlxilQpb+raJbOLIVOc65nEzD2GsoyrZtWYfooPs/BBgAqkkNEn8lQmwAAAAASUVORK5CYII=')#" />
