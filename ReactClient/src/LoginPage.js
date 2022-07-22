import { Link,useNavigate } from 'react-router-dom';
import './LoginPage.css';
import { useState } from 'react';
import { User } from '.';


function LoginPage({nowOnline}) {
  // Save the username and password which typed.
  const [userValues, setUserValues] = useState({username:'', password:''});
  var navigate = useNavigate();

  function validateForm() {
    return userValues.username.length > 0 && userValues.password.length > 7;
  }
  
  async function handleSubmit(event) {
    event.preventDefault();
    const request = {
      method: 'POST',
      headers:{'Content-Type': 'application/json'},
      body:JSON.stringify({username:userValues.username, password:userValues.password})
    };
    await fetch('http://localhost:5010/api/Users/Login', request)
    .then(async response => {
     if(response.statusText == "Not Found") {
       return null;
     }
     else {
       return response.json()
     }
    }).then(res => {
    // If exist user -> set the nowOnlineUser.
    if(res != null) {
      nowOnline.onlineUser = new User(userValues.username, userValues.password, res.displayName, res.image); // This line clear the chats!

      // we send the token in the username property.
      nowOnline.userToken = res.username;
      navigate('/chat');
    }
    else {
      document.getElementById('wrongInput').style.display = "block";
    }
  });
  }
  
  
  function handleChange(e) {
    const {name, value} = e.target;
    setUserValues({
      ...userValues,
      [name]:value
    })
  }

  
  return (
    <>
    <a href="http://127.0.0.1:5287/Reviews"><button className="revBut">
      <svg xmlns="http://www.w3.org/2000/svg" width="45" height="45" fill="gold" className="bi bi-star-fill" viewBox="0 0 16 16">
        <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
      </svg>
    </button></a>
    <form className="container-fluid" onSubmit={handleSubmit}>
            <div id="cardLogin" className="card row">
                <div className="card-body">

                    <div className="row">
                      <div className="col-12">             
                        <img src="LoginLogo.jpg" id="logo" alt="logo"></img>
                        <p id="slogan">Communicate like the old days.Nowadays.</p>
                        <br></br>
                      </div>
                    </div>

                    <div className="alert alert-danger alert-dismissable fade show" role="alert" id="wrongInput">
                      Wrong username or password. Please try again, or <Link to='/register'>click here</Link> if you are not registered.
                      <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close" id="close-alert-btn"></button>
                    </div>
                    
                    <div className="mb-3 row">
                      <label className="col-sm-6 col-lg-2">Username</label>
                      <div className="col-10">
                        <input name="username" type="text" className="form-control" autoComplete="off" placeholder="Please enter your username here..." onChange={handleChange} value={userValues.username} required></input>
                      </div>
                    </div>

                    <div className="mb-3 row">
                      <label className="col-sm-6 col-lg-2">Password</label>
                      <div className="col-10">
                        <input name="password" type="password" className="form-control" placeholder="Please enter your password here..." onChange={handleChange} value={userValues.password} required></input>
                      </div>
                    </div>

                    <div className="container">
                        <div className="row">
                          <div className="col-6">
                              Not Registered? <Link to='/register'>click here</Link> to join our community!        
                          </div>
                          <div className="col-6">
                            <button type="submit" id="loginButton" className="btn btn-outline-primary" disabled={!validateForm()}>
                              Login
                            </button>
                          </div>
                        </div>
                        <br></br>
                        <p id="moreInfo">PigeOnline is the fastest and safest way to communicate.</p>
                    </div>

                </div>
              </div>
        </form>
        </>
  );
}

export default LoginPage;

