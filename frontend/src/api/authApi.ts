import axios from "axios";

export interface AuthResponse {
  token: string;
  username: string;
}

export const authApi = {
  async register(username: string, password: string): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>("/api/auth/register", {
      username,
      password,
    });
    return response.data;
  },
  async login(username: string, password: string): Promise<AuthResponse> {
    const response = await axios.post<AuthResponse>("/api/auth/login", {
      username,
      password,
    });
    return response.data;
  },
};
