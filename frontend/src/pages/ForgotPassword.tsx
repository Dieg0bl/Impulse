import React, { useState } from "react";
import { authApi } from "../services/api";
import { ForgotPasswordRequestDto } from "../types/dtos";

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const req: ForgotPasswordRequestDto = { email };
      const msg = await authApi.forgotPassword(req);
      setMessage(msg || "Si el email existe, recibirás instrucciones para restablecer tu contraseña.");
    } catch (err: any) {
      setError(err.message || "Error al solicitar recuperación de contraseña");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-12 p-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">¿Olvidaste tu contraseña?</h2>
      <form onSubmit={handleSubmit}>
        <label className="block mb-2 font-medium">Correo electrónico</label>
        <input
          type="email"
          className="w-full border px-3 py-2 rounded mb-4"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded font-semibold disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Enviando..." : "Enviar instrucciones"}
        </button>
      </form>
      {message && <div className="mt-4 text-green-600">{message}</div>}
      {error && <div className="mt-4 text-red-600">{error}</div>}
    </div>
  );
};

export default ForgotPassword;
