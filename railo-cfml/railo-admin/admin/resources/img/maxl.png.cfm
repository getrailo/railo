<cfsilent>
	<cfapplication name="HTTPCaching" sessionmanagement="no" clientmanagement="no" applicationtimeout="#createtimespan(1,0,0,0)#" />
	<cfif not structKeyExists(application, "oHTTPCaching")>
		<cfset application.oHTTPCaching = createObject("component", "../HTTPCaching") />
	</cfif>
	
	<!--- the string to be used as an Etag - in the response header --->
	<cfset etag = "C0065BC51976A969B93B0AF5C525D0A3" />
	<cfset mimetype = "image/png" />
	
	<!--- check if the content was cached on the browser, and set the ETag header. --->
	<cfif application.oHTTPCaching.handleResponseWhenCached(fileEtag=etag, mimetype=mimetype, expireDays=100)>
		<cfexit method="exittemplate" />
	</cfif>
</cfsilent>

<!--- file was not cached; send the data --->
<cfcontent reset="yes" type="#mimetype#"
	variable="#toBinary('iVBORw0KGgoAAAANSUhEUgAAABYAAAAWCAYAAADEtGw7AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAsZJREFUeNq0VU1vElEUncc8hg+hbYwIFFzYTQMEbMLClbrRtAt/gj/M1L/hwqqxmnZLgpC0smFRoEA0Df1IW2CG8Vy8r7md1Gj8eMnNm7kz78y95557R/m+ryzLsmFhmAPTfE/+31k+zIO5sAlsSvcGJAqLw5J8rRnY/wWoeYdAL2GnsHO61hwpgd6GZRk8zuAzBnBFRB77fT5rcZQXsK98P4/YYbDs1tbWB+sv1vr6+mP+yFhzZJT+Aj98iS3HH5tyioewDu8j9lHki5ydg6A2sccMlYZjzU5aue3t7WeeN8/mR3VQ4HQ6vVsul18ppY6YS1Mwj4OwmJo5pimSYgetpOu64dlsZhvgfD5/UCwWdwBKgN9gx5yNKa4dKKjSQjLmJRcAV2qwbdvL5XKH8FG1T2BHDD4VQFoUcX42FNCjxdWdazgUCoERz67X6w+Gw+EKXAnmNCrTFhFfaV8Cu7z3QUMI6Xeq1WrNcZzL8Xgc29/ffw6KlvA8zXYHtiS0fw1P3wB8kMlkdkql0ieKHtw2Wq3WBuh4D1oS+Oh9ZEKaP2NVJFiy17iWwBPeDyuVyiYfPIYamqlU6iM4Trbb7Ye9Xu/p6urqG/jbpKRGo/EE77/ls85NwB7vI1Eg2m8hwrugYaXT6WyAlsze3t6LcDjc6Pf72cFgkAfwlyCe5Ni0r+n5EYMPybTWo0Kh8DoSiVxMJpNorVardrvde/jojEfBT4F9EbkRv+k8ktoZpb+2tvaZJCh07ovmUkEqlBgoiwzqi24i3wI6MA6Ol0mCsisDOMoA+4HxGBd8m1mdBECy2Ww+gp5ziNgVDeQydZYIyNeBtC0x7WzRAFGowkGR3uGaCrUsmoRAe6K5XDM2zZAmp8VT6k/XCWO5CinGOEISfYoLIQe44iLb4telRfFdLi5F3meJnmo+fC6A/sWvaar+18/0uwADAGFuIZXoIWVFAAAAAElFTkSuQmCC')#" />
