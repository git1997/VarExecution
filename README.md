# VarExecution

Varex 2013: Implementation of the Varex paper ("Exploring Variability-Aware Execution for Testing Plugin-Based Web Applications", ICSE 2014, http://dl.acm.org/citation.cfm?id=2568225.2568300)

- src folder contains the modified Quercus source code plus Varex source code (edu.iastate.* packages).
  + The most important class is edu.iastate.hungnv.shadow.Env_ where Varex is instrumented into Quercus.
  + Results are output through edu.iastate.hungnv.debug.* and edu.iastate.hungnv.empiricalstudy.* classes. You will need to change the path constants in these files.
- WebContent/WebApps folder contains the source code of websites. You can try running /quercus/WebContent/WebApps/TestProject/test2—Branches.php first before running larger examples.
- WebContent/WebApps/WordPress-3.4.2/includes/load.php:Line 479 is where optional plugins in WordPress are injected.
