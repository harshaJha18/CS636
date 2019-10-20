rem for Windows
rem be sure to load Oracle before using this the first time
rem Usage: runOnOracle SystemTest|TakeOrder|ShopAdmin
rem Depends on env vars ORACLE_SITE, ORACLE_USER, and ORACLE_PW
java -cp target/pizza1-1-jar-with-dependencies.jar cs636.pizza.presentation.%1 jdbc:oracle:thin:@%ORACLE_SITE% %ORACLE_USER% %ORACLE_PW%
