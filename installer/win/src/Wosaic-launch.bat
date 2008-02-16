@echo OFF

rem A simple Windows launcher for the Wosaic project.  Simply call
rem the appropriate java command

rem TODO: Perhaps check if we the java command first, and then launch
pushd "%~dp0"
start javaw -jar bin\wosaic.jar
popd
exit /b

