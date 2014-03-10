<cfcomponent name="InfoCard" entityname="InfoCard" table="infocard2275" persistent="true" output="false" accessors="true" hint="Represents an Infocard">
	<cfproperty name="ID" column="id" fieldtype="id" ormtype="string" length="18" update="false" insert="false" />
	<cfproperty name="InfoCardNumber" column="number" sqltype="nvarchar(30)"  />
	<cfproperty name="Revision" column="revision" sqltype="nvarchar(30)"  />
	<cfproperty name="Title" column="title" sqltype="nvarchar(300)"  />

	<cffunction name="init" output="false" access="public" returntype="InfoCard">
		<cfset setID(Left(replace(CreateUUID(),"-","","ALL"),18)) />
		<cfreturn this />
	</cffunction>
</cfcomponent>