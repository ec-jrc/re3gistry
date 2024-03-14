@echo off

REM DART The script sets environment variables helpful for PostgreSQL
call "postgres_setenv.bat"

if not "%PGDATA%" == "" goto customPGData
set "PGDATA=%~dp0/pgdata"
:customPGData

REM DART stop the server
echo Stopping PostgreSQL service
echo.
REM "%~dp0\bin\pg_ctl" -D "%~dp0/pgdata" stop
"%~dp0\bin\pg_ctl" stop