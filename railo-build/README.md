Railo Server Build Process

The build process can be broken down as follows:

1. setting date and version in "Railo-Core/src/railo/runtime/Info.ini" 
(FYI:in Appollo there will be only 3 digits for the version, the patch number will be based on the date)

2. building the railo-context.ra 
just execute the attached admin-ra.cfm

3. build .rc and railo.jar
see attached java files starting with "WriteOSJar"
(this is splited in 2 version because prior version had a obfuscator and this has stopped the process)

4. build war, jar zip railo express ...
just execute the attached install-os.cfm, just say mw with resources you need here