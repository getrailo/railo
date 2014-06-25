<cfscript>
url.number=1; // avoid exception thrown by param itself 
param numeric url.number;  
param url.number; 
param name="url.number";
param name="url.number" type="numeric";
</cfscript>