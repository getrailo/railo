<cfcomponent name="FieldLink" entityname="FieldLink" table="fieldLink2275" persistent="true" output="false" accessors="true" hint="Field Placeholder cfc">
	<cfproperty name="Field" fieldtype="id,many-to-one" cfc="Field" fkcolumn="field_id" hint="ID of Field" /> 
	<cfproperty name="InfoCard" fieldtype="id,many-to-one" cfc="InfoCard" fkcolumn="infocard_id" hint="Template ID of Field" /> 
	
	<cfproperty name="DisplayName" column="display" sqltype="nvarchar(50)"  />
	<cfproperty name="Type" column="type" sqltype="nvarchar(50)"  />
</cfcomponent>