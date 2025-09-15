import React, { useState } from "react";
import { useAuth } from "../providers/AuthProvider";
import { useNavigate } from "react-router-dom";

const Login: React.FC = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState("demo@impulse.test");
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    await login(email);
    navigate("/");
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Iniciar sesión</h2>
      <form onSubmit={submit}>
        <div>
          <label htmlFor="login-email">Email</label>
          <input
            id="login-email"
            aria-label="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div style={{ marginTop: 10 }}>
          <button type="submit">Entrar</button>
        </div>
      </form>
    </div>
  );
};

export default Login;
