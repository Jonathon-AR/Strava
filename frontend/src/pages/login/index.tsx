import React from "react";
import { logo } from "../../assets";
import "../../App.css";
import { useGoogleLogin } from "@react-oauth/google";
import { apiRequest } from "../../utils/axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login: saveAuthData } = useAuth();

  const login = useGoogleLogin({
    scope: 'openid profile email',
    onSuccess: (googleResponse) => {
      const accessToken: string = googleResponse.access_token;

      apiRequest<any>(
        "/login/auth/google",
        "POST",
        { accessToken }
      ).then((response) => {
        const { token, name, email } = response;
        saveAuthData({ token, name, email });
        navigate("/activity");
      }).catch(err => {
        console.error("Login failed:", err);
      });
    },
    onError: () => console.log("Login Failed"),
  });

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <button onClick={() => login()}>Login with Google</button>
      </header>
    </div>
  );
};

export default Login;
