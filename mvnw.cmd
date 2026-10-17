@echo off
set MAVEN_VERSION=3.9.11
set WRAPPER_BASE=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_HOME=%WRAPPER_BASE%\apache-maven-%MAVEN_VERSION%
set MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  if not exist "%WRAPPER_BASE%" mkdir "%WRAPPER_BASE%"
  if not exist "%WRAPPER_BASE%\apache-maven-%MAVEN_VERSION%-bin.zip" powershell -NoProfile -Command "Invoke-WebRequest '%MAVEN_URL%' -OutFile '%WRAPPER_BASE%\apache-maven-%MAVEN_VERSION%-bin.zip'"
  powershell -NoProfile -Command "Expand-Archive -Force '%WRAPPER_BASE%\apache-maven-%MAVEN_VERSION%-bin.zip' '%WRAPPER_BASE%'"
)
call "%MAVEN_HOME%\bin\mvn.cmd" %*
