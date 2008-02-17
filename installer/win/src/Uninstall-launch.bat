@echo OFF

rem A simple launcher for the Wosaic uninstaller.  Simply run the appropriate jar
rem We really only have this so Vista users can choose to "Run as Administrator.."

rem TODO: Perhaps check if we the java command first, and then launch
"%~dp0\Uninstaller\uninstaller.jar"
exit /b

