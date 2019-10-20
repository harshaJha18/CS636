rem for Windows
rem be sure to load mysql before using this the first time
rem Usage: runOnMysql SystemTest|...
rem Depends on env vars MYSQL_SITE, MYSQL_USER, and MYSQL_PW
java -cp target/music1-1-jar-with-dependencies.jar cs636.music.presentation.%1 jdbc:mysql://%MYSQL_SITE%/%MYSQL_USER%db %MYSQL_USER% %MYSQL_PW%
