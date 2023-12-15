@echo off

REM DART The script sets environment variables helpful for PostgreSQL
call "postgres_setenv.bat"

if not "%PGDATA%" == "" goto customPGData
set "PGDATA=%~dp0/pgdata"
:customPGData

"%~dp0\bin\psql" -U postgres -d postgres -f "E:\Dev\Software\pgsql-10.23\ror_pre_restore.sql"
"%~dp0\bin\psql" -U postgres -d ror -f "E:\Dev\Software\pgsql-10.23\ror_20200222_full.sql"