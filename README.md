# Movie Recommendation App

## Overview
This is a Java + MySQL Movie Recommendation project with both Console and GUI versions.
Features:
- Insert new movies
- Delete movies by ID
- Flexible search across title, genre, language, director
- Fetch and display all movies
- Uses JDBC and MySQL database

## Setup Instructions

### 1. Setup MySQL Database
- Install MySQL server if not already installed.
- Run the SQL script file `sql/setup_database.sql` in your MySQL environment.
  This will create a database named `moviedb` and a `movies` table with sample data.

### 2. Download MySQL JDBC Driver
- Download the MySQL Connector/J driver from:
  https://dev.mysql.com/downloads/connector/j/
- Place the downloaded JAR in the `lib` folder.

### 3. Compile and Run Java Programs

- Compile with MySQL connector in classpath. Example:
  ```
  javac -cp .:lib/mysql-connector-java-x.x.xx.jar src/*.java
  ```

- Run Console App:
  ```
  java -cp .:lib/mysql-connector-java-x.x.xx.jar src.MovieRecommendationConsole
  ```

- Run GUI App:
  ```
  java -cp .:lib/mysql-connector-java-x.x.xx.jar src.MovieRecommendationGUI
  ```

## Features

- Console app and GUI app have options for:
  - Insert new movie
  - Delete movie by ID
  - Search movies by any term (title, genre, language, director)
  - View all movies

## Notes
- Adjust the DB connection parameters in source code if needed.
