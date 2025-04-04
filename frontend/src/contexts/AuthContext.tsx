import { GoogleOAuthProvider } from "@react-oauth/google";
import React, { createContext, useContext, useState } from "react";

type AuthContextType = {
  token: string | null;
  name: string | null;
  email: string | null;
  login: (authData: { token: string; name: string; email: string }) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem("token")
  );
  const [name, setName] = useState<string | null>(localStorage.getItem("name"));
  const [email, setEmail] = useState<string | null>(
    localStorage.getItem("email")
  );

  const login = ({
    token,
    name,
    email,
  }: {
    token: string;
    name: string;
    email: string;
  }) => {
    setToken(token);
    setName(name);
    setEmail(email);
    localStorage.setItem("token", token);
    localStorage.setItem("name", name);
    localStorage.setItem("email", email);
  };

  const logout = () => {
    setToken(null);
    setName(null);
    setEmail(null);
    localStorage.clear();
  };

  return (
    <GoogleOAuthProvider
      clientId={process.env.REACT_APP_GAUTHCLIENTID as string}
    >
      <AuthContext.Provider value={{ token, name, email, login, logout }}>
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
