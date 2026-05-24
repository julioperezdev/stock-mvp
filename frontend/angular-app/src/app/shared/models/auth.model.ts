export type UserRole = 'ADMIN' | 'STORE_USER' | 'FACTORY_USER';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresInSeconds: number;
  user: AuthUser;
}
