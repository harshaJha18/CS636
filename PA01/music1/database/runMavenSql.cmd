rem run SQL script on specified DB with a profile in pom.xml
rem Usage: doMavenSql h2file|oracle|mysql createdb|dropdb|showdb
mvn sql:execute@%2 -P%1
