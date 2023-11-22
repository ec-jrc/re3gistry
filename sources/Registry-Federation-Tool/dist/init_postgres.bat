@echo off

REM DART The script sets environment variables helpful for PostgreSQL
call "postgres_setenv.bat"

if not "%PGDATA%" == "" goto customPGData
REM set a default folder
set "PGDATA=%~dp0/pgdata"
:customPGData

REM -o [initdb-options]

REM DART init the Postgre database cluster
REM DART check also "--no-locale"
"%~dp0\bin\pg_ctl" -o "--auth=trust --username=postgres --encoding=UTF8" initdb