@echo off

REM DART custom environment variables
SET PATH="%~dp0\bin";%PATH%
REM @SET PGDATA="%~dp0\pgdata"
SET PGDATA=%~dp0\pgdata

SET PGDATABASE=postgres
SET PGUSER=postgres

REM SET PORT=5432
SET PGPORT=5439

SET PGLOCALEDIR="%~dp0\share\locale"
REM @SET PGLIB="%~dp0\lib"

SET ENCODING="UTF8"