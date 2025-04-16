// api.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosError } from "axios";

interface AuthResponse {
  token: string;
  refreshToken: string;
  newUser: boolean;
  name: string;
  email: string;
}

interface TokenVerifyResponse {
  status: "ok" | "expired";
}

class ApiService {
  private instance: AxiosInstance;
  private isRefreshing = false;
  private refreshSubscribers: ((token: string) => void)[] = [];

  constructor(baseURL: string) {
    this.instance = axios.create({
      baseURL,
      timeout: 10000,
      headers: { "Content-Type": "application/json" },
    });

    this.instance.interceptors.request.use((config) => {
      const token = localStorage.getItem("token");
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    this.instance.interceptors.response.use(
      (res) => res,
      async (err: AxiosError) => {
        const original = err.config as AxiosRequestConfig & { _retry?: boolean };

        if (
          err.response?.status === 403 &&
          !original._retry &&
          original.url !== "/login/auth/refresh"
        ) {
          if (this.isRefreshing) {
            return new Promise((resolve) => {
              this.refreshSubscribers.push((token) => {
                if (original.headers) {
                  original.headers.Authorization = `Bearer ${token}`;
                }
                resolve(this.instance(original));
              });
            });
          }

          original._retry = true;
          this.isRefreshing = true;

          const refreshed = await this.refreshToken();
          this.isRefreshing = false;

          if (refreshed) {
            const token = localStorage.getItem("token") || "";
            this.refreshSubscribers.forEach((cb) => cb(token));
            this.refreshSubscribers = [];

            if (original.headers) {
              original.headers.Authorization = `Bearer ${token}`;
            }

            return this.instance(original);
          } else {
            this.redirectToLogin();
            throw err;
          }
        }

        throw err;
      }
    );
  }

  private async refreshToken(): Promise<boolean> {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) return false;

    try {
      const res = await this.instance.post<AuthResponse>("/login/auth/refresh", { refreshToken });
      const { token, refreshToken: newRefreshToken, email, name } = res.data;

      localStorage.setItem("token", token);
      localStorage.setItem("refreshToken", newRefreshToken);
      localStorage.setItem("email", email);
      localStorage.setItem("name", name);

      return true;
    } catch {
      return false;
    }
  }

  public async verifyToken(): Promise<boolean> {
    const token = localStorage.getItem("token");
    if (!token) return false;

    try {
      const res = await this.instance.post<TokenVerifyResponse>("/login/auth/verify", {
        jstToken: token,
      });
      return res.data.status === "ok";
    } catch {
      return false;
    }
  }

  private redirectToLogin() {
    localStorage.clear();
    window.location.href = "/login";
  }

  public async request<T>(
    url: string,
    method: "GET" | "POST" | "PUT" | "DELETE",
    data?: any
  ): Promise<T> {
    const res = await this.instance.request<T>({ url, method, data });
    return res.data;
  }

  public async loginWithGoogle(accessToken: string): Promise<AuthResponse> {
    return this.request<AuthResponse>("/login/auth/google", "POST", { accessToken });
  }

  public async refresh(refreshToken: string): Promise<AuthResponse> {
    return this.request<AuthResponse>("/login/auth/refresh", "POST", { refreshToken });
  }
}

const apiService = new ApiService(process.env.REACT_APP_API_URL || "http://localhost:8080");

export default apiService;
