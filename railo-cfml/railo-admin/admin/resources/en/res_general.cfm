<cfset stText.General.Yes         = "Yes">
<cfset stText.General.No         = "No">
<cfset stText.General.Day         = "Day">
<cfset stText.General.Days        = "Days">
<cfset stText.General.Month       = "Month">
<cfset stText.General.Year        = "Year">
<cfset stText.General.Hour        = "Hour">
<cfset stText.General.Hours       = "Hours">
<cfset stText.General.HourError   = "The field ""hour"" must contain a positive integer">
<cfset stText.General.Minute      = "Minute">
<cfset stText.General.Minutes     = "Minutes">
<cfset stText.General.MinuteError = "The field ""minute"" must contain a positive integer">
<cfset stText.General.Second      = "Second">
<cfset stText.General.Seconds     = "Seconds">
<cfset stText.General.SecondError = "The field ""second"" must contain a positive integer">
<cfset stText.General.Seconds     = "Seconds">

<!--- Login --->
<cfset stText.Login.language              = "Language">
<cfset stText.Login.Password              = "Password">
<cfset stText.Login.PasswordMissing       = "Please enter a value for the field password">
<cfset stText.Login.RetypePassword        = "Retype Password">
<cfset stText.Login.RetypePasswordMissing = "Please reenter the password">

<cfset stText.Login.OldTooShort                = "The old password is to short, its length must be at least 6 characters">
<cfset stText.Login.NewTooShort                = "The new password is to short, its length must be at least 6 characters">
<cfset stText.Login.UnequalPasswords           = "The new password and the reentered password are not the same">
<cfset stText.Login.ChangePassword             = "Change password">
<cfset stText.Login.ChangePasswordDescription  = "Change the password for this administrator">
<cfset stText.Login.OldPassword                = "Old password">
<cfset stText.Login.OldPasswordDescription     = "The old password to change">
<cfset stText.Login.OldPasswordMissing         = "Please enter a value for the old password">
<cfset stText.Login.NewPassword                = "New password">
<cfset stText.Login.NewPasswordDescription     = "The new password for the administrator">
<cfset stText.Login.NewPasswordMissing         = "Please enter a value for the new password">
<cfset stText.Login.RetypePassword             = "Retype new password">
<cfset stText.Login.RetypeNewPassword          = "Reenter the new password">
<cfset stText.Login.RetypeNewPasswordMissing   = "Please reenter the new password">
<cfset stText.Login.DefaultPassword            = "Set default password">
<cfset stText.Login.DefaultPasswordDescription = "Set the default password for all web administrators">
<cfset stText.Login.Password                   = "Password">
<cfset stText.Login.web                        = "Web">
<cfset stText.Login.resetWebPW                 = "Reset Password">
<cfset stText.Login.resetWebPWDescription      = "reset the Password of the selected Web">
<cfset stText.Login.rememberMe="Remember&nbsp;Me for">
<cfset stText.Login.s="this Session">
<cfset stText.Login.d="one Day">
<cfset stText.Login.ww="one Week">
<cfset stText.Login.m="one Month">
<cfset stText.Login.yyyy="one Year">

<!--- Overview --->
<cfset stText.Overview.Info               = "Info">
<cfset stText.Overview.Version            = "Version">
<cfset stText.Overview.VersionName        = "Version Name">
<cfset stText.Overview.ContextCount       = "Context count">
<cfset stText.Overview.ReleaseDate        = "Release date">
<cfset stText.Overview.CFCompatibility    = "ColdFusion&reg; compatibilty version">
<cfset stText.Overview.OS                 = "OS">
<cfset stText.Overview.remote_addr        = "Remote IP">
<cfset stText.Overview.server_name        = "Host Name">
<cfset stText.Overview.InstalledTLs       = "Installed tag<br/> libraries">
<cfset stText.Overview.InstalledFLs       = "Installed function<br/> libraries">
<cfset stText.Overview.DateTime           = "Railo date/time">
<cfset stText.Overview.ServerTime         = "Server date/time: ">
<cfset stText.Overview.Contact            = "Contact">
<cfset stText.Overview.ContactInfo        = "Info">
<cfset stText.Overview.InfoMail           = "info@railo.ch">
<cfset stText.Overview.Sales              = "Sales">
<cfset stText.Overview.SalesMail          = "sales@railo.ch">
<cfset stText.Overview.BugReport          = "Bug report">
<cfset stText.Overview.BugReportMail      = "bugs@railo.ch">
<cfset stText.Overview.FeatureRequest     = "Feature request">
<cfset stText.Overview.FeatureRequestMail = "features@railo.ch">

<cfset stText.Overview.SerialNumber            = "Serial number">
<cfset stText.Overview.SerialNumberDescription = "Serial number for Railo">

<cfset stText.Overview.Support="Support">
<cfset stText.Overview.Forum="Forum">
<cfset stText.Overview.Mailinglist_de="Mailinglist (german)">
<cfset stText.Overview.Mailinglist_en="Mailinglist (english)">
<cfset stText.Overview.Professional="Professional Support">
<cfset stText.Overview.issueTracker="Issue Tracker">
<cfset stText.Overview.blog="Blog">

<cfset stText.Overview.contexts.title="Web contexts">
<cfset stText.Overview.contexts.label="Label">
<cfset stText.Overview.contexts.url="URL">
<cfset stText.Overview.contexts.webroot="Webroot">
<cfset stText.Overview.contexts.config_file="Config file">

<!--- Introduction --->
<cfset  stText.Overview.introdesc.web="
Railo, the CFML engine - free, open source and easy to use. This Web Administrator is provided in order to customize your web context.">

<cfset  stText.Overview.introdesc.server="The Server Administrator allows you to install updates and patches for your Railo installation and to restart the engine with a mouse click. You can preconfigure new web contexts and define restrictions and configurations per web context individually.">

<cfset stText.Overview.Licence="Available Editions">
<cfset stText.Overview.LicenceDesc="Railo is available in four different editions , adapted to the different areas of application and budgets.">

<cfset stText.Overview.LicenceDevelop="Develop">
<cfset stText.Overview.LicenceDevelopDesc="The Develop Version is a version, addressing those users who are most likely to apply it in order for assembling CFML appliction . The version though is prohibited to be used commercially.">


<cfset stText.Overview.LicenceCommunity="Community">
<cfset stText.Overview.LicenceCommunityDesc="Railo Community is intended to be used by low budget business applicants. Except for some few limitations the Community Version offers the entire language range of Railo.">

<cfset stText.Overview.LicenceProfessional="Professional">
<cfset stText.Overview.LicenceProfessionalDesc="The Professional Version offers the full language range of Railo, no restriction to the use of any feature has to be expected. With the Professional Version several web contextes can be used on one server, the number of web contextes however is limited by the licence price.">

<cfset stText.Overview.LicenceEnterprise="Enterprise">
<cfset stText.Overview.LicenceEnterpriseDesc="The Enterprise Version of Railo is the biggest and most comprehensive version intended to be used in a larger context.">

<cfset stText.Overview.LicenceDevelopMore="more">

<cfset stText.Overview.purchase="Purchase your Railo license"> 

<cfset stText.Overview.used="currently in use">


<cfset stText.locale = "english (united kingdom)">