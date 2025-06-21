@echo off
@rem Use this script to start key
if "x%KEYHOME%"=="x" goto notdef
	rem make sure CLASSPATH is set properly
	call %KEYHOME%\windows\setvars.bat
	cd %KEYHOME%
	java key.Main

goto endof
:notdef
echo Please set your KEYHOME environment variable to be where Key is installed
echo for example, type: set KEYHOME=C:\Projects\key and put this in your 
echo AUTOEXEC.BAT (or, if you're running NT, with the System Control Panel)
:endof
