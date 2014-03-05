<cfcomponent name="Field" entityname="Field" table="field2275" persistent="true" output="false" accessors="true" hint="Field Placeholder cfc">
	<cfproperty name="ID" column="id" fieldtype="id" type="string" ormtype="string" length="18" update="false" insert="false"  />
	<cfproperty name="Name" column="name" sqltype="nvarchar(50)"  />
	<cfproperty name="CustomFieldType" column="type" sqltype="nvarchar(50)" /> 
	
	<cffunction name="init" output="false" access="public" returntype="Field">
		<cfset setID(Left(replace(CreateUUID(),"-","","ALL"),18)) />
		<cfreturn this />
	</cffunction>
</cfcomponent>