
import React, { useState } from "react";
import { useAuth } from "../providers/AuthProvider";
import { useNavigate } from "react-router-dom";
import PageHeader from "../components/PageHeader";
import FormField from "../components/FormField";
import { Button } from "../components/ui";
import { v4 as uuidv4 } from "uuid";
import LoadingSpinner from "../components/LoadingSpinner";

const Register: React.FC = () => {
  const { register } = useAuth();
  const [name, setName] = useState("Demo");
  const [email, setEmail] = useState("demo@impulse.test");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<{ code?: string; message: string; correlationId?: string } | null>(null);
  const [idempotencyKey, setIdempotencyKey] = useState(uuidv4());
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      // Suponiendo que register puede aceptar headers, si no, adaptar provider
      await register(name, email, {
        headers: {
          "Idempotency-Key": idempotencyKey,
        },
      });
      setIdempotencyKey(uuidv4());
      navigate("/");
    } catch (err: any) {
      if (err?.response?.data && typeof err.response.data === "object") {
        setError(err.response.data);
      } else {
        setError({ message: err.message || "Error al registrar" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-16 max-w-md mx-auto w-full">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Registro</span>}
          subtitle={<span className="text-lg text-gray-600">Crea tu cuenta para comenzar a participar en retos</span>}
        />
        <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100">
          <form onSubmit={submit} className="space-y-6" aria-label="Formulario de registro" aria-busy={loading} aria-describedby={error ? "register-error" : undefined}>
            <FormField
              id="reg-name"
              label={<span className="text-base font-semibold text-primary-700">Nombre</span>}
              required
            >
              <input
                id="reg-name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="block w-full text-base px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 bg-gray-50"
                required
                aria-required="true"
                aria-invalid={!!error}
              />
            </FormField>

            <FormField
              id="reg-email"
              label={<span className="text-base font-semibold text-primary-700">Email</span>}
              required
            >
              <input
                id="reg-email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="block w-full text-base px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 bg-gray-50"
                required
                aria-required="true"
                aria-invalid={!!error}
              />
            </FormField>

            <Button
              type="submit"
              size="lg"
              className="w-full shadow-colored px-6 py-3 text-base font-semibold flex items-center justify-center"
              disabled={loading}
              aria-disabled={loading}
            >
              {loading ? <LoadingSpinner size={20} aria-label="Creando cuenta..." /> : "Crear cuenta"}
            </Button>
          </form>
          {error && (
            <div id="register-error" className="mt-4 text-red-600" role="alert">
              {error.code && <span className="font-mono text-xs bg-red-100 px-2 py-1 rounded mr-2">{error.code}</span>}
              {error.message}
              {error.correlationId && <span className="block text-xs text-gray-400 mt-1">ID: {error.correlationId}</span>}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Register;
