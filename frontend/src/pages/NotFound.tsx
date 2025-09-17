
import React from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { Home, Search, ArrowLeft } from "lucide-react";
import PageHeader from "../components/PageHeader";

const NotFound: React.FC = () => {
  return (
    <main className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-primary-50 flex flex-col items-center justify-center py-16" aria-label="Página no encontrada">
      <div className="container mx-auto px-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-center max-w-2xl mx-auto"
        >
          <PageHeader
            title={
              <span className="text-4xl font-extrabold text-primary-700 tracking-tight">
                Página no encontrada
              </span>
            }
            subtitle={
              <span className="text-gray-600 text-lg leading-relaxed">
                La página que buscas no existe, ha sido movida o no tienes permisos para acceder.
              </span>
            }
            actions={null}
            className="mb-12"
          />

          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ duration: 0.5, delay: 0.2 }}
            className="mb-12"
          >
            <div
              aria-hidden
              className="text-[12rem] font-bold bg-gradient-to-r from-primary-200 to-primary-400 bg-clip-text text-transparent select-none leading-none mb-4"
            >
              404
            </div>
            <div className="w-24 h-1 bg-gradient-to-r from-primary-500 to-primary-600 rounded-full mx-auto"></div>
          </motion.div>

          <motion.nav
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
            aria-label="Acciones de recuperación"
            className="flex flex-col sm:flex-row gap-4 justify-center max-w-md mx-auto"
          >
            <Link to="/" className="flex-1">
              <button className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-primary-500 text-white font-semibold rounded-xl hover:bg-primary-600 transition-all duration-200 hover:shadow-lg" aria-label="Volver al inicio">
                <Home className="w-5 h-5" />
                Volver al inicio
              </button>
            </Link>
            <Link to="/challenges" className="flex-1">
              <button className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 hover:border-gray-400 transition-all duration-200" aria-label="Ver desafíos">
                <Search className="w-5 h-5" />
                Ver desafíos
              </button>
            </Link>
          </motion.nav>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5, delay: 0.6 }}
            className="mt-8"
          >
            <button
              onClick={() => window.history.back()}
              className="flex items-center gap-2 text-gray-500 hover:text-primary-600 transition-colors duration-200 mx-auto"
            >
              <ArrowLeft className="w-4 h-4" />
              <span className="text-sm">Regresar a la página anterior</span>
            </button>
          </motion.div>
        </motion.div>
      </div>
    </main>
  );
};

export default NotFound;
