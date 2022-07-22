import './RegisterPage.css';
import { Link,useNavigate } from 'react-router-dom';
import { useState } from 'react';
import {User} from './index';


function RegisterPage({nowOnline}) {
  // Save the register values which typed.
  const [userValues, setUserValues] = useState({username:'', password:'', validatePass:'',displayName:'', picture:''});
  var navigate = useNavigate();
  // Save validation of each value.
  const [isValidValues] = useState({validUserName:false, validPassword:false, validPassword2:false, validDisplayName:false, 
  validPicture:true});

  function validateForm() {
    return isValidValues.validUserName && isValidValues.validPassword && isValidValues.validPassword2 &&
     isValidValues.validDisplayName && isValidValues.validPicture;
  }

  function validateUserName() {
    // Username with at least 1 character.
    if(userValues.username.length > 0) {
        document.getElementById("usernameError").style.display = "none";
        document.getElementById("usedMessage").style.display = "none";
        
        document.getElementById("usernameError").style.display = "unset";
        document.getElementById("usernameError").style.color = "rgb(15, 203, 15)";
        document.getElementById("usernameError").innerHTML = "Valid !"
    }
    else {
      document.getElementById("usedMessage").style.display = "none"; 
      document.getElementById("usernameError").style.display = "block";
      document.getElementById("usernameError").style.color = "red";
      document.getElementById("usernameError").innerHTML = "Username must contain at least one character."
    }
    return userValues.username.length > 0;
  }

  function validateDisplayName() {
    // displayName with at least 1 character.
    if(userValues.displayName.length > 0) {
      document.getElementById("displayNameError").style.display = "unset";
      document.getElementById("displayNameError").style.color = "rgb(15, 203, 15)";
      document.getElementById("displayNameError").innerHTML = "Valid !"
    } else {
      document.getElementById("displayNameError").style.display = "block";
      document.getElementById("displayNameError").style.color = "red";
      document.getElementById("displayNameError").innerHTML = "The display name must contain at least one character."
    }
    return userValues.displayName.length > 0;
  }

  function validatePassword() {
    // Check valid password.
    var passw = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    if (userValues.password.match(passw)) {
      document.getElementById("passwordError").style.display = "block";
      document.getElementById("passwordError").style.color = "rgb(15, 203, 15)";
      document.getElementById("passwordError").innerHTML = "Valid !"
      return true;
    } else {
      document.getElementById("passwordError").style.display = "block";
      document.getElementById("passwordError").style.color = "red";
      document.getElementById("passwordError").innerHTML = ("Choose at least 8 characters- one numeric digit, one uppercase and one lowercase letter.")
      return false;
    }
  }

  function validatePassword2() {
    // Check if equal to the password value.
    if (userValues.password === userValues.validatePass && userValues.password.length > 0) {
      document.getElementById("validatePassError").style.display = "unset";
      document.getElementById("validatePassError").style.color = "rgb(15, 203, 15)";
      document.getElementById("validatePassError").innerHTML = "Valid !"
    }
    else {
      document.getElementById("validatePassError").style.display = "block";
      document.getElementById("validatePassError").style.color = "red";
      document.getElementById("validatePassError").innerHTML = "This field must be equal to the password above."
    }
    return userValues.password === userValues.validatePass;
  }

  function validatePicture() {
    var fileName = userValues.picture;
    var idxDot = fileName.lastIndexOf(".") + 1;
    var extFile = fileName.substring(idxDot, fileName.length).toLowerCase();
    // Check valid img type.
    if (extFile === "jpg" || extFile === "jpeg" || extFile === "png" || userValues.picture === ""){
        document.getElementById("pictureError").style.display = "unset";
        document.getElementById("pictureError").style.color = "rgb(15, 203, 15)";
        document.getElementById("pictureError").innerHTML = "Valid !"
        return true;
    }else{
        document.getElementById("pictureError").style.display = "block";
        document.getElementById("pictureError").style.color = "red";
        document.getElementById("pictureError").innerHTML = "Only jpg/jpeg and png files are allowed."
        return false;
    }   
  }

  function handleChange(e) {
    const {name, value} = e.target;
    setUserValues({
      ...userValues,
      [name]:value
    })
    validityCheck(name, value);
  }

  function validityCheck(name, value) {
    switch(name) {
      case 'username':
        userValues.username = value;
        if(validateUserName()) {
          isValidValues.validUserName = true;
        } 
        else {
          isValidValues.validUserName = false;
        }
        break;
      case 'password':
        userValues.password = value;
        validatePassword2()
        if(validatePassword()) {
          isValidValues.validPassword = true;
          isValidValues.validPassword2 = true;
        }
        else {
          isValidValues.validPassword = false;
          isValidValues.validPassword2 = false;
        }
        break;
      case 'validatePass':
        userValues.validatePass = value;
        validatePassword()
        if(validatePassword2()) {
          isValidValues.validPassword2 = true;
          isValidValues.validPassword = true;
        }
        else {
          isValidValues.validPassword2 = false;
          isValidValues.validPassword = false;
        }
        break;
      case 'displayName':
        userValues.displayName = value;
        if(validateDisplayName()) {
          isValidValues.validDisplayName = true;
        }
        else {
          isValidValues.validDisplayName = false;
        }
        break;
      case 'picture':
        userValues.picture = value;
        if(validatePicture()) {
          isValidValues.validPicture = true;
        } 
        else {
          isValidValues.validPicture = false;
        }
        break;
        default:
          break;
    }
  }

  function handleSubmit(event) {
    // Enter default picture.
    if(userValues.picture === "") {
      userValues.picture = "defaultpic.png";
    } else {
      userValues.picture = URL.createObjectURL(document.getElementById("inputGroupFile02").files[0])
    }
    // Set nowOnlineUser.
    registerServer();
    
    event.preventDefault();
  }
  
  // Not case sensitive checking ! (when search exist user).
  async function registerServer() {
    const request = {
      method: 'POST',
      headers:{'Content-Type': 'application/json'},
      body:JSON.stringify({username:userValues.username, password:userValues.password, image:"im3.jpg", displayName:userValues.displayName})
    };
    await fetch('http://localhost:5010/api/Users', request).then(async response => {
    return response.text()}).then(res => {

      if(res == "false") {
        document.getElementById("usedMessage").style.display = "block";
        document.getElementById("usernameError").style.display = "none";
      }
      else {
        nowOnline.onlineUser = new User(userValues.username, userValues.password, userValues.displayName, userValues.picture);
        nowOnline.userToken = res;
        navigate('/chat');
      }
    
   });

  }

  return (
    <form className="container-fluid" onSubmit={handleSubmit} >
    <div id="cardRegister" className="card row">
      <div className="card-body">

        <div className="row">
          <div className="col-12" id="logoWrapper">
            <img src="LoginLogo.jpg" id="logo" alt="logo"></img>
            <p id="slogan">Communicate like the old days.Nowadays.</p>
          </div>
        </div>

        <div className="mb-3 row">
          <label className="col-sm-6 col-lg-2">Username</label>
          <div className="col-10">
            <input name="username" type="text" className="form-control" autoComplete="off" placeholder="Please enter your username here..." onChange={handleChange} />
            <div id="usernameError" className="error-divs">
              Username must contain at least one character.
            </div>
            <div id="usedMessage" className="error-divs">
              Sorry, this username is already taken.
            </div>
          </div>
        </div>

        <div className="mb-3 row">
          <label className="col-sm-6 col-lg-2">Password</label>
          <div className="col-10">
            <input name="password" type="password" className="form-control" placeholder="Please enter your password here..." onChange={handleChange}/>
            <div id="passwordError" className="error-divs">
            Choose at least 8 characters- one numeric digit, one uppercase and one lowercase letter.
            </div>
          </div>
        </div>

        <div className="mb-3 row">
          <label className="col-sm-6 col-lg-2">Verify password</label>
          <div className="col-10">
            <input name="validatePass" type="password" className="form-control" placeholder="Please enter your password again..." onChange={handleChange}/>
            <div id="validatePassError" className="error-divs">
              This field must be equal to the password above.
            </div>
          </div>
        </div>

        <div className="mb-3 row">
          <label className="col-sm-6 col-lg-2">Display name</label>
          <div className="col-10">
            <input name="displayName" className="form-control" autoComplete="off" placeholder="Please enter your display name..." onChange={handleChange}/>
            <div id="displayNameError" className="error-divs">
              The display name must contain at least one character.
            </div>
          </div>
        </div>

        <div className="mb-3 row">
          <label className="col-sm-6 col-lg-2">Upload picture</label>
          <div className="col-10">
            <input name="picture" type="file" accept="image/*" id="inputGroupFile02" className="form-control" onChange={handleChange}/>
            <div id="pictureError" className="error-divs">
            Only jpg/jpeg and png files are allowed.
            </div>
          </div>
        </div>
        
        <div className="container">
          <div className="row">
            <div className="col-5">
              Already Registered? <Link to='/'>click here!</Link>
            </div>
            <div className="col-7">
              <button type="submit" id="loginButton" className="btn btn-outline-primary"  disabled={!validateForm()}>Register</button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </form>
  );
}

export default RegisterPage;
