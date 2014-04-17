<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cfset variables.ds="railo_mirror">
	
	<cffunction name="beforeTests">
		<!--- create table User if necessary --->
		<cftry>
			<cfquery datasource="#variables.ds#">
		        select * from User306
		    </cfquery>
		    <cfcatch>
		    <cfquery datasource="#variables.ds#">
		        CREATE TABLE User306 (
		            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
		            data VARCHAR(100) 
		        );
		    </cfquery>
		    </cfcatch>
		</cftry>
		<!--- create table Order if necessary --->
		<cftry>
			<cfquery datasource="#variables.ds#">
		        select * from Order306
		    </cfquery>
		    <cfcatch>
		    <cfquery datasource="#variables.ds#">
		        CREATE TABLE Order306 (
		            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
		            data VARCHAR(100) , 
		            User_id INT , 
		            
		            Foreign Key (User_id) references User306(id)
		        );
		    </cfquery>
		    </cfcatch>
		</cftry>
		
	</cffunction>
	<cffunction name="afterTests">
		<!--- drop table User --->
		<cftry>
			<cfquery datasource="#variables.ds#">
		        drop table User306
		    </cfquery>
		    <cfcatch></cfcatch>
		</cftry>
		
		<!--- drop table Order --->
		<cftry>
			<cfquery datasource="#variables.ds#">
		        drop table Order306
		    </cfquery>
		    <cfcatch></cfcatch>
		</cftry>
	
	</cffunction>
	
	
	<cffunction name="testOrder_User_id">
		<cfdbinfo type="columns" datasource="#variables.ds#" name="local.data" table="Order306" pattern="User_id">
		<cfset assertEquals("User_id",data.COLUMN_NAME)>
		<cfset assertEquals(10,data.COLUMN_SIZE)>
		<cfset assertEquals(0,data.DECIMAL_DIGITS)>
		<cfset assertEquals(true,_boolean(data.IS_FOREIGNKEY))>
		<cfset assertEquals(true,_boolean(data.IS_NULLABLE))>
		<cfset assertEquals(false,_boolean(data.IS_PRIMARYKEY))>
		<cfset assertEquals(3,data.ORDINAL_POSITION)>
		<cfset assertEquals("id",data.REFERENCED_PRIMARYKEY&"")>
		<cfset assertEquals("User306",data.REFERENCED_PRIMARYKEY_TABLE&"")>
		<cfset assertEquals("",data.REMARKS)>
		<cfset assertEquals("INT",data.TYPE_NAME)>
	</cffunction>
	
	<cffunction name="testOrder_id">
		<cfdbinfo type="columns" datasource="#variables.ds#" name="local.data" table="Order306" pattern="id">
		<cfset assertEquals("id",data.COLUMN_NAME)>
		<cfset assertEquals(10,data.COLUMN_SIZE)>
		<cfset assertEquals(0,data.DECIMAL_DIGITS)>
		<cfset assertEquals(false,_boolean(data.IS_FOREIGNKEY))>
		<cfset assertEquals(false,_boolean(data.IS_NULLABLE))>
		<cfset assertEquals(true,_boolean(data.IS_PRIMARYKEY))>
		<cfset assertEquals(1,data.ORDINAL_POSITION)>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY&"")>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY_TABLE&"")>
		<cfset assertEquals("",data.REMARKS)>
		<cfset assertEquals("INT",data.TYPE_NAME)>
	</cffunction>
	
	<cffunction name="testOrder_data">
		<cfdbinfo type="columns" datasource="#variables.ds#" name="local.data" table="Order306" pattern="data">
		<cfset assertEquals("data",data.COLUMN_NAME)>
		<cfset assertEquals(100,data.COLUMN_SIZE)>
		<cfset assertEquals(0,data.DECIMAL_DIGITS)>
		<cfset assertEquals(false,_boolean(data.IS_FOREIGNKEY))>
		<cfset assertEquals(true,_boolean(data.IS_NULLABLE))>
		<cfset assertEquals(false,_boolean(data.IS_PRIMARYKEY))>
		<cfset assertEquals(2,data.ORDINAL_POSITION)>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY&"")>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY_TABLE&"")>
		<cfset assertEquals("",data.REMARKS)>
		<cfset assertEquals("VARCHAR",data.TYPE_NAME)>
	</cffunction>
	
	
	<cffunction name="testUser_id">
		<cfdbinfo type="columns" datasource="#variables.ds#" name="local.data" table="User306" pattern="id">
		<cfset assertEquals("id",data.COLUMN_NAME)>
		<cfset assertEquals(10,data.COLUMN_SIZE)>
		<cfset assertEquals(0,data.DECIMAL_DIGITS)>
		<cfset assertEquals(false,_boolean(data.IS_FOREIGNKEY))>
		<cfset assertEquals(false,_boolean(data.IS_NULLABLE))>
		<cfset assertEquals(true,_boolean(data.IS_PRIMARYKEY))>
		<cfset assertEquals(1,data.ORDINAL_POSITION)>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY&"")>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY_TABLE&"")>
		<cfset assertEquals("",data.REMARKS)>
		<cfset assertEquals("INT",data.TYPE_NAME)>
	</cffunction>
	
	
	<cffunction name="testUser_data">
		<cfdbinfo type="columns" datasource="#variables.ds#" name="local.data" table="User306" pattern="data">
		<cfset assertEquals("data",data.COLUMN_NAME)>
		<cfset assertEquals(100,data.COLUMN_SIZE)>
		<cfset assertEquals(0,data.DECIMAL_DIGITS)>
		<cfset assertEquals(false,_boolean(data.IS_FOREIGNKEY))>
		<cfset assertEquals(true,_boolean(data.IS_NULLABLE))>
		<cfset assertEquals(false,_boolean(data.IS_PRIMARYKEY))>
		<cfset assertEquals(2,data.ORDINAL_POSITION)>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY&"")>
		<cfset assertEquals("N/A",data.REFERENCED_PRIMARYKEY_TABLE&"")>
		<cfset assertEquals("",data.REMARKS)>
		<cfset assertEquals("VARCHAR",data.TYPE_NAME)>
	</cffunction>
	
	<cffunction access="private" name="_boolean">
		<cfargument name="b" type="boolean" required="yes">
		<cfreturn arguments.b==true>
	</cffunction>
</cfcomponent>