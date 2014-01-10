<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="setUp"></cffunction>
	<cffunction name="test">
	
<!--- test to see what happens with getBaseTagData --->
<cfimport prefix="tags" taglib="./Jira0229/" />

<cfsavecontent variable="local.content">
<cfsetting enablecfoutputonly="yes">
<!--- getBaseTagData will thrown an error --->
<tags:mytag label="lvl 1">
  <tags:mytag label="lvl 2">
    <tags:mytag label="lvl 3"></tags:mytag>
  </tags:mytag>
</tags:mytag>
<cfsetting enablecfoutputonly="no">
</cfsavecontent>
<cfset assertEquals(
		"{Start:lvl 1}{Start:lvl 2}{label:lvl 2;parent-label:lvl 1}{Start:lvl 3}{label:lvl 3;parent-label:lvl 2;parent-parent-label:lvl 1}{End:lvl 3}{End:lvl 2}{End:lvl 1}"
		,trim(content))>

<!--- the instance number on getBaseTagData correctly 
      works when the tags are not immediately nested --->
<cfsavecontent variable="local.content">
<cfsetting enablecfoutputonly="yes">
<tags:mytag label="lvl 1">
  <tags:mytag2>
    <tags:mytag label="lvl 2">
      <tags:mytag2>
        <tags:mytag label="lvl 3"></tags:mytag>
      </tags:mytag2>
    </tags:mytag>
  </tags:mytag2>
</tags:mytag>
<cfsetting enablecfoutputonly="no">
</cfsavecontent>

<cfset assertEquals(
		"{Start:lvl 1}{Start:lvl 2}{label:lvl 2;parent-label:lvl 1}{Start:lvl 3}{label:lvl 3;parent-label:lvl 2;parent-parent-label:lvl 2}{End:lvl 3}{End:lvl 2}{End:lvl 1}"
		,trim(content))>

	
	</cffunction>
</cfcomponent>