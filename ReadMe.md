# Doordash Assessment
# Candidate name: Jie Wen

It is a maven project.

Please install:
Maven, Redis, MySQL DBMS, JAVA SDK 1.8.

Go to MySQL client, run the SQL statement in /resources/create_table.sql

Update the Redis and MySQL (username, password, address and port) information in /resources/application.yaml

Start up Redis and MySQL server.

Change the process method with db or redis in /resources/application.yaml, "process.method" to db or redis.

I implemented two ways for data persisted with MySQL and Redis. 

I asked for the requirements from Doordash:
J Wen <wanhohiuying@gmail.com>
to engineering-assessment

Dear sir/madam,

Hello. I am writing to ask for some clarifications of the assessment requirements.

1. Is the API input always with the format "(phone_type) phone_number", do we need to validate the format and if so, what error handling do we need.
2. As I am required to ensure API response time is less than 100ms, may I ask if there are any limitations of the size of the input data or we have to ensure all the response time should be less than 100ms no matter how large the input data is?

Best regards,
Jie Wen

And got the following reply:
Ben Katz <ben.katz@doordash.com>
to me, engineering-assessment

1. Yes, the format should generally match what is described in the doc. You should be able to parse types and phone numbers from the input - for any issues parsing the input, go ahead and return validation errors. Validation error handling is up to you as long as it makes sense, feel free to document your assumptions.
2. Good point about the input potentially being large. Feel free to make some reasonable assumptions about what the input size should be and validate against those assumptions. In general, let's assume the input is small for now.

Then I made my assumptions based on the reply,
1. Will validate the input to see if it is null or empty, and also check if the phone number format is valid, if not valid, will return 500 error.
2. Based on the reply, I will assume the input size is small. Tested both methods with Postman, both are good as Redis query is fast and for DB method, I group all the number from input to search in one query. 
Tested the input with 20 distinct numbers and lots of duplicate numbers, the API response time for both methods are less than 100ms. One point I want to emphasise is that the test results are calculated based on the average response time in multiple attempts on localhost. And the API response time is also dependent on other factors like server machine power, network capacity and Redis/Database power...

Go to project root folder, run mvn install clean. Then run spring-boot:run to start up the application.
Example: POST http://localhost:8080/phone-numbers with request payload in body: 

    { "raw_phone_numbers": "(Home) 415-415-4155 (Cell) 415-123-4567" }

Will get the result like:

    {"results":
    [{
        "id": "f66388f9-d139-44b7-8aba-51b8b70be0ac",
        "phone_number": "4154154155",
        "phone_type": "home",
        "occurrences": 30849
    },
    {
        "id": "d0bf4ce7-ff24-4c9e-9056-68baaffacb3d",
        "phone_number": "4151234567",
        "phone_type": "cell",
        "occurrences": 31392
    },
    {
        "id": "f9f6d22a-698d-4d6f-a582-4a69be1db292",
        "phone_number": "4154154154",
        "phone_type": "home",
        "occurrences": 202
    },
    {
        "id": "cc6bd9d9-91e5-4620-96fb-6a1616b1327a",
        "phone_number": "41541541551",
        "phone_type": "home",
        "occurrences": 17
    },
    {
        "id": "7b21d075-f114-48da-8a76-a2e3628ca424",
        "phone_number": "41512345627",
        "phone_type": "cell",
        "occurrences": 17
    },
    {
        "id": "f274270b-4f01-43cc-8ef6-308c83841144",
        "phone_number": "4154154152",
        "phone_type": "home",
        "occurrences": 198
    },
    {
        "id": "849eb6e0-b74f-49e3-81a3-cc4537daab1f",
        "phone_number": "41544164155",
        "phone_type": "home",
        "occurrences": 17
    },
    {
        "id": "e8b434df-e109-46bd-a43d-9fa8b11a8b20",
        "phone_number": "415123455367",
        "phone_type": "cell",
        "occurrences": 198
    },
    {
        "id": "c271b19b-2eb8-4027-a48e-8a61a870e0a3",
        "phone_number": "4154142354155",
        "phone_type": "home",
        "occurrences": 198
    }
]}