# This project code was written by Ben Levi ID:318811304 and Eliran Eiluz ID:313268146
# The PigeOnlineWebAPI and pigeOnline projects requires from you to create database. Please build database by writing "Add-Migration #ANY_NAME" and then "Update-Database" in the Package Manager Console on visual studio.DO IT FOR EACH OF THE SERVERS! The database that will be created can be found on: SQL Server Object Explorer->ProjectModels->DatabaseName.
# The ReactRepo doesn't contains the "node_modules" folder. SO, after pulling the project, please open cmd and intsall node modules by the command "npm install".
# Please run pigeOnline, PigeOnlineWebAPI(with Visual Studio) and ReactRepo(npm start) before using the code.
# The PigeOnlineWebAPI uses JWT for Authentication and Authorization.
# The chat web-application works with SignalR.

This repository represents the second part out of four in Advanced Project 2 Course. In the first part, we built only the client side with react. Now, we're involving 
Two servers, when one of them will work as a ASP.NET Web-API server which will be used for the chat web application(validate login and registration, get chats and messages and so on), and the second one is a ASP.NET-MVC server that is used to hold the rating platform of the chat application.

Now, after binding the client side with the server side, The chat web application works by the following method:

1. Registration:
   During the fill of the registation form, majority of the validity checks will be made in the client side(required fields aren't empty, the password follows the    requested regex and so on). when all the fields were filled correctly, the user will be able to click the "Register" button and the form will be sent to the 
   Web-API server.The only check that the Web-API server does(because all the other requested checks were made in the client side) is to check that the required username isn't already in use. In case it is, The server will return conflict and the user will be able to see an error message that says that the username he chose already in use. In case it isn't in use, the new user will be added to datebase, The server will generate for him a JWT token, and the user will be passed to the chat page, where he can add new chats. no fetch of chats will be made, as it's a new user and he has no chats.
   
2. Login:
  As in the registation, some of the validity checks will be made in the client side(fields aren't empty and so on). After those checks passed, the user will be able to click the "Login" button, and the form will be passed to the Web-API server. The server will check that the username exists and the password entered is correct.In case it is, the sever will generate a JWT token for this user, and the user will be passed to his chat page, where he will be able to see all his chats and messages.
  When passing to the chat page, a fetch from the web-api server will be made of all the chats this user has. messages with a specific user will be fetched if and only if the user clicked on this chat, and the .
  
***After login/registretion - all GET/POST request will be valid if and only if they conatin in the header the user's JWT Token!***   
  
3. Adding new chat:
   When a user wishes to add a new chat with a user, he needs to fill 3 fields:
   1. Username of this user
   2. The name he wants to see for this user on the chat menu(display name).
   3. The server that the user he wishes to chat with is registered to.
 After filling those fileds, an "Invitation"(as requested in the API building instructions) sent to the server that the user filled when trying to add the new user(This server might be the same server as the sender user, if they both are registered to the same server). If the server responded with "Ok", than we add this chat to our server and than locally on client's browser.
 
 4. Sending new message:
   When sending a new message, we send a "transfer" request to the other user's server(might be the same server as ours - if this user is registered to our server).
   If the server answers with "Ok" to this transfer request, then we notify our server with the new message and add it locally to the client. The message sending
   and chat adding works by this method to avoid show a chat or a message that did not accept in the other side.
  
 5. SignalR:
   When moving to the chat page(shortly after login or registartion), a SignalR web-socket is opened between the client and the server.
   This web socket is listening to "MessageRecived" event, so if we get a new message from a user that registered to the same server as ours, we will get it immediately. In the same way, when wer'e sending a new message, we invoke the "MessageSent" method, which tells the server to trigger the "MessageRecived" event in the other side, so the other user will be able to see the new message immediately.
   
6. Rating:
   As described above, we created an MVC-Based server that handles the rating page of the chat web-application.You can get to the rating site by clicking the yellow
   star appears at the middle of the login page of the chat web-appplication. In the chat page, you will be able to add a new review, edit existing reviews, delete reviews and view them. The Rating page contains the ability to serach for specific reviews. the serach is async, so the whole page won't refresh.

  
For more information about the project, please read the code documentation in the relavent places.
Enjoy!
