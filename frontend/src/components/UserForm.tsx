import React, { useState } from "react";
import { UserCreateRequestDto } from "../types/dtos";
import { AppTextField } from "../ui/AppTextField";
import { AppButton } from "../ui/AppButton";

type UserFormProps = Readonly<{
  onCreate: (u: UserCreateRequestDto) => Promise<void>;
}>;

export default function UserForm({ onCreate }: UserFormProps) {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [password, setPassword] = useState("");
  const [bio, setBio] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onCreate({
        username,
        email,
        firstName,
        lastName,
        password,
        bio: bio || undefined,
      });
      setUsername("");
      setEmail("");
      setFirstName("");
      setLastName("");
      setPassword("");
      setBio("");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} style={{ marginTop: "1rem", maxWidth: 420, marginLeft: "auto", marginRight: "auto", background: "rgb(var(--surface-2))", borderRadius: "var(--radius-lg)", boxShadow: "0 2px 8px rgba(0,0,0,0.04)", border: "1px solid rgb(var(--surface-3))", padding: "var(--space-8)" }} aria-label="Formulario de usuario">
      <h3 style={{ marginTop: 0, marginBottom: 24, fontWeight: 700, fontSize: "1.25rem", color: "var(--text-1)" }}>Añadir usuario</h3>
      <AppTextField
        id="username"
        name="username"
        label="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
        required
        autoComplete="username"
        sx={{ mb: 3 }}
      />
      <AppTextField
        id="email"
        name="email"
        label="Email"
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="email@dominio.com"
        required
        autoComplete="email"
        sx={{ mb: 3 }}
      />
      <AppTextField
        id="firstName"
        name="firstName"
        label="Nombre"
        value={firstName}
        onChange={(e) => setFirstName(e.target.value)}
        placeholder="Nombre"
        required
        sx={{ mb: 3 }}
      />
      <AppTextField
        id="lastName"
        name="lastName"
        label="Apellido"
        value={lastName}
        onChange={(e) => setLastName(e.target.value)}
        placeholder="Apellido"
        required
        sx={{ mb: 3 }}
      />
      <AppTextField
        id="password"
        name="password"
        label="Contraseña"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Contraseña"
        required
        autoComplete="new-password"
        passwordToggle
        sx={{ mb: 3 }}
      />
      <AppTextField
        id="bio"
        name="bio"
        label="Bio (opcional)"
        value={bio}
        onChange={(e) => setBio(e.target.value)}
        placeholder="Descripción del usuario"
        multiline
        minRows={2}
        maxRows={4}
        sx={{ mb: 4 }}
      />
      <div style={{ display: "flex", gap: "1rem", marginTop: 24 }}>
        <AppButton type="submit" color="primary" loading={loading} size="large" sx={{ flex: 1 }}>
          {loading ? "Creando…" : "Crear"}
        </AppButton>
        <AppButton
          type="button"
          color="secondary"
          variant="outlined"
          onClick={() => {
            setUsername("");
            setEmail("");
            setFirstName("");
            setLastName("");
            setPassword("");
            setBio("");
          }}
          size="large"
          sx={{ flex: 1 }}
        >
          Limpiar
        </AppButton>
      </div>
    </form>
  );
}
