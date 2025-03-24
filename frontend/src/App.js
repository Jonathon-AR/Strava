import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import React from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
          <GoogleOAuthProvider clientId={process.env.GAUTHCLIENTID}>
            <GoogleLogin
              onSuccess={(response) => console.log(response)}
              onError={() => console.log("Login Failed")}
            />
          </GoogleOAuthProvider>
      </header>
    </div>
  );
}

export default App;
