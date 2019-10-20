rem for Linux/Mac
rem run app or web case using DB's profile
rem i.e., not using mvn and its ability to access its library repository
rem be sure to load DB before using this the first time, have current jar built
rem Usage: runJarByWebProfile.sh oracle|mysql|h2 web|SystemTest|TakeOrder|ShopAdmin
rem This makes "active" one of the profiles (oracle or ...) defined in src/main/resources
rem which partially overrides the setup in application.properties
java -jar -Dspring.profiles.active=%1 target/pizza3-1.jar %2

