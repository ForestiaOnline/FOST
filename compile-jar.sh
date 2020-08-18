#!/bin/bash
src="FOST/src"
rm -f FOST.jar
cd ..
jar cf $src/FOST.jar $src/*.java
cd $src
javac *.java
jar ufe FOST.jar Main *.class
cd ..
rm src/*.class
mv src/FOST.jar FOST.jar
chmod 705 FOST.jar
