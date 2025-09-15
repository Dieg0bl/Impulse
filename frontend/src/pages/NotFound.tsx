import React from "react";
import { Link } from "react-router-dom";
import { Button } from "../components/ui/Button";

const NotFound: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex items-center justify-center p-6">
      <div className="max-w-md w-full text-center">
        <div className="mb-8">
          <h1 className="text-9xl font-bold text-white/20 mb-4">404</h1>
          <h2 className="text-2xl font-bold text-white mb-2">Página no encontrada</h2>
          <p className="text-gray-300">
            La página que buscas no existe o ha sido movida.
          </p>
        </div>

        <div className="space-y-4">
          <Link to="/">
            <Button variant="primary" size="lg" className="w-full">
              Volver al inicio
            </Button>
          </Link>

          <Link to="/challenges">
            <Button variant="secondary" size="lg" className="w-full">
              Ver desafíos
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
