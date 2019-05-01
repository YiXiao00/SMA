## Unit Test Description

Project Name: Smart Home Application

Testing Tools: JUnit with Spring Boot, MockMVC

Document Date: 30/4/2019

## Testing Plan Scope

### User-Access Tests

|Function|Description|
|---|---|
|signUpUser|Sign up a user to Smart Home Server|
|signUpDuplicateUser|Input username has been ocupied by other user while signing up|
|signInEmptyUser|Try signing in using a user which never exist|
|signInWrongPassword|Sign in with incorrect password|
|signInCorrectUser|Sign in using the info regestered previously|
|checkValidSessionId|verify the sessionId received from the server, which will be required in nearly all apis|
|signOut|Log out the currect user|
|innerCleanUser|Remove the user's record after this test|

### Smart Device Tests

|Function|Description|
|---|---|
|addDevice|Register a smart home device to the current user|
|getDeviceId|Check the device's Id is not empty and return all accessible info|
|toggleDevice|Toggle the current device|
|checkDeviceStatus|Return updated status info about current device|
|deviceChangeType|Patch the smart home device's type|
|checkDeviceType|Return updated type info about current device|
|removeDeviceOfUser|Delete the smart home device from the user|
|checkDeviceList|Return the list containing all linked devices of a user|
|deviceOfMultipleUser|Check the correctness while multiple users are operating devices at the same time. Check the correspondence of sessionId and deviceId|
|fiwareInfo|Return the smart home monitoring info|
|innerClean_UserDevice|Remove all devices and users after this test|

### Task System Tests

|Function|Description|
|---|---|
|addTask1_instant|Register scheduled task without duration arg|
|viewTask1_instant|Return the current scheduled task list|
|invokeTask1_instance|Turn on the scheduling system and check the task status|
|addTask1_duration|Register scheduled task with duration arg|
|invokeTask1_duration|Turn on the scheduling system and add shut-down task automatically|
|deleteTask1|Delete one scheduled task which has not been invoked|
|addTask1Failure|Add a scheduled task with invalid arguments|
|multipleTask1|Add scheduled tasks and check the sorting|
|addTask2|Register monitored task for current fiware|
|viewTask2|Return the current monitored task list|
|changeTask2|Patch one specific monitored task|
|verifyChangedTask2|Return the changed monitored task|
|deleteTask2|Delete one monitored task|
|verifyDeletedTask2|Return the list of monitored tasks after deletion|
|innerClean_UserDeviceAllTasks|Remove all tasks, devices and users after this test|

## Test Cases and Results

**Passed: 34/34**

**Coverage:**£¨configurations, maintenance and private functions included£©<br>Classes: 83%<br>Method: 66%<br>Line: 59%

### User-Access Tests

|Function|Input Value|Expected|Test Result|
|---|---|---|---|
|signUpUser|name = "test_default_username"<br>pwd = "test_default_password"|"Signed up successfully."|Passed|
|signUpDuplicateUser|name = "test_default_username"<br>pwd = "test_default_password"|"The username has been used by another user."|Passed|
|signInEmptyUser|name = "test_empty_username"<br>pwd = "test_empty_password"|"failed"|Passed|
|signInWrongPassword|name = "test_default_username"<br>pwd = "test_wrong_password"|"failed"|Passed|
|signInCorrectUser|name = "test_default_username"<br>pwd = "test_default_password"|"succeeded"<br>Response.Cookie["sessionId"].Length > 0|Passed|
|checkValidSessionId|sessionId|sessionId != "wrong_sessionId"|Passed|
|signOut|token = sessionId|"succeeded"|Passed|
|innerCleanUser||200/OK|Passed|

### Smart Device Tests

|Function|Input Value|Expected|Test Result|
|---|---|---|---|
|addDevice|token = userSessionId<br>type = "test_default_device_type"|200/OK|Passed|
|getDeviceId|token = userSessionId|Response.String["deviceId"].Length > 0|Passed|
|toggleDevice|token = userSessionId<br>device = defaultDeviceId|"Device toggled"|Passed|
|checkDeviceStatus|token = userSessionId<br>device = defaultDeviceId|Response.String["poweredOn"] == "true"|Passed|
|deviceChangeType|token = userSessionId<br>device = defaultDeviceId<br>input = "test_default_device_type2"|"finished"|Passed|
|checkDeviceType|token = userSessionId<br>device = defaultDeviceId|Response.String["type"] == "test_default_device_type2"|Passed|
|removeDeviceOfUser|token = userSessionId<br>device = defaultDeviceId|"Device deleted"|Passed|
|checkDeviceList|token = userSessionId|Response.String.Length <= 10|Passed|
|deviceOfMultipleUser|token = userSessionId<br>device = deviceOfUser2Id|"not match"|Passed|
|fiwareInfo||200/OK|Passed|
|innerClean_UserDevice||200/OK|Passed|

### Task System Tests

|Function|Input Value|Expected|Test Result|
|---|---|---|---|
|addTask1_instant|token = userSessionId<br>device = defaultDeviceId<br>type = "test_default_task1_type"<br>in = "0"<br>duration = "0"|"Task added"|Passed|
|viewTask1_instant|token = userSessionId<br>device = defaultDeviceId|Response.String["taskId"].Length > 0<br>Response.String["poweredOn"] == "false"|Passed|
|invokeTask1_instance|token = userSessionId<br>device = defaultDeviceId<br>task = task1Id|"not found"<br>Response.String["poweredOn"] == "true"|Passed|
|addTask1_duration|token = userSessionId<br>device = defaultDeviceId<br>type = "test_default_task1_type"<br>in = "0"<br>duration = "5"|"Task added"<br>Response.String["taskId"].Length > 0|Passed|
|invokeTask1_duration|token = userSessionId<br>device = defaultDeviceId<br>task = task1Id|"not found"<br>Response.String["taskId"].Length > 0 && Response.String["taskId"] != task1Id<br>Response.String["poweredOn"] == "true"|Passed|
|deleteTask1|token = userSessionId<br>task = task1Id|"finished"|Passed|
|addTask1Failure|token = user2SessionId<br>device = defaultDeviceId<br>type = "test_default_task1_type"<br>in = "0"<br>duration = "0"|"device not belongs to the user"|Passed|
|multipleTask1|**Params1:**<br>token = userSessionId<br>device = defaultDeviceId<br>type = "test_default_task1_type_late"<br>in = "5"<br>duration = "0"<br>**Params2:**<br>token = userSessionId<br>device = defaultDeviceId<br>type = "test_default_task1_type_early"<br>in = "0"<br>duration = "0"|Response.String["type"] == "test_default_task1_type_late"|Passed|
|addTask2|token = userSessionId<br>device = defaultDeviceId<br>type = "Toggle"<br>condition = "[Humidity,>,0.35]"|"task2 added"|Passed|
|viewTask2|token = userSessionId<br>device = defaultDeviceId|Response.String["taskId"].Length > 5|Passed|
|changeTask2|token = userSessionId<br>device = defaultDeviceId<br>taskId = task2Id<br>type = "TurnOn"<br>condition = "[Humidity,>,0.4]"|"finished"|Passed|
|verifyChangedTask2|token = userSessionId<br>device = defaultDeviceId|Response.String["type"] == "TurnOn" && Response.String["trigger"] == "[Humidity,>,0.4]"|Passed|
|deleteTask2|token = userSessionId<br>device = defaultDeviceId<br>taskId = task2Id|"finished"|Passed|
|verifyDeletedTask2|token = userSessionId<br>device = defaultDeviceId|Response.String.Length <= 10|Passed|
|innerClean_UserDeviceAllTasks||200/OK|Passed|


## Additional Documents

*The external apis of "fiware-service" are not included in this testing.*