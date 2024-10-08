@echo off

REM DART The script sets environment variables helpful for PostgreSQL
call "postgres_setenv.bat"

if not "%PGDATA%" == "" goto customPGData
set "PGDATA=%~dp0/pgdata"
:customPGData

REM DART start the server
REM "%~dp0\bin\pg_ctl" -D "%~dp0/pgdata" -l logfile start
"%~dp0\bin\pg_ctl" status

REM pause
timeout /t 3