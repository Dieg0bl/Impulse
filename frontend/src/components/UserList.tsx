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
                boxShadow: "0 2px 8px rgba(0,0,0,0.04)",
                background: "rgb(var(--surface-2))",
                border: "1px solid rgb(var(--surface-3))",
                padding: "var(--space-8)",
                minHeight: 220
              }}
              className="card-hover"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div style={{ width: 48, height: 48, background: 'linear-gradient(90deg, rgb(var(--color-primary)), rgb(var(--color-primary-dark)))', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <User style={{ width: 24, height: 24, color: 'rgb(var(--text-inverse))' }} />
                  </div>
                  <div>
                    <h3 className="heading-6 mb-1">
                      {user.firstName} {user.lastName}
                    </h3>
                    <div className="flex items-center gap-2 text-sm" style={{ color: 'rgb(var(--text-2))' }}>
                      <AtSign style={{ width: 16, height: 16, color: 'rgb(var(--text-3))' }} />
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
                <div className="flex items-center gap-2 text-sm" style={{ color: 'rgb(var(--text-2))' }}>
                  <Mail style={{ width: 16, height: 16, color: 'rgb(var(--text-3))' }} />
                  <span className="truncate">{user.email}</span>
                </div>
                <div className="flex items-center justify-between pt-2" style={{ borderTop: '1px solid rgb(var(--surface-3))' }}>
                  <span className="text-xs" style={{ color: 'rgb(var(--text-3))', textTransform: 'uppercase', letterSpacing: 1 }}>
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
