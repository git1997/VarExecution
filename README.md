# VarExecution

Varex 2013: Implementation of the Varex paper ("Exploring Variability-Aware Execution for Testing Plugin-Based Web Applications", ICSE 2014, http://dl.acm.org/citation.cfm?id=2568225.2568300)

Source Code Structure
=====================
- src folder contains the modified Quercus source code plus Varex source code (edu.iastate.* packages).
  + The most important class is edu.iastate.hungnv.shadow.Env_ where Varex is instrumented into Quercus.
  + Results are output through edu.iastate.hungnv.debug.* and edu.iastate.hungnv.empiricalstudy.* classes (some of them are not currently being used). You might need to change the path constants in these files if later on you decide to use them.
- WebContent/WebApps folder contains the source code of websites.
- WebContent/WebApps/WordPress-3.4.2/includes/load.php:Line 479 is where optional plugins in WordPress are injected. You can download WordPress-3.4.2 and put it in the WebApps folder.

Getting started
===============
0. Install Eclipse for JavaEE and a web server like Tomcat.
1. Set the value of edu.iastate.hungnv.util.Logging.WORKSPACE to a folder on your computer. This is where the results are stored.
2. Set the value of edu.iastate.hungnv.shadow.Env_.INSTRUMENT to "true" to run in Varex mode, or "false" to run a concrete execution (without variability-aware capability).
3. Run WebContent/WebApps/TestProject/test-GetStarted.php and check the output to familiarize yourself with Varex.

Contact
=======
Please feel free to contact the author if you have any questions.