@ECHO OFF
SET "ReadMe=FOST\README.md"
ATTRIB -r FOST.jar
DEL FOST.jar
CD ..
jar cf FOST\src\FOST.jar FOST\src\*.java FOST\docs FOST\LICENSE FOST\VERSION %ReadMe%
CD FOST\src
javac *.java
jar ufe FOST.jar Main *.class
CD ..
DEL src\*.class
MOVE src\FOST.jar FOST.jar
ATTRIB +r FOST.jar
