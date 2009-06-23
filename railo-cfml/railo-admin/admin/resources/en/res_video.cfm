<cfset stText.video.provider="Provider">
<cfset stText.video.providerMissing="Missing provider definition">
<cfset stText.video.upload="Upload">
<cfset stText.video.uploadMissing="Missing upload definition">
<cfset stText.video.server.installedNotDesc="For the tag cfvideo/cfvideoplayer OS specific video components are required. They are not bundled with Railo because the size of the software would increase a lot and because some codecs may not be redistributed, although their use is not prohibited. Therefore you can download these components directly from a provider and upload them with the form below.">



<cfset stText.video.server.installedNotURLTitle="Download video components by an URL">
<cfset stText.video.server.installedNotURLDesc="The video componenten are downloaded automatically from the remote server and copied into Railo (no installation).">
<cfset stText.video.server.installedNotUploadTitle="Video components by upload">
<cfset stText.video.server.installedNotUploadDesc="The video components (ffmpeg.zip) are uploaded directly over the form and copied into Railo (no installation). As a source you can use e.g. {provider}">
<cfset stText.video.server.installed="The necessary video components are already installed on your system.">
<cfset stText.video.server.manTitle="Manual installation">
<cfset stText.video.server.manDesc="A manuelle installation can be made like follows: Open the url {provider} and download the corresponding file ffmpeg.zip for your operating system ({OS-Name}). Then copy this file (do not unzip it) into the directory {directory}. If you can not find a file for your operating system, just contact us.">
<cfset stText.video.server.installedBut1="The video components are installed but they can not be executed properly:">
<cfset stText.video.server.installedBut2="A manual installation is recommended.">
<cfset stText.video.web.installed="The video components are installed on your system.">
<cfset stText.video.web.installedBut="The video components are installed but they can not be executed properly.Just switch to the Railo Server Administrator, in order to repair it:">