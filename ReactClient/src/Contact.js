import './ChatPage.css';
import './SampleChat.css';
import UserSideBox from './UserSideBox';
import { useState, useEffect } from 'react';
import ChatMenu from './ChatMenu';
import ChatWindow from './ChatWindow';
import { useLocation,Link } from 'react-router-dom';
import Message from './Message';
import { MessageClass } from '.'


function Contact({nowOnline})  {

  /*
   * This method is used to pull all the messages from the server, when the user clicks a chat.
   */
  async function getMessages() {

    // The GET request, which contains in the header the JWT token.
    const request = {
      method: 'GET',
      headers:{'Content-Type': 'application/json' ,'Authorization': 'Bearer '+ nowOnline.userToken},
      
    };

    // fetch from server, and in case 
    await fetch('http://localhost:5010/api/contacts/' + contactUserName + '/messages', request)
    .then(async response => {
       return response.json()
    }).then(res => {
    if(res != null) {
      // This loop need move to Login.
      for(var i = 0; i < res.length; i++) {
       currentChat.messages.push(new MessageClass(res[i].from, res[i].content, res[i].type, res[i].date, res[i].senderPicture))
      }
      if(res.length > 0) {
        setChatMessages(currentChat.messages.map((message, key) => {
          return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
      }
    }
  });
  }
  

  // Take all messages of the current chat.
  useEffect(()=>{getMessages();},[])

  
  // Take the parameter from Link calling.
  const params = useLocation();
  // The current contact we push it.
  const [contactUserName, setContactUserName] = useState(params.state.user)


  // The chat with the contact.
  const [currentChat, setCurrentChat] = useState(nowOnline.onlineUser.chats.find((e) => e.username === contactUserName))
  // Chat messages with the contact.
  const [chatMessages, setChatMessages] = useState(currentChat.messages.map((message, key) => {
    return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
  // The side list of current user's chats.
  const [chats, setChats] = useState(nowOnline.onlineUser.chats.map((chat, key) => {
    return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} setMessages={setChatMessages} setContactName={setContactUserName} setCurrentChat={setCurrentChat} nowOnline={nowOnline} key={key}/>}));
  
    return (
    <>
    <div className="row" id="Bar">
      <div className="col"><img src="im4.png" id="leftLogo" alt="left-logo"/></div>
      <div className="col logoWrap"><img src="logo.png" id="webLogo" alt="right-logo"/></div>
    </div>

    <div className="messaging">
      <div className="inbox_msg">
      <ChatMenu nowOnline={nowOnline} chats={chats} setChats={setChats} setMessages={setChatMessages} setContactName={setContactUserName} setCurrentChat={setCurrentChat}/>
        <ChatWindow setChats={setChats} nowOnline={nowOnline} chatMessages={chatMessages} setChatMessages={setChatMessages} contactUserName={contactUserName} setContactUserName={setContactUserName} setCurrentChat={setCurrentChat}/>
      </div>
    </div>
  </>
  );
}

export default Contact;

