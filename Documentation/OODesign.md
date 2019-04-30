# OO Design and UML

The high level diagram shows a very abstracted overview of how our product works.

![alt text](HighLevelDiagram.jpg "High Level Diagram")

Client: User’s device that is communicating with the web application with HTTP, sending requests, and receiving smart home data.

Web Application: Our web application is the middle man, this takes the client’s requests, uses JSON commands to interact with the database, computes whatever data the client is requesting and sends back to the client.

MongoDB: NoSQL database that we are using to store our user and device data.

IoT Platform: We send device data to the IoT platform, we do not interact with it further.

# UML Class Diagram

![alt text](ClassDiagram.png "UML Class Diagram")

This diagram shows all of the classes we have made and how they are used together to show how our program works as a whole. We have included all attributes and methods. As you can see, the program works by 3 main controllers, FiWareController, MainController and SignInController. FiWareController uses DeviceService, FiWareService, TaskService and UserService as all of their methods and attributes are needed to control the FiWare. MainController uses TaskService, UserService and DeviceService, as those 3 are the ones needed to display the necessary information to the user, for example which tasks they have scheduled, or to view their user/device data. SignInController only requires UserService, as it only needs the user's username, password and session data. Each of these services draw from the base classes: User, Device, Task, Task2, FiWareInfo, each with their respective repository. These base classes are instantiated and represent what their name is. For example, Task stores task data relevant to one specific task; ID, duration, calendar etc.

# Use Case Diagram

![alt text](UseCaseDiagram.png "Use Case Diagram")

This diagram shows how the user interacts with our platform, and which parts of our platform handle each operation. As you can see, the user interacts with the front end, which in turn sends commands to our middleware which interacts with the IoT platform and the database, to provide the relevant data back to the user, and/or store or send data.
