# Go Train Schedule REST API

This is a REST API for train schedules management. It allows users to retrieve train schedules, either all of them or filtered by line and optional departure time.

## Getting Started

### Prerequisites

To run this application, you need to have the following tools installed on your machine:

- JDK 11 or higher
- Maven 3.6.0 or higher

### Building and Running

1. Clone the repository or download the source code.
2. Open a terminal and navigate to the root folder of the project.
3. Run the command `mvn clean install` to build the project.
4. Run the command `mvn spring-boot:run` to start the application.

The application will start running on port `8080`, and you can access it at `http://localhost:8080/go-train-api/v1/schedule`.

## Endpoints

The API has two endpoints:

### GET `/go-train-api/v1/schedule`

This endpoint retrieves all train schedules with pagination support. By default, it returns the first page with 5 schedules sorted by ID. You can customize the results by providing query parameters:

- `page`: the page number to retrieve (default is `0`).
- `size`: the number of schedules per page (default is `5`).
- `sortBy`: the property to sort by (default is `"id"`).

#### Example Request


### GET `/go-train-api/v1/schedule/{line}`

This endpoint retrieves all train schedules for a given line with optional departure time filtering. If the `departure` query parameter is not provided, it returns all schedules for the given line. 
Otherwise, it returns the schedule that matches both line and departure time, if any.

### GET `/go-train-api/v1/schedule/{line}?departure={time}`


## Authors

- [Ruslan Zinovyev](ruslan.zinovyev@gmail.com)

## TODO

- This project was developed as a sample REST API.
- Proper Exception handling should be added

