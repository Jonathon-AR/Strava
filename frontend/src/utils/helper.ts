  export const formatTime = (seconds: number): string => {
    const hrs: string = String(Math.floor(seconds / 3600)).padStart(2, "0");
    const mins = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
    const secs = String(seconds % 60).padStart(2, "0");
    if (hrs === "00") return `${mins}:${secs}`;
    return `${hrs}:${mins}:${secs}`;
  };
