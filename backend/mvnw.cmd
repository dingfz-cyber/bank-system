@echo off
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set MVN_HOME=%DIRNAME%.mvn\maven\apache-maven-3.9.9
set MAVEN_OPTS=%MAVEN_OPTS%

"%JAVA_HOME%/bin/java.exe" %MAVEN_OPTS% -classpath "%MVN_HOME%/lib/*" org.codehaus.classworlds.Launcher %*
endlocal
