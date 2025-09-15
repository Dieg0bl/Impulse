import React, { useState } from "react";
import { useAuth } from "../providers/AuthProvider";
import { useNavigate } from "react-router-dom";

const Register: React.FC = () => {
  const { register } = useAuth();
  const [name, setName] = useState("Demo");
  const [email, setEmail] = useState("demo@impulse.test");
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    await register(name, email);
    navigate("/");
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Registro</h2>
      <form onSubmit={submit}>
        <div>
          <label htmlFor="reg-name">Nombre</label>
          <input
            id="reg-name"
            aria-label="Nombre"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="reg-email">Email</label>
          <input
            id="reg-email"
            aria-label="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div style={{ marginTop: 10 }}>
          <button type="submit">Crear cuenta</button>
        </div>
      </form>
    </div>
  );
};

export default Register;
