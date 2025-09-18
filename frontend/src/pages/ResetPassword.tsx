import React, { useState } from "react";
import { authApi } from "../services/api";
import { ResetPasswordRequestDto } from "../types/dtos";

const ResetPassword: React.FC = () => {
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const req: ResetPasswordRequestDto = { token, newPassword };
      const msg = await authApi.resetPassword(req);
      setMessage(msg || "Contraseña restablecida correctamente. Ya puedes iniciar sesión.");
    } catch (err: any) {
      setError(err.message || "Error al restablecer la contraseña");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-12 p-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Restablecer contraseña</h2>
      <form onSubmit={handleSubmit}>
        <label className="block mb-2 font-medium">Token recibido por email</label>
        <input
          type="text"
          className="w-full border px-3 py-2 rounded mb-4"
          value={token}
          onChange={e => setToken(e.target.value)}
          required
        />
        <label className="block mb-2 font-medium">Nueva contraseña</label>
        <input
          type="password"
          className="w-full border px-3 py-2 rounded mb-4"
          value={newPassword}
          onChange={e => setNewPassword(e.target.value)}
          required
        />
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded font-semibold disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Restableciendo..." : "Restablecer contraseña"}
        </button>
      </form>
      {message && <div className="mt-4 text-green-600">{message}</div>}
      {error && <div className="mt-4 text-red-600">{error}</div>}
    </div>
  );
};

export default ResetPassword;
