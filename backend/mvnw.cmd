@REM ----------------------------------------------------------------------------
@REM Maven Wrapper (Maven 3.9.x / Spring Boot 3.4 compatible)
@REM ----------------------------------------------------------------------------

@set MAVEN_PROJECT_ROOT=%~dp0
@set WRAPPER_LAUNCHER=%MAVEN_PROJECT_ROOT%.mvn\wrapper\maven-wrapper.jar
@set WRAPPER_PROPERTIES=%MAVEN_PROJECT_ROOT%.mvn\wrapper\maven-wrapper.properties

@REM Download wrapper jar if missing
@if not exist "%WRAPPER_LAUNCHER%" (
  @if exist "%WRAPPER_PROPERTIES%" (
    for /f "tokens=2 delims==" %%A in ('findstr /R "^wrapperUrl=" "%WRAPPER_PROPERTIES%"') do set WRAPPER_URL=%%A
    if defined WRAPPER_URL (
      echo Downloading Maven Wrapper jar...
      powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_LAUNCHER%' -UseBasicParsing}" 2>nul
    )
  )
)

@if exist "%WRAPPER_LAUNCHER%" (
  java -classpath "%WRAPPER_LAUNCHER%" org.apache.maven.wrapper.MavenWrapperMain %*
  @goto :eof
)

@REM Fallback: delegate to system Maven
where mvn >nul 2>nul
@if %ERRORLEVEL% equ 0 (
  echo [mvnw] Maven Wrapper jar not found; delegating to system Maven.
  call mvn %*
  @goto :eof
)

echo [mvnw] ERROR: Maven Wrapper jar not found and could not be downloaded.
echo [mvnw] System 'mvn' is not on PATH. Install Maven or run from a project with maven-wrapper.jar.
exit /b 1
