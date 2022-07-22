import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import LoginPage from './LoginPage';
import RegisterPage from './RegisterPage';
import ChatPage from './ChatPage';
import Contact from './Contact';
import { BrowserRouter,Routes, Route } from 'react-router-dom';


class User {
  // chats default to [].
  constructor(username, password, displayName, picture) {
    this.username = username;
    this.displayName = displayName;
    this.password = password;
    this.picture = picture;
    this.chats = [];
  }
}

class MessageClass {
  constructor(from, messageContent, messageType, messageDate ,senderPicture) {
    this.from = from;
    this.messageContent = messageContent;
    this.messageType = messageType;
    this.messageDate = messageDate;
    this.senderPicture = senderPicture;
  }
}

class Chat {
  constructor(username, displayName, image, lastMessage, date) {
    this.username = username
    this.displayName = displayName;
    this.image = image;
    this.messages = [];
    this.lastMessage = lastMessage;
    this.date = date;
    this.server = "";
  }
}


const nowOnline = {onlineUser:null, userToken:null, signalR:null};


ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
    <Routes>
      <Route exact path='/contact' element={<Contact nowOnline={nowOnline} />}></Route>
      <Route exact path='/chat' element={<ChatPage nowOnline={nowOnline} />}></Route>
      <Route exact path='/register' element={<RegisterPage nowOnline={nowOnline}/>}></Route>
      <Route path='/' element={<LoginPage nowOnline={nowOnline}/>}></Route>
    </Routes>
    </BrowserRouter>
  </React.StrictMode>
,
  document.getElementById('root')
);

export  {User, Chat, MessageClass};


