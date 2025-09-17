import React, { useState } from "react";
import { motion } from "framer-motion";
import { Calendar, Clock, User, Phone, Mail, MapPin } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/Card";
import { Button } from "./ui/Button";
import { Input } from "./ui/Input";
import FormField from "./FormField";
import PageHeader from "./PageHeader";

interface BookingFormData {
  name: string;
  email: string;
  phone: string;
  date: string;
  time: string;
  service: string;
  notes: string;
}

const BookingForm: React.FC = () => {
  const [formData, setFormData] = useState<BookingFormData>({
    name: '',
    email: '',
    phone: '',
    date: '',
    time: '',
    service: '',
    notes: ''
  });

  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (field: keyof BookingFormData) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    // Simular envío
    await new Promise(resolve => setTimeout(resolve, 2000));

    setIsSubmitting(false);
    alert('Reserva enviada correctamente. Te contactaremos pronto.');
  };

  return (
    <div className="container-narrow space-y-8">
      <PageHeader
        title="Reservar Sesión"
        subtitle="Completa el formulario para reservar tu sesión personalizada con uno de nuestros coaches."
      />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <Card className="surface-elevated">
          <CardHeader>
            <CardTitle className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-primary-500 to-primary-600 rounded-lg flex items-center justify-center">
                <Calendar className="w-5 h-5 text-white" />
              </div>
              Información de la Reserva
            </CardTitle>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid gap-6 md:grid-cols-2">
                <FormField
                  id="name"
                  label="Nombre completo"
                  required
                >
                  <Input
                    value={formData.name}
                    onChange={handleInputChange('name')}
                    placeholder="Tu nombre completo"
                    leftIcon={<User className="w-4 h-4" />}
                    required
                  />
                </FormField>

                <FormField
                  id="email"
                  label="Email"
                  required
                >
                  <Input
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    placeholder="tu@email.com"
                    leftIcon={<Mail className="w-4 h-4" />}
                    required
                  />
                </FormField>

                <FormField
                  id="phone"
                  label="Teléfono"
                  required
                >
                  <Input
                    type="tel"
                    value={formData.phone}
                    onChange={handleInputChange('phone')}
                    placeholder="+34 666 777 888"
                    leftIcon={<Phone className="w-4 h-4" />}
                    required
                  />
                </FormField>

                <FormField
                  id="service"
                  label="Tipo de servicio"
                  required
                >
                  <select
                    value={formData.service}
                    onChange={handleInputChange('service')}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white text-gray-900"
                    required
                  >
                    <option value="">Seleccionar servicio</option>
                    <option value="coaching-personal">Coaching Personal</option>
                    <option value="coaching-grupal">Coaching Grupal</option>
                    <option value="consultoria">Consultoría</option>
                    <option value="workshop">Workshop</option>
                  </select>
                </FormField>

                <FormField
                  id="date"
                  label="Fecha preferida"
                  required
                >
                  <Input
                    type="date"
                    value={formData.date}
                    onChange={handleInputChange('date')}
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />
                </FormField>

                <FormField
                  id="time"
                  label="Hora preferida"
                  required
                >
                  <select
                    value={formData.time}
                    onChange={handleInputChange('time')}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white text-gray-900"
                    required
                  >
                    <option value="">Seleccionar hora</option>
                    <option value="09:00">09:00</option>
                    <option value="10:00">10:00</option>
                    <option value="11:00">11:00</option>
                    <option value="12:00">12:00</option>
                    <option value="14:00">14:00</option>
                    <option value="15:00">15:00</option>
                    <option value="16:00">16:00</option>
                    <option value="17:00">17:00</option>
                  </select>
                </FormField>
              </div>

              <FormField
                id="notes"
                label="Notas adicionales"
                description="Comparte cualquier información adicional que pueda ser útil para preparar tu sesión."
              >
                <textarea
                  value={formData.notes}
                  onChange={handleInputChange('notes')}
                  placeholder="Describe tus objetivos, preferencias o cualquier información relevante..."
                  rows={4}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white text-gray-900 resize-none"
                />
              </FormField>

              <div className="flex flex-col sm:flex-row gap-4 pt-6 border-t border-gray-200">
                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  loading={isSubmitting}
                  className="flex-1"
                  icon={<Calendar className="w-5 h-5" />}
                >
                  {isSubmitting ? 'Enviando...' : 'Confirmar Reserva'}
                </Button>

                <Button
                  type="button"
                  variant="secondary"
                  size="lg"
                  className="sm:w-auto"
                >
                  Cancelar
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </motion.div>

      {/* Info adicional */}
      <div className="grid gap-6 md:grid-cols-3">
        <Card className="surface">
          <CardContent className="p-6 text-center">
            <div className="w-12 h-12 bg-gradient-to-r from-emerald-500 to-emerald-600 rounded-lg flex items-center justify-center mx-auto mb-4">
              <Clock className="w-6 h-6 text-white" />
            </div>
            <h3 className="heading-6 mb-2">Respuesta Rápida</h3>
            <p className="text-sm text-gray-600">Te contactaremos en menos de 24 horas para confirmar tu reserva.</p>
          </CardContent>
        </Card>

        <Card className="surface">
          <CardContent className="p-6 text-center">
            <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg flex items-center justify-center mx-auto mb-4">
              <MapPin className="w-6 h-6 text-white" />
            </div>
            <h3 className="heading-6 mb-2">Modalidad Flexible</h3>
            <p className="text-sm text-gray-600">Sesiones presenciales u online según tus preferencias.</p>
          </CardContent>
        </Card>

        <Card className="surface">
          <CardContent className="p-6 text-center">
            <div className="w-12 h-12 bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg flex items-center justify-center mx-auto mb-4">
              <User className="w-6 h-6 text-white" />
            </div>
            <h3 className="heading-6 mb-2">Coaches Certificados</h3>
            <p className="text-sm text-gray-600">Profesionales con experiencia y certificaciones reconocidas.</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default BookingForm;
