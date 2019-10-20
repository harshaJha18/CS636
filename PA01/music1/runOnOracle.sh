music# for UNIX/Linux/MacOSX
# be sure to load Oracle before using this the first time
# Usage runOnOracle SystemTest|...
# Depends on env vars ORACLE_SITE, ORACLE_USER, and ORACLE_PW
java -cp target/music1-1-jar-with-dependencies.jar cs636.music.presentation.$1 jdbc:oracle:thin:@${ORACLE_SITE} ${ORACLE_USER} ${ORACLE_PW}
