<cfsetting showdebugoutput="no">
<cfset newState = entityNew("State") />
<cfset newState.setStateCode("CA") />
<cfset newState.setCountryCode("US") />
<cfset newState.setSusi("Sorglos") />
<cfset entitySave( newState ) />
<cfset ormFlush() />