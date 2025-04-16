import React, { useState, useEffect } from "react";
import { useGoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import apiService from "../../utils/axios";

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login: saveAuthData, token } = useAuth();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const checkExistingSession = async () => {
      if (token) {
        try {
          const isValid = await apiService.verifyToken();
          if (isValid) {
            navigate("/home");
          }
        } catch (error) {}
      }
    };

    checkExistingSession();
  }, [token, navigate]);

  const handleLoginError = (message: string) => {
    setError(message);
    setIsLoading(false);
  };

  const login = useGoogleLogin({
    scope: "openid profile email",
    onSuccess: async (googleResponse) => {
      setIsLoading(true);
      setError(null);
      try {
        const accessToken: string = googleResponse.access_token;
        const response = await apiService.loginWithGoogle(accessToken);
        const { token, refreshToken, name, email } = response;
        saveAuthData({ token, refreshToken, name, email });
        navigate("/home");
      } catch (err: any) {
        console.error("Login failed:", err);
        if (err.message?.includes("Invalid Google access token")) {
          handleLoginError("Google authentication failed. Please try again.");
        } else if (err.message?.includes("Failed to retrieve user info")) {
          handleLoginError(
            "Couldn't retrieve your information from Google. Please try again."
          );
        } else {
          handleLoginError("Login failed. Please try again later.");
        }
      }
    },
    onError: () => {
      handleLoginError("Google login failed. Please try again.");
    },
  });

  return (
    <div className="flex h-screen w-full items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-xl bg-white p-8 shadow-xl">
        <div className="mb-6 flex flex-col items-center">
          <div className="h-16 w-16 rounded-full bg-gray-300" />
          <h1 className="mt-4 text-2xl font-bold text-gray-800">Welcome</h1>
          <p className="text-sm text-gray-500">Please log in to continue</p>
        </div>

        {error && (
          <div className="mb-4 rounded bg-red-100 px-4 py-2 text-sm text-red-700">
            {error}
          </div>
        )}

        <button
          onClick={() => {
            setError(null);
            login();
          }}
          disabled={isLoading}
          className={`w-full rounded-lg px-4 py-2 text-white transition ${
            isLoading
              ? "cursor-not-allowed bg-blue-300"
              : "bg-blue-600 hover:bg-blue-700"
          }`}
        >
          {isLoading ? "Logging in..." : "Login with Google"}
        </button>
      </div>
    </div>
  );
};

export default Login;
