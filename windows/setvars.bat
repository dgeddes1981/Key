@echo off
if "x%KEYHOME%"=="x" goto notdef
set CLASSPATH=%KEYHOME%\classes;%KEYHOME%\external\com.mortbay.Jetty.jar;%KEYHOME%\external\javax.servlet.jar;%KEYHOME%\external\gnujsp.jar:%CLASSPATH%
echo KEYHOME is %KEYHOME%

goto endof
:notdef
echo Please set your KEYHOME environment variable to be where Key is installed
echo for example, type: set KEYHOME=C:\Projects\key and put this in your 
echo AUTOEXEC.BAT (or, if you're running NT, with the System Control Panel)
:endof

