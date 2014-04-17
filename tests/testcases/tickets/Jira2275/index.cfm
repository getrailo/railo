<cfsetting showdebugoutput="false">	 

<cfset field = entityLoad("Field",{Name:"Test Field"}, true) />
<cfif isNull(field)>
	<cfset field = entityNew("Field",{Name:"Test Field", CustomFieldType:"Date/Time"}) />
	<cfset entitySave(field) />
</cfif>

<cfset infocard = entityLoad("InfoCard",{ID:"0000RISKMANAGEMENT"}, true)>
<cfif isNull(infocard)>
	<cfset infocard = entityNew("InfoCard", {InfoCardNumber:"Test-1", Revision:"1A", Title:"Test InfoCard"}) />
	<cfset entitySave(infocard) />
</cfif>

<cfset fieldLink = entityLoad("FieldLink",{Field:field, InfoCard:infocard}, true) />
<cfif isNull(fieldLink)>
	<cfset fieldLink = entityNew("FieldLink",{Field:field, InfoCard:infocard, DisplayName:"Test Field Link", Type:"Test Field Link Type"}) />
	<cfset entitySave(fieldLink) />
</cfif>

<cfquery datasource="#getApplicationSettings().defaultDatasource#" name="fl">
	select * from fieldLink2275
</cfquery>
<cfoutput>#listSort(fl.columnlist,"textnocase")#</cfoutput>
