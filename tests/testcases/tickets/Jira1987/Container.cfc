<cfcomponent hint="Holds Item objects" output="false">
	<cfproperty name="items" type="Item[]" />
	
	<!--- Under ColdFusion, this ensures the HyperlinkedItem object
	will be defined if it appears in the items array --->
	<cfproperty name="_HI" type="HyperlinkedItem" />
	
	<cfset this.items = ArrayNew( 1 ) />
</cfcomponent>