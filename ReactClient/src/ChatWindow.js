import './ChatPage.css';
import './SampleChat.css';
import DropUp from './DropUp';
import {useState,useEffect} from 'react'
import {MessageClass} from './index';
import Message from './Message';
import UserSideBox from './UserSideBox';


function ChatWindow({setChats, nowOnline, chatMessages, contactUserName, setChatMessages, setContactUserName, setCurrentChat}) {
  // The current text which typed.
  const [textInput, setTextInput] = useState('');
  // The current chat i'm in.
  const currentUserChat = nowOnline.onlineUser.chats.find((e) => e.username === contactUserName);
  // The current audio which recorded.
  const [lastRecord, setLastRecord] = useState('');

  async function sendNewMessage(newMessage, date) {
    var flag = 0;

    /*
     * in case the user sent a new message, make a transfer request to the server that the other user is registered to(may be the same server).
     * The POST request contains the user JWT token in the header, and the desired JSON onject in the body(from ,to ,content).
     */
    const request1 = {
      method: 'POST',
      headers:{'Content-Type': 'application/json' ,'Authorization': 'Bearer '+ nowOnline.userToken},
      body:JSON.stringify({from:nowOnline.onlineUser.username ,to:currentUserChat.username, content:textInput})
    };
    await fetch(currentUserChat.server + '/api/transfer', request1)
    .then(async response => {
     if(response.status == 201) {
       flag = 1;
     }
    });


    /*
     * In case of a successful transfer, add the new message localy and notify your server that a new message sent.
     * In addition, invoke the signalR web socket, so if the other user is registered to the same server as I and he's online, 
     * he will see the message immediately.
     */
    if (flag == 1) {
      const request2 = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + nowOnline.userToken },
        body:JSON.stringify({content:textInput})
      };
      await fetch('http://localhost:5010/api/contacts/' + currentUserChat.username + '/messages', request2)
        .then(async response => {
          if (response.status == 201)  {
              // Push the new message to the current chat.
            currentUserChat.messages.push(newMessage);
            // Update date chat.
            currentUserChat.date = date;

            currentUserChat.lastMessage = textInput;
            // Rerender the side chats list and the chat window.
            setChatMessages(currentUserChat.messages.map((message, key) => {
              return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
            setChats(nowOnline.onlineUser.chats.map((chat, key) => {
              return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} setMessages={setChatMessages} setContactName={setContactUserName} setCurrentChat={setCurrentChat} nowOnline={nowOnline} key={key}/>}));
            // After success new message - Use signalR -> The server will push the message.
              //nowOnline.signalR.signalrSocket.invoke("MessageSent", {from:nowOnline.onlineUser.username, to:currentUserChat.username, content:textInput})
            var scrollToDown = document.getElementById("messageDisplay");
            scrollToDown.scrollTop = scrollToDown.scrollHeight;
          }
        });



    }
  }

  function handleChange(e) {
    setTextInput(e.target.value)
  }

  function newTextMessage() {
    // Don't send empty message.
    if (document.getElementById("messageText").value === "") {
      return;
    }
    // Clear the message line.
    document.getElementById("messageText").value = "";

    const date = dateNow()
    const newMessage = new MessageClass(nowOnline.onlineUser.username, textInput, "text", date, nowOnline.onlineUser.picture)
    //afterMessage(newMessage, textInput,date);
    sendNewMessage(newMessage, date)
  }

  function newPictureMessage(e) {
    const image = document.getElementById("image-input").files[0].name;
    if(image === "") {
      return;
    }
    const pathImg = URL.createObjectURL(document.getElementById("image-input").files[0])
    var fileName = image;

    var idxDot = fileName.lastIndexOf(".") + 1;
    var extFile = fileName.substring(idxDot, fileName.length).toLowerCase();
    // If img type.
    if (extFile === "jpg" || extFile === "jpeg" || extFile === "png"){
      const date = dateNow()
      const newMessage = new MessageClass(nowOnline.onlineUser.username, pathImg, "image", date, nowOnline.onlineUser.picture)
      currentUserChat.lastMessage = "image";
      //afterMessage(newMessage, "image", date);
      // Clean image input.
      document.getElementById("image-input").value = "";
    } else{
       alert('Invalid file type !')
    }   
  }

  function newVideoMessage() {
    const video = document.getElementById("video-input").files[0].name;
    if(video === "") {
      return;
    }

    const pathVid = URL.createObjectURL(document.getElementById("video-input").files[0])
    var fileName = video;

    var idxDot = fileName.lastIndexOf(".") + 1;
    var extFile = fileName.substring(idxDot, fileName.length).toLowerCase();
    // If video type.
    if (extFile === "mp4") {
      const date = dateNow()
      const newMessage = new MessageClass(nowOnline.onlineUser.username, pathVid, "video", date, nowOnline.onlineUser.picture)
      currentUserChat.lastMessage = "video";
      //afterMessage(newMessage, "video",date);
      // Clean video input.
      document.getElementById("video-input").value = "";
    } else{
       alert('Invalid file type !')
    }   
  }
  
  function startRecord() {
    // Clear the recorder
    document.getElementById('recordPlay').src = null;
    // True for recording.
    let audioIN = { audio: true };
 
    // Access the permission.
    navigator.mediaDevices.getUserMedia(audioIN)
 
      .then(function (mediaStreamObj) {
        
        // First audio element - the recorder.
        let audio = document.getElementById('recordPlay');
 
        // Set recorder src.
        if ("srcObject" in audio) {
          audio.srcObject = mediaStreamObj;
        }
        // Old version.
        else {
          audio.src = window.URL
            .createObjectURL(mediaStreamObj);
        }
 
        audio.onloadedmetadata = function (ev) {
          // exist data after recording.
        };
 
        // Start record button.
        let start = document.getElementById('btnStart');
 
        // Stop record button.
        let stop = document.getElementById('btnStop');
 
        // Second audio element - play the audio.
        let playAudio = document.getElementById('adioPlay');
 
        // Use 'MediaRecorder' API.
        let mediaRecorder = new MediaRecorder(mediaStreamObj);
 
        // Start event.
        start.addEventListener('click', function (ev) {
          audio.src = null;
          audio.play();
          mediaRecorder.start();
        })
 
        // Stop event.
        stop.addEventListener('click', function (ev) {
          if(mediaRecorder.state !== "inactive")
            mediaRecorder.stop();
            audio.pause();
        });
 
        // Push audio data to chunk array.
        mediaRecorder.ondataavailable = function (ev) {
          dataArray.push(ev.data);
        }
 
        // Chunk array to store the audio data.
        let dataArray = [];
 
        // After stop - convert the audio data by Blob.
        mediaRecorder.onstop = function (ev) {
          // blob of type mp3.
          let audioData = new Blob(dataArray,
                    { 'type': 'audio/mp3;' });
           
          // After fill up - clear.
          dataArray = [];
 
          // Create audio url.
          let audioSrc = window.URL
              .createObjectURL(audioData);
 
          // Pass the audio url to the 2nd audio tag.
          playAudio.src = audioSrc;

          audioIN[audio] = false;
          // Set lastRecord state.
          setLastRecord(playAudio.src);
        }
      })
 
      // If any error occurs.
      .catch(function (err) {
        //console.log(err.name, err.message);
      });
    }

    // Reset the 2nd audio element.
    function resetRecord() {
      document.getElementById('adioPlay').src = null;
      document.getElementById('recordPlay').src = null;
      // Don't send last record after close the modal.
      setLastRecord("");
    }

    function newRecordMessage() {
    if(lastRecord === "") {
      return;
    }
      const date = dateNow()
      const newMessage = new MessageClass(nowOnline.onlineUser.username, lastRecord, "audio", date, nowOnline.onlineUser.picture)
      currentUserChat.lastMessage = "audio";
      //afterMessage(newMessage, "audio", date);
      resetRecord();
    }

/*
  function afterMessage(newMessage,lastMessage, date) {
    var result = sendNewMessage()
    console.log(result)
    if(result) {
    // Push the new message to the current chat.
    currentUserChat.messages.push(newMessage);
    // Update date chat.
    currentUserChat.date = date;

    currentUserChat.lastMessage = textInput;
    // Rerender the side chats list and the chat window.
    setChatMessages(currentUserChat.messages.map((message, key) => {
      return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
    setChats(nowOnline.onlineUser.chats.map((chat, key) => {
      return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} setMessages={setChatMessages} setContactName={setContactUserName} setCurrentChat={setCurrentChat} nowOnline={nowOnline} key={key}/>}));
    }
  }
*/

    function dateNow() {
        let currentDate = new Date();
        let minutes = currentDate.getMinutes();
        let hours = currentDate.getHours();
        let days = currentDate.getDate();
        let months = currentDate.getMonth() + 1;
        if(minutes.toString().length === 1) {
          minutes = "0" + minutes;
        }
        if(hours.toString().length === 1) {
          hours = "0" + hours;
        }
        if(days.toString().length === 1) {
          days = "0" + days;
        }
        if(months.toString().length === 1) {
          months = "0" + months;
        }
        const date = hours + ":" + minutes + " | " + days + "/" + months + "/" + currentDate.getFullYear();
        return date;
      }
    

    // Send message with enter.
    function handleKeyDown(event) {
      if(event.key === "Enter") { 
        document.getElementById("buttonForSend").click();
      }
    }
    
    
    // When msg_history is onLoad - push on chat.
    function scrollDown() {
      var scrollToDown = document.getElementById("messageDisplay");
      scrollToDown.scrollTop = scrollToDown.scrollHeight
    }

    // on rerender - when send new message.
    useEffect(() => {
      setListen();
    },[]);


     /* make the socket listen to the event "MeesageRecivied", so if a new message is recived, this user will be able to see it immediately.
      * the unnamed function declared inside is operating the situation that this event occured.
      */ 
    function setListen() {
      // Define handler function when the server call to MessageRecived.
      nowOnline.signalR.signalrSocket.on("MessageRecived", function(from, to, content) {
      // Only for the reciever.
      if(to == nowOnline.onlineUser.username) {
      const chatToPush = nowOnline.onlineUser.chats.find((e)=> e.username == from);
      chatToPush.messages.push(new MessageClass(from, content, "text", dateNow(), "im3.jpg"));
      chatToPush.lastMessage = content;
      chatToPush.date = dateNow();
      // If my current chat is with the sender -> rerender messages.
      if(currentUserChat.username == from) {
        setChatMessages(chatToPush.messages.map((message, key) => {
          return <Message senderUserName={message.from} content={message.messageContent} nowOnline={nowOnline} type={message.messageType} date={message.messageDate} senderPicture={message.senderPicture} key={key}/>}));
      }
      setChats(nowOnline.onlineUser.chats.map((chat, key) => {
        return <UserSideBox displayname={chat.displayName} image={chat.image} date={chat.date} lastMessage={chat.lastMessage} username={chat.username} setMessages={setChatMessages} setContactName={setContactUserName} setCurrentChat={setCurrentChat} nowOnline={nowOnline} key={key}/>}));
      
        var scrollToDown = document.getElementById("messageDisplay");
      scrollToDown.scrollTop = scrollToDown.scrollHeight;
      }
     })
    
    }
    
  return (
    <div className="mesgs">
      <div className="msg_history" id="messageDisplay" onLoad={scrollDown}>
      {/* show the messages of current chat.*/}
        {chatMessages}
      </div>
      <div className="row type_msg">
      
        <div className='col-xl-1 col-sm-1 col-xs-1 col' id='sendBut'>
          <button id="buttonForSend" className="SendButton msg_send_btn" type="button" onClick={newTextMessage}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-send-fill" viewBox="0 0 16 16">
              <path d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z" />
            </svg>
          </button>
        </div>

        <DropUp newPictureMessage={newPictureMessage} newVideoMessage={newVideoMessage} startRecord={startRecord} newRecordMessage={newRecordMessage} resetRecord={resetRecord} />

        <div className='col-xl-10 col-sm-10 col-xs-10 col' id='inputRow'>
          <input type="text" className="form-control" id="messageText" autoComplete="off" placeholder="New message here..." onChange={handleChange} onKeyDown={handleKeyDown}></input>
        </div>

      </div>
    </div>
  );
}

export default ChatWindow;
