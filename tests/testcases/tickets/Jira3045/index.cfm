<cfscript>
setting showdebugoutput="no";
exp=dateAdd("d", 30, now());
COOKIE.test1 ={ domain: ".mydomain.com", path: "/", value: "Hello", expires: exp, httpOnly: true };

cookie name="test2" domain=".mydomain.com" path="/" value="Hallo" expires="#exp#" httpOnly="true";
</cfscript>
