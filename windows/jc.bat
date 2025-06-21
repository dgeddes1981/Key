@echo off
@rem Use this script to compile an individual java file, like "jc MyFile.java".  It
@rem automatically puts the .class into the right directory
if "x%KEYHOME%"=="x" goto notdef
	rem make sure CLASSPATH is set properly
	call %KEYHOME%\windows\setvars.bat
	javac -d %KEYHOME%\classes %1 %2 %3 %4 %5 %6 %7 %8 %9

goto endof
:notdef
echo Please set your KEYHOME environment variable to be where Key is installed
echo for example, type: set KEYHOME=C:\Projects\key and put this in your 
echo AUTOEXEC.BAT (or, if you're running NT, with the System Control Panel)
:endof
