import { useNavigate } from 'react-router-dom';
import './ChatPage.css';
import './SampleChat.css'
import Message from './Message';
import {MessageClass} from '.'


function UserSideBox({image,displayname,date,lastMessage,username, setMessages, nowOnline, setContactName, setCurrentChat}) {
  var navigation = useNavigate();

  // When push on contact -> take all messages.
  async function getMessages(currentChat) {

    // The GET request, which contains in the header the JWT token.
    const request = {
      method: 'GET',
      headers:{'Content-Type': 'application/json' ,'Authorization': 'Bearer '+ nowOnline.userToken},
      
    };

    // fetch from server, and in case 
    await fetch('http://localhost:5010/api/contacts/' + username + '/messages', request)
    .then(async response => {
       return response.json()
    }).then(res => {
    if(res != null) {
      currentChat.messages = []
      // This loop need move to Login.
      for(var i = 0; i < res.length; i++) {
       currentChat.messages.push(new MessageClass(res[i].from, res[i].content, res[i].type, res[i].date, res[i].senderPicture))
      }
      if(res.length > 0) {
      setMessages(currentChat.messages.map((message, key) => {
        return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
      }
    }
  });
  }



  function stopMedia() {
    // Wraper is the className of video and audio tags.
    var childElements = document.getElementsByClassName('Wrapper');
    for (var i = 0; i < childElements.length; i++) {
      childElements[i].pause();
      childElements[i].currentTime = 0;
    }
  }

  function handleClick() {
    // Already in '/contact' -> just rerender the contact.
    if(setMessages) {
      // Stop media while change chat.
      stopMedia();
      const currentChat = nowOnline.onlineUser.chats.find((e) => e.username === username)
      setContactName(username)
      setCurrentChat(currentChat)
      getMessages(currentChat)
    }
    // Move to contact page (from chatPage).
    else {
      navigation('/contact', {state:{user:username}})
    }
  }

    return(
        <button className="li_user" onClick={handleClick}>
          <div className="chat_list">
            <div className="chat_people">

              <div className="chat_img">
                {" "}
                <img
                  src={image}
                  alt="contact-picture"
                />{" "}
              </div>
              
              <div className="chat_ib">
                <h5>
                  {displayname} <span className="chat_date">{date}</span>
                </h5>
                <p>
                  {lastMessage}
                </p>
              </div>

            </div>
          </div>
        </button>
          
    )
}

export default UserSideBox;