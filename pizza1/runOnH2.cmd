rem for Windows
rem be sure to load H2 before using this the first time
rem Usage: runOnH2 SystemTest|TakeOrder|ShopAdmin
java -cp target/pizza1-1-jar-with-dependencies.jar cs636.pizza.presentation.%1
