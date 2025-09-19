
import React, { useState } from "react";
import { authApi } from "../services/api";
import { VerifyEmailRequestDto } from "../types/dtos";
import { v4 as uuidv4 } from "uuid";
import LoadingSpinner from "../components/LoadingSpinner";

const VerifyEmail: React.FC = () => {
  const [token, setToken] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState<{ code?: string; message: string; correlationId?: string } | null>(null);
  const [loading, setLoading] = useState(false);
  const [idempotencyKey, setIdempotencyKey] = useState(uuidv4());

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setMessage("");
    try {
      const req: VerifyEmailRequestDto = { token };
      // Enviar Idempotency-Key en headers si la API lo soporta
      const msg = await authApi.verifyEmail(req, {
        headers: {
          "Idempotency-Key": idempotencyKey,
        },
      });
      setMessage(msg || "Email verificado correctamente. Ya puedes iniciar sesión.");
      setIdempotencyKey(uuidv4()); // Nueva key para siguiente submit
    } catch (err: any) {
      // Normalizar error
      if (err?.response?.data && typeof err.response.data === "object") {
        setError(err.response.data);
      } else {
        setError({ message: err.message || "Error al verificar el email" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-12 p-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Verificar email</h2>
      <form onSubmit={handleSubmit} aria-busy={loading} aria-describedby={error ? "verify-email-error" : undefined}>
        <label className="block mb-2 font-medium" htmlFor="verify-token">Token recibido por email</label>
        <input
          id="verify-token"
          type="text"
          className="w-full border px-3 py-2 rounded mb-4"
          value={token}
          onChange={e => setToken(e.target.value)}
          required
          aria-required="true"
          aria-invalid={!!error}
        />
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded font-semibold disabled:opacity-60 flex items-center justify-center"
          disabled={loading}
          aria-disabled={loading}
        >
          {loading ? <LoadingSpinner size={20} aria-label="Verificando..." /> : "Verificar email"}
        </button>
      </form>
      {message && <div className="mt-4 text-green-600" role="status">{message}</div>}
      {error && (
        <div id="verify-email-error" className="mt-4 text-red-600" role="alert">
          {error.code && <span className="font-mono text-xs bg-red-100 px-2 py-1 rounded mr-2">{error.code}</span>}
          {error.message}
          {error.correlationId && <span className="block text-xs text-gray-400 mt-1">ID: {error.correlationId}</span>}
        </div>
      )}
    </div>
  );
};

export default VerifyEmail;
