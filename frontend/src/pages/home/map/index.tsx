import React, { useEffect, useRef, useState } from "react";
import api from "../../../utils/axios";

interface MapProps {
  activityId: number;
}

type Coordinate = {
  latitude: number;
  longitude: number; 
  timestamp: string;
  speed: number;
};

type LatLng = {
  lat: number;
  lng: number;
};

declare global {
  interface Window {
    google: any;
  }
}

const loadGoogleMapsScript = (callback: () => void) => {
  const existingScript = document.getElementById("google-maps-script");
  if (existingScript) {
    callback();
    return;
  }

  const script = document.createElement("script");
  script.id = "google-maps-script";
  script.src = `https://maps.googleapis.com/maps/api/js?key=${process.env.REACT_APP_GOOGLE_MAPS_API_KEY}`;
  script.async = true;
  script.onload = callback;
  document.body.appendChild(script);
};

const MapWithPath: React.FC<MapProps> = ({ activityId }) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const [coordinates, setCoordinates] = useState<Coordinate[]>([]);

  // Fetch coordinates
  useEffect(() => {
    if (!activityId) return;
  
    const fetchCoordinates = () => {
      api.request(`/gps/${activityId}`, "GET")
        .then((data:any) => {
          if (data.gps_points) {
            const sorted = data.gps_points.sort((a: Coordinate, b: Coordinate) =>
              a.timestamp.localeCompare(b.timestamp)
            );
            setCoordinates(sorted);
          }
        })
        .catch((err:any) => {
          console.error("Failed to fetch coordinates:", err);
        });
    };
  
    fetchCoordinates(); // initial fetch
  
    const intervalId = setInterval(fetchCoordinates, 5000); // poll every 5s
  
    return () => clearInterval(intervalId); // cleanup
  }, [activityId]);

  // Render map
  useEffect(() => {
    if (!coordinates.length) return;

    loadGoogleMapsScript(() => {
      if (!mapRef.current || !window.google) return;

      const google = window.google;
      const pathPoints: LatLng[] = coordinates.map((coord) => ({
        lat: coord.latitude,
        lng: coord.longitude,
      }));

      const map = new google.maps.Map(mapRef.current, {
        zoom: 14,
        center: pathPoints[0],
      });

      const path = new google.maps.Polyline({
        path: pathPoints,
        geodesic: true,
        strokeColor: "#2196f3",
        strokeOpacity: 1.0,
        strokeWeight: 4,
      });

      path.setMap(map);
    });
  }, [coordinates]);

  if (!activityId) return null;

  return <div ref={mapRef} style={{ height: "100vh", width: "100%" }} />;
};

export default MapWithPath;
