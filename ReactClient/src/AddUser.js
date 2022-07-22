import './ChatPage.css';
import './SampleChat.css';
import { useState } from 'react';
import {Chat, User} from './index'
import UserSideBox from './UserSideBox';


function AddUser({nowOnline, setChats, setMessages, setContactName, setCurrentChat}) {
  // The state of AddUser is name and the object of the User we want to add.
  const toAdd = useState({newUser : '', displayName: '', server:''});

  async function handleAddClick() {
    if(toAdd.newUser == "") {
      return;
    }
    // Clear the input after push Add.
    document.getElementById("contentMessage").value = "";
    document.getElementById("server").value = "";
    document.getElementById("displayName").value = "";
    // Can't add yourself.
    if(toAdd.newUser === nowOnline.onlineUser.username) {
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "block";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "none";
      }
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "none";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "none";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "none";
      }
    }

    // Server is required.
    else if(toAdd.server === "") {
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "none";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "none";
      }
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "none";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "block";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "none";
      }
    }

    // displayName is required.
    else if(toAdd.displayName === "") {
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "none";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "none";
      }
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "none";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "none";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "block";
      }
    }
    

    // Can't add User which already exist.
    else if(nowOnline.onlineUser.chats.find((e) => e.username === toAdd.newUser)) {
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "none";
      }
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "none";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "block";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "none";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "none";
      }
    }

    /*
    * First, send invitation to the user you wish to add. If the invitation passed successfully, then POST the add to 
    * your local server(The invitation may send to your local server, if the user you wish to add is registered to your server).
    */
    else {
      var flag = 0;

      // the POST request to the server, which contains in the header the user JWT token.
      // the request body contains the desired object by the API.(from ,to, server JSON).
      const request1 = {
        method: 'POST',
        headers:{'Content-Type': 'application/json' ,'Authorization': 'Bearer '+ nowOnline.userToken},

        // need to send in server field my server.
        body:JSON.stringify({from:nowOnline.onlineUser.username, to:toAdd.newUser, server:"http://127.0.0.1:5010"})
      };
      await fetch(toAdd.server + '/api/invitations', request1)
      .then(async response => {
       if(response.status == 201) {
         flag = 1;
       }
      });


    /*
    * After a successful invite, add the user to your server and save it locally.
    */
      if (flag == 1) {
        const request2 = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + nowOnline.userToken },
          body:JSON.stringify({id:toAdd.newUser, name:toAdd.displayName, server:toAdd.server})
        };
        await fetch('http://localhost:5010/api/contacts', request2)
          .then(async response => {
            if (response.status == 201)  {
              successAdd();
              var chat = new Chat(toAdd.newUser, toAdd.displayName, "im3.jpg", "", "")
              chat.server = toAdd.server
              //toAdd.objectUser = new User(res.username, res.password, res.displayName, res.image); // res.chats.
              nowOnline.onlineUser.chats.push(chat);
            } else {
              userNotExist();
            }
          });

        // Rerender my chats by setState method.
        setChats(
          nowOnline.onlineUser.chats.map((chat, key) => {
            return <UserSideBox nowOnline={nowOnline} displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} setMessages={setMessages} setContactName={setContactName} setCurrentChat={setCurrentChat} key={key}/>
          })
        );
      }

      }
    }
  
    // Success comment.
    function successAdd() {
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "block";
      }
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "none";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "none";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "none";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "none";
      }
    }

    // notFound comment.
    function userNotExist() {
      if(document.getElementById("addYourself")) {
        document.getElementById("addYourself").style.display = "none";
      }
      if(document.getElementById("notFound")) {
        document.getElementById("notFound").style.display = "block";
      }
      if(document.getElementById("sucsessAdd")) {
        document.getElementById("sucsessAdd").style.display = "none";
      }
      if(document.getElementById("exists")) {
        document.getElementById("exists").style.display = "none";
      }
      if(document.getElementById("noServer")) {
        document.getElementById("noServer").style.display = "none";
      }
      if(document.getElementById("noDisplayName")) {
        document.getElementById("noDisplayName").style.display = "none";
      }
    }

    // Clear values when close modal.
  function handleCloseClick() {
    // Clear the input after push close.
    document.getElementById("contentMessage").value = "";
    document.getElementById("server").value = "";
    document.getElementById("displayName").value = "";

    // Clear the errors after push close.
    if(document.getElementById("addYourself")) {
      document.getElementById("addYourself").style.display = "none";
    }
    if(document.getElementById("notFound")) {
      document.getElementById("notFound").style.display = "none";
    }
    if(document.getElementById("sucsessAdd")) {
      document.getElementById("sucsessAdd").style.display = "none";
    }
    if(document.getElementById("exists")) {
      document.getElementById("exists").style.display = "none";
    }
    if(document.getElementById("noServer")) {
      document.getElementById("noServer").style.display = "none";
    }
    if(document.getElementById("noDisplayName")) {
      document.getElementById("noDisplayName").style.display = "none";
    }
  }

  // Save the nameUser typed into newUser.
  function handleUserNameChange(e) {
    const {value} = e.target;
    toAdd.newUser = value;
  }

    // Save the nameUser typed into newUser.
    function handleServerChange(e) {
      const {value} = e.target;
      toAdd.server = value;
    }

    // Save the displayName typed into newUser.
    function handleDisplayNameChange(e) {
      const {value} = e.target;
      toAdd.displayName = value;
    }

    // Define listener to close modal event.
  function handleCloseAddModal() {
    document.getElementById('contactModal').addEventListener('hidden.bs.modal', handleCloseClick, {once:true})
  }


  return (
    <>
    <button type="button" id="addUser" data-bs-toggle="modal" data-bs-target="#contactModal" onClick={handleCloseAddModal}>
    <svg xmlns="http://www.w3.org/2000/svg" width={16} height={16} fill="currentColor" className="bi bi-person-plus-fill">
      <path d="M1 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6z" />
      <path fillRule="evenodd" d="M13.5 5a.5.5 0 0 1 .5.5V7h1.5a.5.5 0 0 1 0 1H14v1.5a.5.5 0 0 1-1 0V8h-1.5a.5.5 0 0 1 0-1H13V5.5a.5.5 0 0 1 .5-.5z" />
    </svg>
    </button>

    <div className="modal fade" id="contactModal" tabIndex="-1" aria-labelledby="ModalLabel" aria-hidden="true">
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">

          <div className="modal-header">
            <h5 className="modal-title" id="ModalLabel">Add new contact</h5>
            <button type="button" className="btn-close" data-bs-dismiss="modal" onClick={handleCloseClick} aria-label="Close"></button>
          </div>

          <div className="modal-body">
            <div className="alert alert-danger alert-dismissible fade show" id="notFound" role="alert">
              <strong>The user not found.</strong>
            </div>
            <div className="alert alert-danger alert-dismissible fade show" id="addYourself" role="alert">
              <strong>You can't chat with yourself.</strong>
            </div>
            <div className="alert alert-success alert-dismissible fade show" id="sucsessAdd" role="alert">
              <strong>User added succefully.</strong>
            </div>
            <div className="alert alert-warning alert-dismissible fade show" id="exists" role="alert">
              <strong>You already chat with this user.</strong>
            </div>
            <div className="alert alert-warning alert-dismissible fade show" id="noServer" role="alert">
              <strong>The server field can't be empty.</strong>
            </div>
            <div className="alert alert-warning alert-dismissible fade show" id="noDisplayName" role="alert">
              <strong>The display name field can't be empty.</strong>
            </div>
            
          <input type="text" id="contentMessage" className="form-control" placeholder="Contact's identifier" onChange={handleUserNameChange} autoComplete="off"></input>
          <input type="text" id="displayName" className="form-control" placeholder="Contact's display name" onChange={handleDisplayNameChange} autoComplete="off"></input>
          <input type="text" id="server" className="form-control" placeholder="Contact's server" onChange={handleServerChange} autoComplete="off"></input>
          </div>

          <div className="modal-footer">
            <button type="button" id="addBtn" className="btn btn-primary" onClick={handleAddClick}>Add</button>
          </div>

        </div>
      </div>
    </div>
    </>




    
  );
}

export default AddUser;

