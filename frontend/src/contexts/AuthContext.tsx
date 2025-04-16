
import { GoogleOAuthProvider } from "@react-oauth/google";
import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import apiService from "../utils/axios";

interface AuthData {
  token: string;
  refreshToken: string;
  name: string;
  email: string;
}

type AuthContextType = {
  token: string | null;
  name: string | null;
  email: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (authData: AuthData) => void;
  logout: () => void;
  checkSession: () => Promise<boolean>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem("token"));
  const [name, setName] = useState<string | null>(localStorage.getItem("name"));
  const [email, setEmail] = useState<string | null>(localStorage.getItem("email"));
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  // Check session validity on mount
  useEffect(() => {
    const verifySession = async () => {
      if (token) {
        try {
          const isValid = await apiService.verifyToken();
          if (!isValid) {
            // Silent token refresh if verify fails
            const refreshToken = localStorage.getItem("refreshToken");
            if (refreshToken) {
              try {
                const response = await apiService.refresh(refreshToken);
                login({
                  token: response.token,
                  refreshToken: response.refreshToken,
                  name: response.name || "",
                  email: response.email
                });
              } catch (error) {
                // If refresh fails, logout
                handleLogout();
              }
            } else {
              handleLogout();
            }
          }
        } catch (error) {
          handleLogout();
        }
      }
      
      setIsLoading(false);
    };

    verifySession();
  }, []);

  const login = ({ token, refreshToken, name, email }: AuthData) => {
    setToken(token);
    setName(name);
    setEmail(email);
    
    // Store in localStorage
    localStorage.setItem("token", token);
    localStorage.setItem("refreshToken", refreshToken);
    if (name) localStorage.setItem("name", name);
    localStorage.setItem("email", email);
  };

  const handleLogout = () => {
    setToken(null);
    setName(null);
    setEmail(null);
    localStorage.clear();
  };

  const logout = () => {
    handleLogout();
    navigate("/login");
  };

  const checkSession = async (): Promise<boolean> => {
    if (!token) return false;
    
    try {
      const isValid = await apiService.verifyToken();
      return isValid;
    } catch (error) {
      return false;
    }
  };

  return (
    <GoogleOAuthProvider clientId={process.env.REACT_APP_GAUTHCLIENTID as string}>
      <AuthContext.Provider value={{ 
        token, 
        name, 
        email, 
        isAuthenticated: !!token,
        isLoading,
        login, 
        logout,
        checkSession
      }}>
        {children}
      </AuthContext.Provider>
    </GoogleOAuthProvider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
};
