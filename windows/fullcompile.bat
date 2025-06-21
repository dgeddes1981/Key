@echo off
@rem Use this script to compile everything.
if "x%KEYHOME%"=="x" goto notdef
	rem make sure CLASSPATH is set properly
	call %KEYHOME%\windows\setvars.bat

	echo Starting a full compile of key
	cd %KEYHOME%\src\key
	javac -d %KEYHOME%\classes collections\*.java commands\*.java commands\clan\*.java config\*.java core\*.java effect\*.java effect\forest\*.java events\*.java exceptions\*.java hack\*.java io\*.java paragraphs\*.java primitive\*.java sql\*.java talker\*.java talker\forest\*.java talker\objects\*.java talker\te0\*.java terminals\*.java transient\*.java trusted\*.java util\*.java web\*.java web\servlets\*.java
	echo Compile complete

goto endof
:notdef
echo Please set your KEYHOME environment variable to be where Key is installed
echo for example, type: set KEYHOME=C:\Projects\key and put this in your 
echo AUTOEXEC.BAT (or, if you're running NT, with the System Control Panel)
:endof
