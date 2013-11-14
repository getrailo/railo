<cfsetting showdebugoutput="no">
<cfcontent type="#isDefined('url.text')?'application/javascript;charset=UTF-8':'application/unknow'#">
<cfheader name="Content-Encoding" value="gzip">