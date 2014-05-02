<cfscript>	
	setting showdebugoutput="false";
	ormReload();
	entity = EntityNew("MixedComponent");
	entity.setUnitId("hello");
	entity.setEntityId("goodbye");
	entity.setEntityTypeId(7);
	EntitySave(entity);
	ormFlush();
</cfscript>
