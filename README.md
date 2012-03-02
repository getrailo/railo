## Railo CFML Engine

Welcome to the Railo CFML Engine repostory

Building from source modified
--------------------

### 1. Before you get started
Before you start building Railo from source, you are going to need a few things installed on your machine:

1. Eclipse for JEE - this is the easiest Eclipse bundle to work with when building Java projects <http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/heliosr>
1. Java 5 JDK - not just the JRE (because you're going to be compiling Java code) - and not Java 6! Railo requires Java 5 to build correctly! (CHECK)
1. A Git client, any client will do. The demo here will be using the command line client to keep it simple <http://git-scm.com/>. There is also the EGit plugin for Eclipse for you to commit your changes locally and create patch files for submission <http://www.eclipse.org/egit/>
1. A running Railo installation in which to test your new patch file <http://www.getrailo.org/index.cfm/download/>


### 2. Get the source code from git://github.com/getrailo/railo.git
The main way to get the code for Railo is to clone it with git. So at a command line (or using your favourite client) clone the source of the project into a folder on your computer:

    git clone git://github.com/getrailo/railo.git


after a little while you should see something like:

	Cloning into railo...
	remote: Counting objects: 11038, done.
	remote: Compressing objects: 100% (4138/4138), done.
	remote: Total 11038 (delta 7162), reused 10404 (delta 6612)
	Receiving objects: 100% (11038/11038), 59.48 MiB | 639 KiB/s, done.
	Resolving deltas: 100% (7162/7162), done.
	
That's it! you have downloaded all the source code for Railo!

### 3. Setup Eclipse for JVM JDK5
Currently we build Railo with JDK5, so you need to setup Eclipse to use that JVM as default. you can do this by going to:

	Eclipse (or Window on Windows) -> Preferences -> Java -> Installed JREs 
	
Here you can click "Add..." to add the JRE 5 and select the checkbox to make it your default.

Note: On OSX this will be under "/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home"


### 4. Import the code into Eclipse
Start up Eclipse, if you haven't already, and go to the menu
	
	File -> Import... -> General -> Existing Projects into Workspace...
	
Select the root folder where you cloned the source and Eclipse should find the following projects:
	
	Railo
	Railo Source
	Railo-CFML
	Railo-Core
	Railo-Loader
	
Click finish and you now should have all your projects nicely imported into your Eclipse workspace. 

### 5. Fixing the build paths
Now that you have imported the projects you will see some exclamation marks next to each project, we shall go and fix those as follows:

1. Right click on Railo-Core -> Buld Path -> Configure Build Path... you should see a number of errors in the Libraries tab
1. Select all the erroneous paths (about 92) since they are missing, and Remove them. Making sure not to remove the JRE System Library
1. We shall now re-add the Jars. Click on Add External Jars... and select the path to <checkoutdirectory>/railo-java/libs and select all the jar files click ok, and click ok again. 
1. Repeat the process above for the Railo-Loader project

There might also be under "Libraries" an entry that says 'JRE System Library [1.5.0] unbound' which you will need to remove and click "Add Library..." select a JRE System Library and select the "Workspace default JRE (JVM 1.50)" which we added in step 3. 


### 6. Setting your build version
Now that we have the projects imported we need to set the build version for our brand new version of Railo. To do this you need to edit the following file in the Railo-Core project:

	/Railo-Core/src/railo/runtime/Info.ini
		
At the point of writing, the Info.ini looks like:

	[version]
	number=3.1.2.018
	level=os
	state=final
	name=Barry
	name-explanation=http://en.wikipedia.org/wiki/Barry_(dog)
	release-date=2010/07/22 00:00:00	

So for this example, we are going to change the number entry to:

	[version]
	number=3.1.2.118
	
As you create new builds of railo you will want to be increase the version number so you can see which patches youh have applied. 

### 7. Setting up the Compile server

As part of the build process we need to setup an instance of Railo (as mentioned previously in the requirements). This example uses the railo-3.1.2.001-railo-express-macosx.zip that you can obtain from <http://www.getrailo.org/index.cfm/download/> which is using the Jetty server. You can use the apropiate express version for your OS. 

1. Once unzipped, open "/railo-3.1.2.001-railo-express-macosx/contexts/railo.xml" and change:

		<Set name="resourceBase"><SystemProperty name="jetty.home" default="."/>/webroot/</Set>
		
	to point to the directory where the Railo-CFML project is, for example:

		<Set name="resourceBase">/Users/markdrew/Projects/railo/railo-cfml/</Set>

1. Start the server by either double clicking on start or start.bat files. 
1. Log into the Railo Administrator by going to: <http://localhost:8888/railo-context/admin/web.cfm> and set a password
1. Edit /Railo-Core/build.properties to add the URL of our compile script:
		
		railo.url=http://localhost:8888/compileAdmin.cfm

### 8. Building Railo
Open up the Railo Source project and right click on "build.xml" and select:

	Run As > Ant Build
	
This will kick of the process of building your patch file and will take a few seconds, if all goes well you will get something like:

	BUILD SUCCESSFUL
	Total time: 24 seconds
	
Right-click on the Railo-Core project and select "Refresh", you should now see a new folder called "dist" with file inside called "3.1.2.118.rc" (or whatever the version number you setup in Info.ini). Well done! This is your patch file that you can apply to your server! 

### 9. Deploying your new build
Now that you have your patch file, all you have to do is copy it from the "railo/railo-java/railo-core/dist" folder to your current running Railo server, to the folder "your-railo-server/lib/railo-server/patches" and restart the server. 

Once you have restarted, if you go to the web or server administrator you should see:

	Version	Railo 3.1.2.118 final

	
Well done! you have now built Railo! 


### 10. Posting patches and fixes
If you have modified the code and want to submit a patch, you should post your issues at the JIRA bug tracker: <https://jira.jboss.org/browse/RAILO>



## Problems on OS X Snow Leopard 

There are some problems on OS X Snow Leopard, especially when dealing with the fact that you don't actually have Java 1.5.0 which is required to build the source, there are work rounds for this, see: <http://chxor.chxo.com/post/183013153/installing-java-1-5-on-snow-leopard>

>Download the official Java package from Apple, �Java for Mac OS X 10.5 Update 7” dated May 18, 2010.

>Then use the excellent shareware utility Pacifist to open the downloaded JavaForMacOSX10.5Update4.pkg file.

>How to install

>1) First use Finder to go to System > Library > Frameworks > JavaVM.framework > Versions and delete the two aliases (symlinks) >“1.5” and “1.5.0”. Don’t skip this step, because otherwise the extraction will follow the symlinks and overwrite the contents 
>of the 1.6.0 folder, oops.

>2) In Pacifist, drill down into Contents > System > Library > Frameworks > JavaVM.framework > Versions.

>3) In Pacifist, select 1.5 and 1.5.0, right-click, and chose Install to Default Location


Also if you get an error like:
>java.lang.UnsatisfiedLinkError: Cannot load 32-bit SWT libraries on 64-bit JVM

>   at org.eclipse.swt.internal.Library.loadLibrary(Library.java:182)

>    at org.eclipse.swt.internal.Library.loadLibrary(Library.java:159)

see <http://www.agynamix.de/blog/run-32bit-swt-apps-from-eclipse-in-snow-leopard/>

