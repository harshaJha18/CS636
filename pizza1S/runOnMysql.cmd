rem for Windows
rem be sure to load mysql before using this the first time
rem Usage: runOnMysql SystemTest|TakeOrder|ShopAdmin
rem Depends on env vars MYSQL_SITE, MYSQL_USER, and MYSQL_PW
java -jar target/pizza1S-1.jar %1 jdbc:mysql://%MYSQL_SITE%/%MYSQL_USER%db %MYSQL_USER% %MYSQL_PW%
