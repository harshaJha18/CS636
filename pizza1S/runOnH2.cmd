rem for Windows
rem be sure to load H2 before using this the first time
rem Usage: runOnH2 SystemTest|TakeOrder|ShopAdmin
rem This is an "executable jar" that is config'd to execute ClientApplication
java -jar target/pizza1S-1.jar %1
