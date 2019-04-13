# OO Design and UML

The high level diagram shows a very abstracted overview of how our product works.

![alt text](HighLevelDiagram.jpg "High Level Diagram")

Client: User’s device that is communicating with the web application with HTTP, sending requests, and receiving smart home data.

Web Application: Our web application is the middle man, this takes the client’s requests, uses JSON commands to interact with the database, computes whatever data the client is requesting and sends back to the client.

MongoDB: NoSQL database that we are using to store our user and device data.

IoT Platform: We send device data to the IoT platform, we do not interact with it further.

# UML Class Diagram

![alt text](ClassDiagram.png "UML Class Diagram")

This diagram shows all of the classes we have made and how they are used together to show how our program works as a whole. We have included all attributes and methods. 

