import React from "react";
import { motion } from "framer-motion";
import { User, Mail, AtSign, Shield, ShieldCheck } from "lucide-react";
import { UserResponseDto } from "../types/dtos";
import { AppCard } from "../ui/AppCard";
import { Badge } from "./ui/Badge";
import PageHeader from "./PageHeader";
import DataState from "./DataState";

interface UserListProps {
  readonly usuarios: UserResponseDto[];
}

const UserList: React.FC<UserListProps> = ({ usuarios }) => {
  if (!usuarios.length) {
    return (
      <div className="container-app space-y-8">
        <PageHeader
          title="Usuarios"
          subtitle="Gestión de usuarios de la plataforma"
        />
        <DataState
          empty={true}
          emptyTitle="No hay usuarios registrados"
          emptyDescription="Aún no hay usuarios en la plataforma. Los usuarios aparecerán aquí cuando se registren."
        />
      </div>
    );
  }

  return (
    <div className="container-app space-y-8">
      <PageHeader
        title="Usuarios"
        subtitle={`${usuarios.length} usuario${usuarios.length !== 1 ? 's' : ''} registrado${usuarios.length !== 1 ? 's' : ''} en la plataforma`}
      />

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {usuarios.map((user, index) => (
          <motion.div
            key={user.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <AppCard
              style={{
                borderRadius: "var(--radius-lg)",
                boxShadow: "var(--glass-shadow)",
                background: "rgba(var(--glass-bg), var(--glass-alpha))",
                padding: "var(--space-8)",
                minHeight: 220
              }}
              className="card-hover"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-gradient-to-r from-primary-500 to-primary-600 rounded-full flex items-center justify-center">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h3 className="heading-6 mb-1">
                      {user.firstName} {user.lastName}
                    </h3>
                    <div className="flex items-center gap-2 text-sm" style={{ color: "var(--text-2)" }}>
                      <AtSign className="w-4 h-4" />
                      <span>{user.username}</span>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col gap-2">
                  <Badge
                    variant={user.isActive ? "success" : "error"}
                    size="sm"
                    icon={user.isActive ? <ShieldCheck className="w-3 h-3" /> : <Shield className="w-3 h-3" />}
                  >
                    {user.isActive ? "Activo" : "Inactivo"}
                  </Badge>
                </div>
              </div>
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-sm" style={{ color: "var(--text-2)" }}>
                  <Mail className="w-4 h-4 text-gray-400" />
                  <span className="truncate">{user.email}</span>
                </div>
                <div className="flex items-center justify-between pt-2 border-t border-gray-100">
                  <span className="text-xs" style={{ color: "var(--text-3)", textTransform: "uppercase", letterSpacing: 1 }}>
                    Estado
                  </span>
                  <Badge
                    variant="secondary"
                    size="sm"
                  >
                    {user.status}
                  </Badge>
                </div>
              </div>
            </AppCard>
          </motion.div>
        ))}
      </div>
    </div>
  );
};

export default UserList;
