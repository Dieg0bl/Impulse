import React, { useState } from "react";
import { useAuth } from "../providers/AuthProvider";
import { useNavigate } from "react-router-dom";
import PageHeader from "../components/PageHeader";
import { Button } from "../components/ui/Button";

const Login: React.FC = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState("demo@impulse.test");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(email);
      navigate("/");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[70vh] flex flex-col justify-start bg-gradient-to-br from-gray-50 via-white to-primary-50 pb-20">
      <div className="container-app pt-16 max-w-md mx-auto w-full">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Iniciar Sesión</span>}
          subtitle={<span className="text-lg text-gray-600">Accede a tu cuenta para continuar con tus retos y progreso.</span>}
        />
        <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100">
          <form
            onSubmit={submit}
            className="space-y-8"
            aria-label="Formulario de inicio de sesión"
          >
            <div className="space-y-3">
              <label
                htmlFor="login-email"
                className="block text-base font-semibold text-primary-700 mb-1"
              >
                Email
              </label>
              <input
                id="login-email"
                type="email"
                required
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="block w-full text-base px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 bg-gray-50"
                aria-describedby="login-email-help"
              />
              <p id="login-email-help" className="text-sm text-gray-500">
                Usa el correo de demo para acceder rápidamente.
              </p>
            </div>

            <Button
              type="submit"
              size="lg"
              className="w-full shadow-colored px-6 py-3 text-base font-semibold"
              disabled={loading}
            >
              {loading ? "Entrando…" : "Entrar"}
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
