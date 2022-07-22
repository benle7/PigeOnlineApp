import './ChatPage.css';
import './SampleChat.css'
import AddUser from './AddUser';
import { Link } from 'react-router-dom';


function ChatMenu({nowOnline, setChats, chats, setMessages, setContactName, setCurrentChat}) {

  // when clicking the logout button, close the web socket(The server will mark you offline)
  function closeSignalR() {
    nowOnline.signalR.signalrSocket.invoke("LogOut", nowOnline.onlineUser.username).then(() => {nowOnline.signalR.signalrSocket.stop();});
  }

  return (
    <>
    <div className="inbox_people">
      <div className="headind_bar row">
        <div className="exit col-2">
          <Link to="/"><button className="exitBut" onClick={closeSignalR}>
            <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-arrow-down-left-square-fill" viewBox="0 0 16 16">
              <path d="M2 16a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2zm8.096-10.803L6 9.293V6.525a.5.5 0 0 0-1 0V10.5a.5.5 0 0 0 .5.5h3.975a.5.5 0 0 0 0-1H6.707l4.096-4.096a.5.5 0 1 0-.707-.707z" />
            </svg>
          </button></Link>
        </div>


        <div className="recent_heading col-6">
          <h6>Recent</h6>
        </div>

        <div className="add_bar col-2">
        <AddUser nowOnline={nowOnline} setChats={setChats} setMessages={setMessages} setContactName={setContactName} setCurrentChat={setCurrentChat}/>
        </div>
      </div>
      
      <div className="inbox_chat">
        {chats}
      </div>

    </div>
</>
  );
}

export default ChatMenu;

