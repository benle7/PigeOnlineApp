import './ChatPage.css';
import './SampleChat.css';
import ChatMenu from './ChatMenu';
import UserSideBox from './UserSideBox';
import { useState, useEffect } from 'react';
import {Chat} from '.'

 function ChatPage({nowOnline}) {

  /* 
   * when navigating to the ChatPage(happends after a successful login or registration), pull all the chat of this certain user from the server
   */
  async function getChats() {

    // the GET request to the server, which contains in the header the user JWT token. 
    const request = {
      method: 'GET',
      headers:{'Content-Type': 'application/json' ,'Authorization': 'Bearer '+ nowOnline.userToken},
      
    };

    // fetch the chats from server.
    await fetch('http://localhost:5010/api/contacts', request)
    .then(async response => {
     if(response.statusText == "Not Found") {
       return null;
     }
     else {

      // in case of a successful fetch, return the response in json.
       return response.json()
     }
    }).then(res => {
    if(res != null) {
     
      // if the user has any chat(res in not null), iterate the chats pulled and save them localy.
      for(var i = 0; i < res.length; i++) {
        var chat = new Chat(res[i].chatWith, res[i].displayName, "im3.jpg", res[i].lastMessage, res[i].date)
        chat.server = res[i].serverURL;
        nowOnline.onlineUser.chats.push(chat)
      }

      // re-render the page after adding the chats.
      setChats(nowOnline.onlineUser.chats.map((chat, key) => {
        return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} nowOnline={nowOnline} setContact={null} setMessages={null} key={key}/>}));
      
    }
  });
  }

  // establish connection with the server by the signalR web socket.
  function establishSignalR() {
    nowOnline.signalR = ({signalrObject:require('@microsoft/signalr'), signalrSocket:null})
    nowOnline.signalR.signalrSocket = new nowOnline.signalR.signalrObject.HubConnectionBuilder().withUrl("http://localhost:5010/PigeOnlineHub").build();
    nowOnline.signalR.signalrSocket.start().then(() => {nowOnline.signalR.signalrSocket.invoke("DeclareOnline", nowOnline.onlineUser.username)});
    
  }

  

  // get the chats and establish connection only on the first time the page renders.
  useEffect(()=>{getChats(); establishSignalR();},[])

  // The state of ChatPage is chats - the side list of current user's chats.
  const [chats, setChats] = useState(nowOnline.onlineUser.chats.map((chat, key) => {
    return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} nowOnline={nowOnline} setContact={null} setMessages={null} key={key}/>}));
  
    return (
    <>
      <div className="row" id="Bar">
        <div className="col"><img src="im4.png" id="leftLogo" alt="left-logo" /></div>
        <div className="col logoWrap"><img src="logo.png" id="webLogo" alt="right-logo" /></div>
      </div>

      <div className="messaging">
        <div className="inbox_msg">
          <ChatMenu nowOnline={nowOnline} chats={chats} setChats={setChats} setMessages={null}/>
        </div>
      </div>
    </>
    
  );
}

export default ChatPage;

