import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config:any) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const apiRequest = <T>(
  url: string,
  method: "GET" | "POST" | "PUT" | "DELETE",
  data?: any
): Promise<T> => {
  return api({
    url,
    method,
    data,
  }).then((res: { data: any; }) => res.data);
};
