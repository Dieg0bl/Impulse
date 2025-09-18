import React, { useState } from "react";
import { authApi } from "../services/api";
import { VerifyEmailRequestDto } from "../types/dtos";

const VerifyEmail: React.FC = () => {
  const [token, setToken] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const req: VerifyEmailRequestDto = { token };
      const msg = await authApi.verifyEmail(req);
      setMessage(msg || "Email verificado correctamente. Ya puedes iniciar sesión.");
    } catch (err: any) {
      setError(err.message || "Error al verificar el email");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-12 p-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Verificar email</h2>
      <form onSubmit={handleSubmit}>
        <label className="block mb-2 font-medium">Token recibido por email</label>
        <input
          type="text"
          className="w-full border px-3 py-2 rounded mb-4"
          value={token}
          onChange={e => setToken(e.target.value)}
          required
        />
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded font-semibold disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Verificando..." : "Verificar email"}
        </button>
      </form>
      {message && <div className="mt-4 text-green-600">{message}</div>}
      {error && <div className="mt-4 text-red-600">{error}</div>}
    </div>
  );
};

export default VerifyEmail;
