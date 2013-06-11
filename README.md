## Railo CFML Engine

Welcome to the Railo CFML Engine repostory

Building from source
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

### 3. Change into the railo-build directory and run ant
	
	cd railo/railo-build
	ant

Currently the default build creates the .rc file (for whichever version you have set in the railo-build/build.properties file) (this is not reflected everywhere yet! It is only used to name the file, you really want to change it in railo-core/src/railo/runtime/Info.ini too)

Once the build has finished you will find the files in the railo-build/build directory


Notes
-----
This is currently a work in progress, so if you encounter any issues, please let us know!
