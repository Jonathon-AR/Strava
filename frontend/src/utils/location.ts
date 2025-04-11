// src/hooks/useGeoLocation.ts
import { useEffect, useRef, useState } from "react";

interface Coordinates {
  latitude: number;
  longitude: number;
  timestamp: number;
  speed: number;
}

interface GeoLocationOptions {
  enableHighAccuracy?: boolean;
  timeout?: number;
  maximumAge?: number;
}

export const useGeoLocation = (options: GeoLocationOptions = {}) => {
  const [position, setPosition] = useState<Coordinates | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isTracking, setIsTracking] = useState(false);

  const watchId = useRef<number | null>(null);

  const startTracking = () => {
    if (!navigator.geolocation) {
      setError("Geolocation not supported.");
      return;
    }

    watchId.current = navigator.geolocation.watchPosition(
      (pos) => {
        const coords: Coordinates = {
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
          timestamp: pos.timestamp,
          speed: pos.coords.speed? pos.coords.speed : 0,
        };
        setPosition(coords);
        setError(null);
      },
      (err) => {
        setError(err.message);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 10000,
        timeout: 10000,
        ...options,
      }
    );

    setIsTracking(true);
  };

  const stopTracking = () => {
    if (watchId.current !== null) {
      navigator.geolocation.clearWatch(watchId.current);
      watchId.current = null;
    }
    setIsTracking(false);
  };

  useEffect(() => {
    return () => stopTracking();
  }, []);

  return { position, error, isTracking, startTracking, stopTracking };
};
