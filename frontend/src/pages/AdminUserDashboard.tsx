import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import CRMService, { UserProfile, UserLifecycleStage, UserSegment, Campaign, SupportTicket } from "../services/CRMService";
import PageHeader from "../components/PageHeader";
import { Badge } from "../components/ui/Badge";

const lifecycleColors: Record<UserLifecycleStage, string> = {
  new: "bg-blue-100 text-blue-800",
  onboarding: "bg-cyan-100 text-cyan-800",
  active: "bg-green-100 text-green-800",
  at_risk: "bg-yellow-100 text-yellow-800",
  churned: "bg-red-100 text-red-800",
  reactivated: "bg-purple-100 text-purple-800",
};

const AdminUserDashboard: React.FC = () => {
  const [users, setUsers] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulación: cargar 6 perfiles demo
    const demoUsers: UserProfile[] = [
      {
        id: "1",
        email: "alice@impulse.app",
        name: "Alice Demo",
        createdAt: new Date("2025-01-01"),
        lastActive: new Date(),
        stage: "active",
        segment: "power_users",
        tags: ["engaged", "premium"],
        isPremium: true,
        isCoach: false,
        supportLevel: "priority",
      },
      {
        id: "2",
        email: "bob@impulse.app",
        name: "Bob Demo",
        createdAt: new Date("2025-02-01"),
        lastActive: new Date(),
        stage: "onboarding",
        segment: "newcomers",
        tags: ["trial"],
        isPremium: false,
        isCoach: false,
        supportLevel: "standard",
      },
      {
        id: "3",
        email: "carol@impulse.app",
        name: "Carol Demo",
        createdAt: new Date("2025-03-01"),
        lastActive: new Date(),
        stage: "at_risk",
        segment: "at_risk",
        tags: ["inactive"],
        isPremium: false,
        isCoach: false,
        supportLevel: "standard",
      },
      {
        id: "4",
        email: "dan@impulse.app",
        name: "Dan Demo",
        createdAt: new Date("2025-04-01"),
        lastActive: new Date(),
        stage: "churned",
        segment: "churned",
        tags: ["churned"],
        isPremium: false,
        isCoach: false,
        supportLevel: "standard",
      },
      {
        id: "5",
        email: "eve@impulse.app",
        name: "Eve Demo",
        createdAt: new Date("2025-05-01"),
        lastActive: new Date(),
        stage: "reactivated",
        segment: "reactivated",
        tags: ["winback"],
        isPremium: true,
        isCoach: false,
        supportLevel: "priority",
      },
      {
        id: "6",
        email: "coach@impulse.app",
        name: "Coach Demo",
        createdAt: new Date("2025-06-01"),
        lastActive: new Date(),
        stage: "active",
        segment: "coaches",
        tags: ["coach"],
        isPremium: true,
        isCoach: true,
        supportLevel: "vip",
      },
    ];
    setUsers(demoUsers);
    setLoading(false);
  }, []);

  if (loading) {
    return <div className="p-10 text-center text-gray-500">Cargando usuarios...</div>;
  }

  return (
    <div className="container-app py-10 space-y-8 max-w-7xl mx-auto">
      <PageHeader
        title={<span className="font-extrabold text-primary-700 tracking-tight">CRM & Gestión de Usuarios</span>}
        subtitle="Administra el ciclo de vida, segmentación, campañas y soporte de usuarios."
      />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {users.map((user) => (
          <motion.div
            key={user.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6 flex flex-col gap-2"
          >
            <div className="flex items-center justify-between mb-2">
              <div>
                <div className="font-bold text-lg text-gray-900">{user.name}</div>
                <div className="text-xs text-gray-500">{user.email}</div>
              </div>
              <Badge variant={user.isPremium ? "success" : "secondary"} size="sm">
                {user.isPremium ? "Premium" : "Free"}
              </Badge>
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span className={`px-2 py-1 rounded-lg font-semibold ${lifecycleColors[user.stage]}`}>{user.stage.replace('_', ' ')}</span>
              <span className="px-2 py-1 rounded-lg bg-gray-100 text-gray-700">{user.segment}</span>
              {user.isCoach && <span className="px-2 py-1 rounded-lg bg-indigo-100 text-indigo-700">Coach</span>}
            </div>
            <div className="flex flex-wrap gap-1 mt-2">
              {user.tags.map((tag) => (
                <span key={tag} className="px-2 py-1 rounded bg-primary-100 text-primary-700 text-xs">{tag}</span>
              ))}
            </div>
            <div className="flex items-center justify-between mt-4 text-xs">
              <span className="text-gray-500">Soporte: <span className="font-semibold text-gray-700">{user.supportLevel}</span></span>
              <span className="text-gray-400">Última actividad: {user.lastActive.toLocaleDateString()}</span>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
};

export default AdminUserDashboard;
