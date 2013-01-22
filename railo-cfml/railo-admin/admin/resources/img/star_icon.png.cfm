<cfsavecontent variable='content'>iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYxIDY0LjE0MDk0OSwgMjAxMC8xMi8wNy0xMDo1NzowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNS4xIE1hY2ludG9zaCIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpCRDgwOEM1NjVGRjUxMUUxQjQxMUY4OTA2QkQ2MzZDMCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpCRDgwOEM1NzVGRjUxMUUxQjQxMUY4OTA2QkQ2MzZDMCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkJEODA4QzU0NUZGNTExRTFCNDExRjg5MDZCRDYzNkMwIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkJEODA4QzU1NUZGNTExRTFCNDExRjg5MDZCRDYzNkMwIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+JvJrqAAAAgZJREFUeNpUks9rE0EUxz87s6mbpklsG7W09mIPnlToxYuIf0L/CEEoitCj4MGbF0GkCF49VLx5by+KJ2/iqUVbfxQSUjVmY7LNZnfGNzsJwWEfO/Pm+977vvedwL5chgCYzaEsZvErME+wNsaqR/4slmqIQ/DfxKl8UGDkn99g+cIWuexb7Tfo8GNxN3TwAoRisoxEZ3JMZT9S96lfhLPCxpa2Cv8onLKRNQ201mcbmZvUzm3AGdBzUJnfIMvXfdQ00tW+JUHXCfWaUFgiqqwzvxKSjNM2VmvEySsC+wllm5jsu+DeBfb1apdoqcZiA0oloTxO7PpzSwc+gZJ/fyDD6Uo77QNFrh6Sp4Z0Bv7K5cCZG4Ty5s59sVj2ZlaK5Sk5j0OMesbv7pD4cJuFSyGh+m8Ivn83PJGq9yNh2LlNOdhRRZe69IKfvXscNdNCq2EwNSeTs8PmgJPOHbTe8Tp+HmdLzHuiZMiKniFzPVqvr+vNTbybnhLoXTJJrFy9jvaATF+hXq1y6oBSoSI9GwlIRgWQSn2Bdm9NdG5h3QOIpG5U8L1MZVGSuKH0hdr+F74efMMMxSfTzgQzMFcZuGR2+uqIJFCJBMdHLeI/28LiuXjL/Nq/S+P8JmFYpzp3DWsKhiFm8obUHs1jyyh9Skl/ELGdtyO0HtA+2RWNN9Hq7QT/T4ABAF+eyYxUzqtdAAAAAElFTkSuQmCC</cfsavecontent>

	<cfsetting showdebugoutput='#false#'>
	<cfif getBaseTemplatePath() == getCurrentTemplatePath()>	

		<cfapplication name='__RAILO_STATIC_CONTENT' sessionmanagement='#false#' clientmanagement='#false#' applicationtimeout='#createtimespan( 1, 0, 0, 0 )#'>
				
		<cfset etag 	= '''6FBAA94801D143D79F391AA49E417D0A'''>
		<cfset mimetype = 'image/png'>		

		<cfheader name='Expires' value='#getHttpTimeString( now() + 100 )#'>
		<cfheader name='Cache-Control' value='max-age=#86400 * 100#'>		
		<cfheader name='ETag' value='#etag#'>

		<cfif len( CGI.HTTP_IF_NONE_MATCH ) && ( CGI.HTTP_IF_NONE_MATCH == '#etag#' )>

			<!--- etag matches, return 304 !--->
			<cfheader statuscode='304' statustext='Not Modified'>
			<cfcontent reset='#true#' type='#mimetype#'><cfabort>
		</cfif>

		<!--- file was not cached; send the content !--->
		<cfcontent reset='#true#' type='#mimetype#' variable='#toBinary( content )#'><cfabort>
	<cfelse>

		<cfcontent reset='#true#'><cfoutput>content:image/image/png;base64,#content#</cfoutput><cfabort>
	</cfif>