import {
  UserResponseDto,
  UserCreateRequestDto,
  LoginRequestDto,
  LoginResponseDto,
  ApiResponseDto,
} from "./types/dtos";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";
const LOGIN_EMAIL = import.meta.env.VITE_DEMO_EMAIL ?? "admin@impulse.app";
const LOGIN_PASS = import.meta.env.VITE_DEMO_PASSWORD ?? "password";

async function ensureToken(): Promise<string> {
  let token = localStorage.getItem("token");
  if (!token) {
    const loginData: LoginRequestDto = {
      email: LOGIN_EMAIL,
      password: LOGIN_PASS,
    };

    const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loginData),
    });

    if (!res.ok) throw new Error("No se pudo iniciar sesión demo");

    const response: ApiResponseDto<LoginResponseDto> = await res.json();
    token = response.data?.token || null;

    if (token) {
      localStorage.setItem("token", token);
      if (response.data?.user) {
        localStorage.setItem("user", JSON.stringify(response.data.user));
      }
    }
  }
  return token!;
}

export async function getUsuarios(): Promise<UserResponseDto[]> {
  const token = await ensureToken();
  const res = await fetch(`${API_BASE}/api/v1/users`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Error listando usuarios");

  const response: ApiResponseDto<{ content: UserResponseDto[] }> = await res.json();
  return response.data?.content || [];
}

export async function crearUsuario(usuario: UserCreateRequestDto): Promise<UserResponseDto> {
  const token = await ensureToken();
  const res = await fetch(`${API_BASE}/api/v1/users`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(usuario),
  });
  if (!res.ok) throw new Error("Error creando usuario");

  const response: ApiResponseDto<UserResponseDto> = await res.json();
  return response.data!;
}
