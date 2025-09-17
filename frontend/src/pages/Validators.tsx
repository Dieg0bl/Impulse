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
      <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
        <div className="container-app pt-10 max-w-5xl mx-auto">
          <div className="text-center">
            <div className="text-2xl font-bold text-primary-700">Cargando validadores...</div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-5xl mx-auto">
        {/* Header */}
        <div className="mb-10 text-center">
          <h1 className="text-4xl font-extrabold text-primary-700 tracking-tight mb-4">Validadores</h1>
          <p className="text-lg text-gray-600">
            Conecta con validadores expertos para verificar tus evidencias
          </p>
        </div>

        {/* Filters */}
        <div className="mb-10 bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <Input
                placeholder="Buscar validadores..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 bg-gray-50 text-base"
              />
            </div>

            <select
              value={selectedSpecialty}
              onChange={(e) => setSelectedSpecialty(e.target.value as ValidatorSpecialty | "ALL")}
              className="px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg text-base font-medium focus:ring-2 focus:ring-primary-500"
            >
              <option value="ALL">Todas las especialidades</option>
              {Object.values(ValidatorSpecialty).map(specialty => (
                <option key={specialty} value={specialty}>{specialty}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Validators Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredValidators.map((validator) => (
            <Card key={validator.id} className="bg-white shadow-lg border border-gray-100 hover:shadow-xl transition-all">
              <div className="p-6">
                <div className="flex items-start justify-between mb-6">
                  <div className="flex items-center space-x-3">
                    <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                      <Award className="w-6 h-6 text-primary-600" />
                    </div>
                    <div>
                      <h3 className="font-bold text-gray-900 text-base">Validador #{validator.id}</h3>
                      <p className="text-sm text-gray-600">{validator.specialty}</p>
                    </div>
                  </div>
                  {getStatusBadge(validator.status)}
                </div>

                <div className="space-y-4">
                  <div className="flex items-center space-x-2">
                    <Star className="w-5 h-5 text-yellow-500" />
                    <span className="text-sm text-gray-700 font-medium">
                      {validator.averageScore?.toFixed(1) || "N/A"} ({validator.validationCount || 0} validaciones)
                    </span>
                  </div>

                  <div className="flex items-center space-x-2">
                    <CheckCircle className="w-5 h-5 text-green-500" />
                    <span className="text-sm text-gray-700 font-medium">
                      {validator.successRate ? `${(validator.successRate * 100).toFixed(1)}% éxito` : "Nuevo validador"}
                    </span>
                  </div>
                </div>

                <div className="mt-6 pt-4 border-t border-gray-200">
                  <Button
                    variant="primary"
                    size="sm"
                    className="w-full shadow-colored"
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
          <div className="text-center py-16">
            <Award className="w-16 h-16 mx-auto mb-4 text-gray-400" />
            <p className="text-lg font-semibold text-gray-600 mb-2">No se encontraron validadores</p>
            <p className="text-sm text-gray-500">Intenta ajustar los filtros de búsqueda</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Validators;
