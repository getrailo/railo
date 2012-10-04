TEST Functions!

<cfset stringToEnc = "<script>alert('Evlis');</script>">
<cfoutput>
	<pre>
	#encodeForHTML(stringToEnc)#
	#encodeForCSS(stringToEnc)#
	#encodeForJavaScript(stringToEnc)#
	#encodeForHTMLAttribute(stringToEnc)#
	#encodeForXML(stringToEnc)#
	#encodeForXMLAttribute(stringToEnc)#
	#encodeForURL(stringToEnc)#
	#decodeFromURL(encodeForURL(stringToEnc))#
	
	#encodeForLDAP(stringToEnc)#
	#encodeForDN(stringToEnc)#
	#encodeForXPath(stringToEnc)#
	</pre>
   
   
<!---  
	/*  	
	


	
	encodeForVBScript(stringToEnc);
	
	encodeForSQL(org.owasp.esapi.codecs.Codec arg0, java.lang.String arg1);
	encodeForOS(org.owasp.esapi.codecs.Codec arg0, java.lang.String arg1);

	encodeForURL(stringToEnc) throws org.owasp.esapi.errors.EncodingException;
	decodeFromURL(stringToEnc) throws org.owasp.esapi.errors.EncodingException;
	encodeForBase64(byte[] arg0, boolean arg1);
	decodeFromBase64(stringToEnc) throws java.io.IOException;
	*/
--->
</cfoutput>
