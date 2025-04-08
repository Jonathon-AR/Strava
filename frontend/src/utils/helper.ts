/**
 * Capitalizes the first letter of a string
 */
export const capitalize = (str: string): string => 
    str.charAt(0).toUpperCase() + str.slice(1);
  
  /**
   * Formats a date to YYYY-MM-DD
   */
  export const formatDate = (date: Date): string => 
    date.toISOString().split("T")[0];
  
  /**
   * Generates a random string of given length
   */
  export const generateRandomString = (length: number): string => {
    const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    return Array.from({ length }, () => chars[Math.floor(Math.random() * chars.length)]).join("");
  };
  
  /**
   * Debounces a function (useful for search inputs, etc.)
   */
  export const debounce = <T extends (...args: any[]) => void>(func: T, delay: number): (...args: Parameters<T>) => void => {
    let timeoutId: ReturnType<typeof setTimeout>;
    return (...args: Parameters<T>) => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => func(...args), delay);
    };
  };
  
  /**
   * Throttles a function (limits execution to once every `limit` ms)
   */
  export const throttle = <T extends (...args: any[]) => void>(func: T, limit: number): (...args: Parameters<T>) => void => {
    let lastCall = 0;
    return (...args: Parameters<T>) => {
      const now = Date.now();
      if (now - lastCall >= limit) {
        lastCall = now;
        func(...args);
      }
    };
  };
  

  export const formatTime = (seconds: number): string => {
    const hrs: string = String(Math.floor(seconds / 3600)).padStart(2, "0");
    const mins = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
    const secs = String(seconds % 60).padStart(2, "0");
    if (hrs == "00") return `${mins}:${secs}`;
    return `${hrs}:${mins}:${secs}`;
  };
