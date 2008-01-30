@echo OFF

rem A simple launcher for the Wosaic uninstaller.  Simply call the appropriate
rem java command

rem TODO: Perhaps check if we the java command first, and then launch
start javaw -jar "%~dp0\Uninstaller\uninstaller.jar"
exit /b

