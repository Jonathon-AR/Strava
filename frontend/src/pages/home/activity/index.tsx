import React, { useEffect, useState } from "react";
import { useGeoLocation } from "../../../utils/location";
import { GpsPointType } from "../../../types/gpsPoint";
import { formatTime } from "../../../utils/helper";
import api from "../../../utils/axios";
import { data, useNavigate } from "react-router-dom";

interface ActivityProps {
  activityId: number;
  setActivityId: React.Dispatch<React.SetStateAction<number>>;
}

interface Stats {
  avgSpeed: number;
  distance: number;
}

const Activity: React.FC<ActivityProps> = ({ activityId, setActivityId }) => {
  const { position, error, isTracking, startTracking, stopTracking } =
    useGeoLocation();
  const [gpsQueue, setGpsQueue] = useState<GpsPointType[]>([]);
  const [time, setTime] = useState<number>(0);
  const [avgSpeed, setAvgSpeed] = useState<number>(0);
  const [distance, setDistance] = useState<number>(0);
  const [isActive, setIsActive] = useState<boolean>(false);

  useEffect(() => {
    startTracking();
  }, []);

  useEffect(() => {
    if (!isActive) return;
    const interval = setInterval(() => setTime((prev) => prev + 1), 1000);
    return () => clearInterval(interval);
  }, [isActive]);

  useEffect(() => {
    if (!isActive || activityId !== 0 || !position) return;

    const payload = { start: position.timestamp };
    (async () => {
      try {
        const data = await api.request<any>("/activity/start", "POST", payload);
        setActivityId(data);
      } catch (err) {
        console.error("Failed to fetch activityId:", err);
      }
    })();
  }, [isActive]);

  useEffect(() => {
    if (!isActive || !position || activityId === 0) return;

    const newPoint: GpsPointType = {
      latitude: position.latitude,
      longitude: position.longitude,
      timestamp: position.timestamp,
      speed: position.speed,
    };

    setGpsQueue((prev) => [...prev, newPoint]);
  }, [position, isActive, activityId]);

  useEffect(() => {
    if (!isActive || activityId === 0) return;

    const interval = setInterval(() => {
      if (gpsQueue.length < 1 || activityId === 0) return;

      const payload = {
        gpsPointsList: gpsQueue,
        activityId,
      };

      api
        .request<Stats>("/gps/", "POST", payload)
        .then((data) => {
          if (data) {
            if (data.avgSpeed)
              setAvgSpeed(parseFloat(data.avgSpeed.toFixed(2)));
            setDistance(parseFloat(data.distance.toFixed(2)));
          }
          setGpsQueue([]);
        })
        .catch((err) => {
          console.error("Failed to fetch stats:", err);
        });
      console.log(payload);
    }, 5000);

    return () => clearInterval(interval);
  }, [isActive, gpsQueue, activityId]);

  const toggleActivity = () => {
    setIsActive(!isActive);
  };

  const closeActivity = () => {
    api.request(`/activity/end`, "POST", {
      end: position ? position.timestamp : "",
      activityId: activityId,
    });
    setActivityId(0);
    stopTracking();
    setGpsQueue([]);
    setTime(0);
    setAvgSpeed(0);
    setDistance(0);
    setIsActive(false);
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-black text-white">
      <div className="text-sm mb-2">TIME</div>
      <div className="text-6xl font-bold">{formatTime(time)}</div>

      <div className="border-t border-gray-700 my-6 w-4/5"></div>

      <div className="text-sm mb-2">AVG SPEED</div>
      <div className="text-6xl font-bold">{avgSpeed}</div>
      <div className="text-xs text-gray-400 mb-6">KM/H</div>

      <div className="text-sm mb-2">DISTANCE</div>
      <div className="text-6xl font-bold">{distance}</div>
      <div className="text-xs text-gray-400 mb-10">KM</div>

      <div className="flex space-x-6">
        {isActive ? (
          <button
            className="bg-orange-500 w-16 h-16 rounded-full flex items-center justify-center"
            onClick={toggleActivity}
          >
            <div className="w-6 h-6 bg-white"></div>
          </button>
        ) : activityId === 0 ? (
          <button
            className="bg-orange-500 w-16 h-16 rounded-full flex items-center justify-center"
            onClick={toggleActivity}
          >
            <p>Start</p>
          </button>
        ) : (
          <>
            <button
              className="bg-orange-500 w-16 h-16 rounded-full flex items-center justify-center"
              onClick={toggleActivity}
            >
              <p>RESUME</p>
            </button>
            <button
              className="bg-orange-500 w-16 h-16 rounded-full flex items-center justify-center"
              onClick={closeActivity}
            >
              <p>FINISH</p>
            </button>
          </>
        )}
        <button className="w-14 h-14 rounded-full bg-gray-800 flex items-center justify-center">
          <svg
            className="w-6 h-6 text-orange-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 11c0 .552-.448 1-1 1s-1-.448-1-1 .448-1 1-1 1 .448 1 1z"
            />
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"
            />
          </svg>
        </button>
      </div>
    </div>
  );
};

export default Activity;
