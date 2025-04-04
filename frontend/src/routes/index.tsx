import React from 'react';
import { Routes as Switch, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/login';

const AppRoutes: React.FC = () => {
  return (
    <Switch>
      {/* <Route path="/" element={<HomePage />} /> */}
      <Route path="/login" element={<LoginPage />} />
      {/* <Route path="/dashboard" element={<Dashboard />} /> */}
      {/* Fallback route */}
      <Route path="*" element={<Navigate to="/" />} />
    </Switch>
  );
};

export default AppRoutes;
 