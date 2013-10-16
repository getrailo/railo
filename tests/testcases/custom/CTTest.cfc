<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	
	
	<cffunction name="testNewVar">
		<cfmodule template="CTTest/ct.cfm">
		<cfset assertEquals("from.ct",variables.testvar)>
	</cffunction>
	
	<cffunction name="testUpdateExistingVar">
		<cfset variables.testvar="from.here">
		<cfmodule template="CTTest/ct.cfm">
		<cfset assertEquals("from.ct",variables.testvar)>
	</cffunction>
	
	
	<cffunction name="testWriteToParent">
		<cfmodule template="CTTest/level1.cfm">
		<cfset assertEquals("caller.1",variables.fromLevel1)>
		<cfset assertEquals("caller.caller.2",variables.fromLevel2)>
		<cfset assertEquals("caller.2",variables.recievedfromLevel2)>
		<cfset assertEquals("caller_2",variables.recieved.from.Level2)>
		<cfset assertEquals("caller_2",variables.rec.ieved.from.Level2)>
		
		<cfset assertEquals("caller.1.eval",variables.fromLevel1Eval)>
		<cfset assertEquals("caller.caller.2.eval",variables.fromLevel2Eval)>
		
		
		<cfset assertEquals("caller_caller_2",variables.from.Level2)>
		<cfset assertEquals("caller-caller.2.eval",variables.fro.mLevel2.Eval)>
		
	</cffunction>
	
	<cffunction name="testReadFromParent">
		<cfset variables.parentData="parent.data">
		<cfmodule template="CTTest/level3.cfm">
		<cfset assertEquals("parent.data",variables.recievedFromParent)>
		<cfset assertEquals("parent.data",variables.recievedFromParentParent)>
		<cfset assertEquals("parent.data",variables.recievedFromParentEval)>
		<cfset assertEquals("parent.data",variables.recievedFromParentParentEval)>
		
	</cffunction>
	
	
	<cffunction name="testWriteToParentsAttr">
		<cfmodule template="CTTest/level5.cfm">
		<cfset assertEquals("from.6",variables.attr.fromLevel6)>
		<cfset assertEquals("from_6",variables.attr.fromLevel6Eval)>
	</cffunction>
	
	<cffunction name="test">
		<CFSET variables.CurrentFile.Error = "">
	
		<cfmodule template="CTTest/cuta.cfm" ErrorOutput="CurrentFile.Error">
		<cfset assertEquals("from.cuta",variables.CurrentFile.Error)>
	</cffunction>


</cfcomponent>