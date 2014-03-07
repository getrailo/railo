Railo CFML Engine
=================

Welcome to the Railo CFML Engine source code repository.

Railo is a high performance, open source, CFML engine.  For more information visit the project's homepage at http://www.getrailo.org/


Building from source
--------------------


### 1. Before you get started

Before you can start building Railo from source, you will need a few things installed on your machine:

1. **Java JDK** - since you're going to compile Java code you need the JDK and not just the JRE.  Railo requires JDK 6 or later in order to compile.  http://www.oracle.com/technetwork/java/javase/downloads/

1. **Apache ANT** - the source code contains several build scripts that will automate the build process for you. you will need ANT installed in order to run these build scripts. http://ant.apache.org/bindownload.cgi

1. **A runnable copy of Railo** - you will need a running instance of Railo on the local machine as part of the build process. http://www.getrailo.org/index.cfm/download/

If you are familiar with Eclipse and GIT, we recommend using these tools as well.


### 2. Get the source code

Railo's source code is version-controlled with GIT and is hosted on github.com (https://github.com/getrailo/railo - chances are that this is where you're reading this right now ;]).

The repository contains a few branches, with the most important ones being Master (current release) and Develop (alpha and beta releases).

If you are familiar with GIT, then by all means, clone the repository and grab all the branches at once.

If you are not comfortable working with GIT, an easier way to grab the source code of a given branch is by downloading a zipball:

* **Master Branch** - https://github.com/getrailo/railo/zipball/master

* **Develop Branch** - https://github.com/getrailo/railo/zipball/develop

If you require other branches, simply change the branch you're on from the drop-down above and then click the button labeled ZIP.

Extract the contents of the ZIP archive into a work directory, e.g. /railo-source.  Inside that work directory you will now have the folders **/railo-cfml** and **/railo-java** and a few other files (including this one).


### 3. Configure and Start a local instance of Railo

A portion of the Railo code is written in CFML, so you will need a running instance of Railo on your local machine in order to compile it.  In a sense, we are using Railo to build Railo. How meta is that! :)

The easiest way to configure and run a local instance of Railo is by grabbing a copy of Railo Express from http://www.getrailo.org/index.cfm/download/

The root of that server should point to the **/railo-cfml** folder in the source code that you downloaded.  If you are using Railo Express, you can set that as follow:

1. Edit {railo-express}/contexts/railo.xml and modify the resourceBase element so that it points to the /railo-cfml folder, e.g.

        <Set name="resourceBase"><SystemProperty name="jetty.home" default="."/>C:/workspace/railo-source/railo-cfml/</Set>
        
        TIP: Later versions of Railo Express may have a www.xml file instead of a railo.xml file.  Rename the file from www.xml to railo.xml and modify the resourceBase element as indicated above.

2. Start the Railo Express instance by running {railo-express}/start or {railo-express}/start.bat

3. Browse to the Admin of that Railo instance, e.g. http://localhost:8888/railo-context/admin/server.cfm and set the Admin's password.

        TIP: If you are using the Railo Express version, the default port number is 8888.
        TIP: Set the Server Administrator password as well as the Web Administrator password.

Note the URL of that Railo instance.  You will need it in the next step.


### 4. Edit /railo-core/build.properties

The build process will connect to the local instance of Railo in order to compile some of the code.  

You need to edit **/railo-java/railo-core/build.properties** and let the build script know where to find the local Railo instance.  

Change the line that reads *railo.url=http://compile/compileAdmin.cfm* so that the value of railo.url points to your local instance of Railo, e.g.
    
    railo.url=http://localhost:8888/compileAdmin.cfm


### 5. Edit /railo-core/src/railo/runtime/Info.ini

The build process will generate a patch file that you can deploy as an update to Railo servers.  In order for the patch to work, its version must be higher than the current version on the server that you wish to patch.

You should set the version in **/railo-java/railo-core/src/railo/runtime/Info.ini**

At the time of this writing, the content of that file is:

    [version]
    number=4.1.0.000
    level=os
    state=alpha
    name=Endal
    name-explanation=http://en.wikipedia.org/wiki/Endal_(Dog)
    release-date=2012/07/16 12:15:25 CET

Simply edit the value of the number property so that it is higher than the version on the server that you plan to patch, for example: *4.1.0.111*


### 6. Run ANT

Open a Command Prompt (or Shell) and change the working directory to /railo-java/railo-master and run ant by simply typing 

    ANT

When prompted, enter the Admin password that you've set in step 3.

    TIP: ANT's path must be in the system's executables PATH.

The build process should take a minute or two.  Once it's finished, you can find the newly built patch file in **/railo-java/railo-core/dist/**

The filename will be the version number that you've set in step 5, with the extension .rc, for example: *4.1.0.111.rc*


### Congratulations!  You've just built Railo from source :)
