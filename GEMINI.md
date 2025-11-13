# Project Overview

This project is a Java-based web application that implements a user market or online marketplace. Users can register, log in, and browse products. The application features product listing, search functionality, and a "My Page" for user-specific information.

## Technologies

*   **Backend:** Java, Servlets, JDBC
*   **Frontend:** JSP, HTML, CSS, JavaScript
*   **Database:** MySQL (inferred from the `mysql-connector-j` dependency)
*   **Build:** The project appears to be built and managed through an IDE like Eclipse or IntelliJ, given the `.project` and `.classpath` files.

## Architecture

The application follows a Model-View-Controller (MVC) like pattern, with:

*   **Model:** The `model` package contains POJOs (Plain Old Java Objects) like `User.java` and `Product.java` that represent the application's data.
*   **View:** The `webapp` directory contains JSP files for rendering the user interface.
*   **Controller:** The `web` package contains Servlets that handle user requests, interact with the DAO layer, and forward to the appropriate JSP for rendering.
*   **DAO (Data Access Object):** The `dao` package is responsible for all database interactions, using raw JDBC to execute SQL queries.

# Building and Running

There are no explicit build scripts like `pom.xml` or `build.gradle`. The project is likely built and run from within an IDE (like Eclipse or IntelliJ) that is configured to work with a web server like Apache Tomcat.

**To run the project (inferred):**

1.  Set up a MySQL database and execute the SQL scripts found in the project (e.g., `D:\ryu\workspace\userMarket\src\main\webapp\user\sql\user.sql`).
2.  Configure the database connection in `dao.DBUtil.java`.
3.  Deploy the application to a web server like Apache Tomcat.
4.  Access the application in your web browser, starting with the registration page.

# Development Conventions

*   **Database Access:** Database operations are performed using raw JDBC in the `dao` package.
*   **SQL Queries:** SQL queries are embedded as strings within the Java code.
*   **Dependencies:** JAR dependencies are manually managed in the `src/main/webapp/WEB-INF/lib` directory.
*   **Coding Style:** The code follows standard Java conventions. Comments are written in Korean.
