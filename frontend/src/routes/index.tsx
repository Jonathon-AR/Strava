import React from 'react';
import { Routes as Switch, Route, Navigate } from 'react-router-dom';
import Login from '../pages/login';
import Activity from '../pages/activity';

const AppRoutes: React.FC = () => {
  return (
    <Switch>
      <Route path="*" element={<Navigate to="/" />} />
      <Route path="/login" element={<Login/>} />
      <Route path="/activity" element={<Activity/>} />
    </Switch>
  );
};

export default AppRoutes;
 