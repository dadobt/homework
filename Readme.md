# RESTful API for a public message board

PoC  RESTful JSON API for a public message board.

Project is build with 
-   Spring Boot 
-   JWT tokens for authentication and authorization
-   In-memory H2 database

##Requirements

- Java 11 ( build and tested with OpenJDK 11 )
- Maven ( build and tested with 3.5.4 )

##Build the application and run the application

Navigate to the root of the application (where pom.xml is) and execute

```shell script
 mvn clean install
``` 

If you want to run the application after the previous command you can run

```shell script
java -jar target/homework-0.0.1-SNAPSHOT.jar
```

or if you navigate again to the root of the project you can start the app with 

```shell script
mvn spring-boot:run
```
### h2 console
H2 console available at `/h2-console` 
Database available at `jdbc:h2:mem:this-will-be-generated-at-runtime`

user:`sa`

pass:` ` (is empty) 

##What the application supports

- Can become a client by registering
- A client can authenticate with valid user and pass on
- A client can view specific message   
- A client can create a message in the service
- A client can modify their own messages
- A client can delete their own messages
- A client can view all messages in the service
- A client can view all messages by specific user

##Usage with cURL

In order to Create a user you should user POST the /create endpoint with user and password.
 
example:
```shell script
curl --location --request POST 'localhost:8080/create' \
--header 'Content-Type: application/json' \
--data-raw '{
	"username":"Trajko",
	"password":"12345"
}'
```  


In order to authenticate with valid user and pass POST on /authenticate
example:
```shell script
curl --location --request POST 'localhost:8080/authenticate' \
--header 'Content-Type: application/json' \
--data-raw '{
	"username":"Trajko",
	"password":"12345"
}'
```

Once you are logged in you can post a message  POST on /message

example:
```shell script
curl --location --request POST 'localhost:8080/message' \
--header 'Authorization: Bearer {{Your_JWT_Token}}' \
--header 'Content-Type: application/json' \
--data-raw '{
	"title":"Reggatta de Blanc",
	"message":"Message In a bottle"
}'
```
You can get specific message by id 

example:
```shell script
curl --location --request GET 'localhost:8080/message/1' \
--header 'Authorization: Bearer {{Your_JWT_Token}}' \
--header 'Content-Type: application/json'
```

You can edit your specific message

example:
```shell script
curl --location --request PATCH 'localhost:8080/message/1' \
--header 'Authorization: Bearer ' \
--header 'Content-Type: application/json' \
--data-raw '{
	"title":" A Night at the Opera",
	"message":"Bohemian Rhapsody"
}'
```

You can get all messages from the service

example:
```shell script
curl --location --request GET 'localhost:8080/message/all' \
--header 'Authorization: Bearer {{Your_JWT_Token}}' \
--header 'Content-Type: application/json'

```
You can get all messages for specific user 

example:
```shell script
curl --location --request GET 'localhost:8080/message/user/Trajko' \
--header 'Authorization: Bearer {{Your_JWT_Token}}' \
--header 'Content-Type: application/json'
```

You can delete your own message specific user 

example:

```shell script
curl --location --request DELETE 'localhost:8080/message/1' \
--header 'Authorization: Bearer {{Your_JWT_Token}}' \
--header 'Content-Type: application/json'
```

##BlackBox Tests with Postman 
Once the up is running you can execute Postman testsuite against it 
The postman test can be found in `Blackbox-PostmanTests`

Steps to run the black box tests:

 1. Import the collection from `Blackbox-PostmanTests/PostmanTests.postman_collection.json`
 2. Import the environment from `Blackbox-PostmanTests/test-local.postman_environment.json`
 3. Run it :)| 
 
## Known Issues

- jwt secret is hardcoded into the code
- logging should be better , monitoring should be added
- more unit and integration test should be added 


