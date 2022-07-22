# This project code was written by Ben Levi ID:318811304 and Eliran Eiluz ID:313268146
# The repository contains the android project, but also the Asp.NET server and the react source code, as we made some changes in them to work also with android client. For more information about the server side and the React client side, Please read the code documantation or read the README of the previous projects. 
# If you wish to use the React client side, please note that the ReactRepo doesn't contain the "node_modules" folder. So, after cloning the project, please open cmd and intsall node_modules by the command "npm install".
# Note that the server is using local database. so, before using the application, please run the command "Update-Database" in Visual Studio package manager after cloning the server.
# The android applicaton is using local database via room, with two tables: chats and messages.
This repository represents the third part out of four in Advanced Project 2 Course. In previous parts, we built the client side with react and the server side. Now, 
we add another option for the client, to work with an Android application.

The application has few activities. The explantion about how the application works is explanied according to each activity.

1. The Main Activity:
  The main activity is the login page. from this page you can pass to the Register Activity if you're not registered(You can pass to the registration activity by clicking on the "register" button), login if you're registered, or pass to the Setting Activity, where you can change theme to dark mode or change your default server(The server IP you wish to login to). When clicking the "login" button, if the username and password fields aren't empty, the details will be sent to the server for validity check.In case of successful login, the server will send to the client a JWT token, and the client will send to the server the application token for Firebase. The user will be trasfered to the Contacts Activity.
  
2. The Registation Activity:
  As explained before, you can pass to the registation activity from the main activity by clicking the "register" button in the main activity. You can go back to the login page by clicking the "login" button in the registartion activity. In the registation activity you can create a new user. You will be required to enter username, password, validate password, display name and an imgae(optional). in the client side we do validity checks like the fields aren't empty, the password is according to the requested regex, the validate password field equals to the password field. In case it's alid, the details will be sent to the server. If the username the user chose isn't taken, the server will send to the client a JWT token, and the client will send to the server the application token for Firebase. The user will be trasfered to the Contacts Activity. Otherwise, if the username is already taken, an error message will appear and the user will be requested to choose new username.

3. The Contacts Activity: 
  After a successful login or registration, the user will pass to the Contacts activity, where he can see all his chats. The list is implemented with ListView.
  when the login or registation activity passes the user to the contacts activity, they pass on the username that is corrently logged in. By this information, the contacts activity creates an instance of ContactsViewModel, which in his turn creates an instace of ContactsRepository. First, the contacts repository LiveData list is filled with chats that stored in the local database, and thats what the user can see. By this time, the repository gets from the server the updated list of chats, and updates the LiveData list. When clicking on one of the chats, we pass to the Chat Activity.
  
  4. The Chat Activity:
    After the user clicked on one of the chats, the user pass on to the chat activity. The contacts activity passed to the chat activity the username of the user who corrently online and the username of the contact. By this information ,the chat activity creates an instance of MessageViewModel, which in his turn creates an instance of MessageRepository. First, the messages LiveData list is filled with messages from the local database, and by this time the repository gets from the server updated list of messages.after a successful GET, the user can see the updated list. If the other contact is now online and they are chating on demand, the user will be able to see new messages immediately beacuse of the Firebase.
    
For more information, please read the code documantation.
