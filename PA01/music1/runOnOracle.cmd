rem for Windows
rem be sure to load Oracle before using this the first time
rem Usage: runOnOracle SystemTest|...
rem Depends on env vars ORACLE_SITE, ORACLE_USER, and ORACLE_PW
java -cp target/music1-1-jar-with-dependencies.jar cs636.music.presentation.%1 jdbc:oracle:thin:@%ORACLE_SITE% %ORACLE_USER% %ORACLE_PW%
