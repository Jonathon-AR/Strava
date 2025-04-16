import React from 'react';
import { Routes as Switch, Route, Navigate } from 'react-router-dom';
import Login from '../pages/login';
import Home from '../pages/home';
// import Activity from '../pages/home/activity';
// import Map from '../pages/home/map';

const AppRoutes: React.FC = () => {
  return (
    <Switch>
      <Route path="*" element={<Navigate to="/home" />} />
      <Route path="/login" element={<Login/>} />
      <Route path="/home" element={<Home/>} />
      {/* <Route path="/activity" element={<Activity/>} />
      <Route path="/map" element={<Map/>} /> */}
    </Switch>
  );
};

export default AppRoutes;
 