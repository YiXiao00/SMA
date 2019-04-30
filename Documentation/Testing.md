## Unit Test Description

Project Name: Smart Home Application

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
|addTask1_instant||
|viewTask1_instant||
|invokeTask1_instance||
|addTask1_duration||
|invokeTask1_duration||
|deleteTask1||
|addTask1Failure||
|multipleTask1||
|addTask2||
|viewTask2||
|changeTask2||
|verifyChangedTask2||
|deleteTask2||
|verifyDeletedTask2||
|innerClean_UserDeviceAllTasks||

## Test Cases and Results


## Additional Documents

