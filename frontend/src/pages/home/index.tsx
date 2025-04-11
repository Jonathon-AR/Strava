import React, { useState, useEffect } from "react";
import SwipeableViews from "react-swipeable-views";
import { useNavigate, useLocation } from "react-router-dom";
import Activity from "./activity";
import Map from "./map";

const Home: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [activityId, setActivityId] = useState<number>(0);
  // Define index based on current route
  const [index, setIndex] = useState(0);

  // useEffect(() => {
  //   // Update route when index changes
  //   navigate(routes[index]);
  // }, [index, navigate, routes]);

  return (
    <SwipeableViews index={index} onChangeIndex={(i) => setIndex(i)}>
      <div>
        <Activity activityId={activityId} setActivityId={setActivityId}/>
      </div>
      <div>
        <Map activityId={activityId}/>
      </div>
    </SwipeableViews>
  );
};

export default Home;
