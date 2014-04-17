Railo-Tests
===========

Unit Tests for Railo Server.

This Test Suite needs no addional installation.
To write your own testcases, check out the folder "/testcase-templates", there you can find various testcase templates with readme to all of them.


Mapped Folders
--------------

To run the tests from a mapped folder, create the following two mappings in the server or web admin:

    virtual:    /railo-tests
    resource:   {path-of-folder}/railo-tests
    primary:    Resource
    inspect:    Always
    
    virtual:    /testcases
    resource:   {path-of-folder}/railo-tests/testcases
    primary:    Resource
    inspect:    Always
    
Then you can run the tests by calling Railo at /railo-tests/index.cfm, for example

    http://localhost:8888/railo-tests/index.cfm
    
    
