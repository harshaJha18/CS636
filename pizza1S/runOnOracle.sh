# for UNIX/Linux/MacOSX
# be sure to load Oracle before using this the first time
# Usage runOnOracle SystemTest|TakeOrder|ShopAdmin
# Depends on env vars ORACLE_SITE, ORACLE_USER, and ORACLE_PW
java -jar target/pizza1S-1.jar $1 jdbc:oracle:thin:@${ORACLE_SITE} ${ORACLE_USER} ${ORACLE_PW}
