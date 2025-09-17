import React, { useState } from "react";
import { useAuth } from "../providers/AuthProvider";
import { useNavigate } from "react-router-dom";
import PageHeader from "../components/PageHeader";
import FormField from "../components/FormField";
import { Button } from "../components/ui";

const Register: React.FC = () => {
  const { register } = useAuth();
  const [name, setName] = useState("Demo");
  const [email, setEmail] = useState("demo@impulse.test");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await register(name, email);
      navigate("/");
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
          <form onSubmit={submit} className="space-y-6" aria-label="Formulario de registro">
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
              />
            </FormField>

            <Button
              type="submit"
              size="lg"
              className="w-full shadow-colored px-6 py-3 text-base font-semibold"
              disabled={loading}
            >
              {loading ? "Creando cuenta..." : "Crear cuenta"}
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Register;
