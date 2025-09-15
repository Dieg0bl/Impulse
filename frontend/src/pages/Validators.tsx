import React, { useState, useEffect } from "react";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { Input } from "../components/ui/Input";
import { validatorApi } from "../services/api";
import { ValidatorResponseDto, PageRequestDto } from "../types/dtos";
import { ValidatorStatus, ValidatorSpecialty } from "../types/enums";
import { Search, Star, Award, CheckCircle } from "lucide-react";

const Validators: React.FC = () => {
  const [validators, setValidators] = useState<ValidatorResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedSpecialty, setSelectedSpecialty] = useState<ValidatorSpecialty | "ALL">("ALL");

  useEffect(() => {
    loadValidators();
  }, []);

  const loadValidators = async () => {
    try {
      setLoading(true);
      const params: PageRequestDto = { page: 0, size: 20 };
      const response = await validatorApi.getValidators(params);
      setValidators(response.content);
    } catch (error) {
      console.error("Error loading validators:", error);
    } finally {
      setLoading(false);
    }
  };

  const filteredValidators = validators.filter(validator => {
    const matchesSearch = validator.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         validator.specialty.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesSpecialty = selectedSpecialty === "ALL" || validator.specialty === selectedSpecialty;
    return matchesSearch && matchesSpecialty;
  });

  const getStatusBadge = (status: ValidatorStatus) => {
    const variants: Record<ValidatorStatus, "success" | "warning" | "error" | "secondary"> = {
      [ValidatorStatus.ACTIVE]: "success",
      [ValidatorStatus.INACTIVE]: "warning",
      [ValidatorStatus.SUSPENDED]: "error",
      [ValidatorStatus.TRAINING]: "secondary"
    };

    return <Badge variant={variants[status]}>{status}</Badge>;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-blue-900 to-indigo-900 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center text-white">Cargando validadores...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-blue-900 to-indigo-900 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-4">Validadores</h1>
          <p className="text-gray-300 text-lg">
            Conecta con validadores expertos para verificar tus evidencias
          </p>
        </div>

        {/* Filters */}
        <div className="mb-8 grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <Input
              placeholder="Buscar validadores..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>

          <select
            value={selectedSpecialty}
            onChange={(e) => setSelectedSpecialty(e.target.value as ValidatorSpecialty | "ALL")}
            className="px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
          >
            <option value="ALL">Todas las especialidades</option>
            {Object.values(ValidatorSpecialty).map(specialty => (
              <option key={specialty} value={specialty}>{specialty}</option>
            ))}
          </select>
        </div>

        {/* Validators Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredValidators.map((validator) => (
            <Card key={validator.id} className="bg-gray-800/50 border-gray-700 hover:border-blue-500 transition-all">
              <div className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                      <Award className="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-white">Validador #{validator.id}</h3>
                      <p className="text-sm text-gray-400">{validator.specialty}</p>
                    </div>
                  </div>
                  {getStatusBadge(validator.status)}
                </div>

                <div className="space-y-3">
                  <div className="flex items-center space-x-2">
                    <Star className="w-4 h-4 text-yellow-500" />
                    <span className="text-sm text-gray-300">
                      {validator.averageScore?.toFixed(1) || "N/A"} ({validator.validationCount || 0} validaciones)
                    </span>
                  </div>

                  <div className="flex items-center space-x-2">
                    <CheckCircle className="w-4 h-4 text-green-500" />
                    <span className="text-sm text-gray-300">
                      {validator.successRate ? `${(validator.successRate * 100).toFixed(1)}% éxito` : "Nuevo validador"}
                    </span>
                  </div>
                </div>

                <div className="mt-6 pt-4 border-t border-gray-700">
                  <Button
                    variant="primary"
                    size="sm"
                    className="w-full"
                    disabled={validator.status !== ValidatorStatus.ACTIVE}
                  >
                    {validator.status === ValidatorStatus.ACTIVE ? "Solicitar validación" : "No disponible"}
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {filteredValidators.length === 0 && (
          <div className="text-center text-gray-400 py-12">
            <Award className="w-16 h-16 mx-auto mb-4 opacity-50" />
            <p className="text-lg">No se encontraron validadores</p>
            <p className="text-sm">Intenta ajustar los filtros de búsqueda</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Validators;
