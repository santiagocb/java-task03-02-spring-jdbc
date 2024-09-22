# Java Spring JDBC
The target of this exercise is to practice Spring JDBC with Java 17.

## Features
- Create Postgres tables named randomly according to file configuration (column number and types).
- Populate each table with configured number of rows.
- Usage of Properties to read from File the database settings.

## Requirements
- Install Docker
- Download Postgres Docker image
- Install psql client

## Run the project
1. Run postgres through Docker with following command: `docker run --name lil-postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres`
2. Run psql command to create DB: `psql -h localhost -U postgres -f database.sql` and enter the password: `password`
3. Run Main program with properly arg parameter. In this case: `java Main DatabaseConfig`
4. Change parameters in DatabaseConfig file as needed

## Output

![Screenshot 2024-09-21 at 10 31 43 PM](https://github.com/user-attachments/assets/cc0bc840-7a1b-4dc1-9e8a-ed42bed27bb5)
