// ParentalConsentGate - Control de menores según addenda IMPULSE
import React, { useState } from 'react'
import { Box, Typography, TextField, FormControlLabel, Checkbox, Alert, Stepper, Step, StepLabel, Card, CardContent } from '@mui/material'
import { AppButton } from './AppButton'
import { GlassSurface } from './GlassSurface'
import { Shield, Users, Lock, AlertTriangle, CheckCircle, Mail, User } from 'lucide-react'

export interface ParentalConsentData {
  userAge: number
  parentName: string
  parentEmail: string
  relationToChild: string
  consentGiven: boolean
  privacyPolicyAccepted: boolean
  dataProcessingAccepted: boolean
}

export interface ParentalConsentGateProps {
  userAge: number
  onConsentGiven: (data: ParentalConsentData) => void
  onConsentDenied: () => void
  loading?: boolean
  className?: string
}

export const ParentalConsentGate: React.FC<ParentalConsentGateProps> = ({
  userAge,
  onConsentGiven,
  onConsentDenied,
  loading = false,
  className
}) => {
  const [step, setStep] = useState(0)
  const [formData, setFormData] = useState<Partial<ParentalConsentData>>({
    userAge,
    parentName: '',
    parentEmail: '',
    relationToChild: '',
    consentGiven: false,
    privacyPolicyAccepted: false,
    dataProcessingAccepted: false
  })

  const isUnder14 = userAge < 14
  const isMinor = userAge < 18
  const needsParentalConsent = isUnder14

  const steps = needsParentalConsent 
    ? ['Verificación de edad', 'Datos del tutor/padre', 'Consentimientos']
    : ['Verificación de edad', 'Configuración de privacidad']

  const validateStep = (stepIndex: number) => {
    switch (stepIndex) {
      case 0: return true // Age verification
      case 1: 
        if (needsParentalConsent) {
          return formData.parentName && formData.parentEmail && formData.relationToChild
        }
        return true
      case 2:
        if (needsParentalConsent) {
          return formData.consentGiven && formData.privacyPolicyAccepted && formData.dataProcessingAccepted
        }
        return true
      default: return false
    }
  }

  const handleNext = () => {
    if (validateStep(step)) {
      setStep(step + 1)
    }
  }

  const handleBack = () => {
    setStep(Math.max(0, step - 1))
  }

  const handleComplete = () => {
    if (needsParentalConsent) {
      onConsentGiven(formData as ParentalConsentData)
    } else {
      // Para menores 14-17, automáticamente configurar privacidad OFF
      onConsentGiven({
        ...formData,
        consentGiven: true,
        privacyPolicyAccepted: true,
        dataProcessingAccepted: true
      } as ParentalConsentData)
    }
  }

  const renderAgeVerification = () => (
    <GlassSurface elevation={1} sx={{ p: 4, textAlign: 'center' }}>
      <AlertTriangle size={48} className="text-warning mx-auto mb-4" />
      <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
        Verificación de edad
      </Typography>
      
      {isUnder14 ? (
        <>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Has indicado que tienes <strong>{userAge} años</strong>. 
            Para usar IMPULSE necesitamos el consentimiento de tus padres o tutores.
          </Typography>
          <Alert severity="info" sx={{ mb: 3 }}>
            <strong>Según la normativa de protección de menores:</strong><br />
            Los menores de 14 años necesitan autorización parental para crear cuentas 
            y compartir información en plataformas digitales.
          </Alert>
        </>
      ) : (
        <>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Has indicado que tienes <strong>{userAge} años</strong>. 
            Configuraremos tu cuenta con la máxima privacidad por defecto.
          </Typography>
          <Alert severity="info" sx={{ mb: 3 }}>
            <strong>Protección especial para menores:</strong><br />
            Tu perfil será privado por defecto. Podrás cambiar la configuración 
            de privacidad cuando seas mayor de edad.
          </Alert>
        </>
      )}

      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mt: 4 }}>
        <AppButton
          variant="outlined"
          onClick={onConsentDenied}
          disabled={loading}
        >
          Mi edad es incorrecta
        </AppButton>
        <AppButton
          variant="contained"
          onClick={handleNext}
          disabled={loading}
          icon={<Shield size={16} />}
        >
          Continuar
        </AppButton>
      </Box>
    </GlassSurface>
  )

  const renderParentInfo = () => (
    <GlassSurface elevation={1} sx={{ p: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Users size={24} className="text-primary" />
        <Typography variant="h6" sx={{ fontWeight: 600 }}>
          Datos del padre/madre/tutor
        </Typography>
      </Box>

      <Alert severity="warning" sx={{ mb: 3 }}>
        Necesitamos contactar con un padre, madre o tutor legal para autorizar 
        el uso de la plataforma.
      </Alert>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
        <TextField
          fullWidth
          label="Nombre completo del padre/madre/tutor *"
          value={formData.parentName}
          onChange={(e) => setFormData({...formData, parentName: e.target.value})}
          placeholder="Ej: María García López"
          InputProps={{
            startAdornment: <User size={16} className="mr-2 text-text-3" />
          }}
        />

        <TextField
          fullWidth
          type="email"
          label="Email del padre/madre/tutor *"
          value={formData.parentEmail}
          onChange={(e) => setFormData({...formData, parentEmail: e.target.value})}
          placeholder="Ej: maria.garcia@email.com"
          InputProps={{
            startAdornment: <Mail size={16} className="mr-2 text-text-3" />
          }}
          helperText="Enviaremos un email de verificación a esta dirección"
        />

        <TextField
          fullWidth
          label="Relación contigo *"
          value={formData.relationToChild}
          onChange={(e) => setFormData({...formData, relationToChild: e.target.value})}
          placeholder="Ej: Madre, Padre, Tutor legal"
        />
      </Box>

      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'space-between', mt: 4 }}>
        <AppButton
          variant="outlined"
          onClick={handleBack}
          disabled={loading}
        >
          Atrás
        </AppButton>
        <AppButton
          variant="contained"
          onClick={handleNext}
          disabled={!validateStep(1) || loading}
        >
          Continuar
        </AppButton>
      </Box>
    </GlassSurface>
  )

  const renderConsents = () => (
    <GlassSurface elevation={1} sx={{ p: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Lock size={24} className="text-primary" />
        <Typography variant="h6" sx={{ fontWeight: 600 }}>
          Consentimientos requeridos
        </Typography>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        Estos consentimientos son obligatorios según la normativa de protección 
        de datos y menores.
      </Alert>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <Card variant="outlined">
          <CardContent>
            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.consentGiven || false}
                  onChange={(e) => setFormData({...formData, consentGiven: e.target.checked})}
                />
              }
              label={
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    Autorizo el uso de IMPULSE por parte del menor
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Confirmo que soy el padre/madre/tutor legal y autorizo 
                    el uso de la plataforma IMPULSE.
                  </Typography>
                </Box>
              }
            />
          </CardContent>
        </Card>

        <Card variant="outlined">
          <CardContent>
            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.privacyPolicyAccepted || false}
                  onChange={(e) => setFormData({...formData, privacyPolicyAccepted: e.target.checked})}
                />
              }
              label={
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    He leído y acepto la Política de Privacidad
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    <a href="/privacy" target="_blank" className="text-primary underline">
                      Leer Política de Privacidad
                    </a> - Especial para menores de edad
                  </Typography>
                </Box>
              }
            />
          </CardContent>
        </Card>

        <Card variant="outlined">
          <CardContent>
            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.dataProcessingAccepted || false}
                  onChange={(e) => setFormData({...formData, dataProcessingAccepted: e.target.checked})}
                />
              }
              label={
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    Autorizo el tratamiento de datos del menor
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Solo se procesarán los datos mínimos necesarios 
                    para el funcionamiento de la plataforma.
                  </Typography>
                </Box>
              }
            />
          </CardContent>
        </Card>
      </Box>

      <Alert severity="success" sx={{ mt: 3 }}>
        <Typography variant="body2">
          <strong>Protección garantizada:</strong> El perfil será privado por defecto 
          y solo los validadores autorizados podrán ver el contenido del menor.
        </Typography>
      </Alert>

      <Box sx={{ display: 'flex', gap: 2, justifyContent: 'space-between', mt: 4 }}>
        <AppButton
          variant="outlined"
          onClick={handleBack}
          disabled={loading}
        >
          Atrás
        </AppButton>
        <AppButton
          variant="contained"
          color="success"
          onClick={handleComplete}
          disabled={!validateStep(2) || loading}
          loading={loading}
          icon={<CheckCircle size={16} />}
        >
          Completar autorización
        </AppButton>
      </Box>
    </GlassSurface>
  )

  const renderPrivacyConfig = () => (
    <GlassSurface elevation={1} sx={{ p: 4, textAlign: 'center' }}>
      <CheckCircle size={48} className="text-success mx-auto mb-4" />
      <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
        Configuración de privacidad
      </Typography>
      
      <Typography variant="body1" sx={{ mb: 3 }}>
        Tu cuenta se ha configurado con la máxima privacidad por ser menor de edad.
      </Typography>

      <Alert severity="success" sx={{ mb: 3 }}>
        <strong>Configuración automática aplicada:</strong>
        <ul style={{ margin: '8px 0', paddingLeft: '20px', textAlign: 'left' }}>
          <li>Perfil privado por defecto</li>
          <li>Retos no visibles públicamente</li>
          <li>Solo validadores autorizados pueden ver tu contenido</li>
          <li>Máxima protección de datos personales</li>
        </ul>
      </Alert>

      <AppButton
        variant="contained"
        onClick={handleComplete}
        disabled={loading}
        loading={loading}
        icon={<CheckCircle size={16} />}
      >
        Continuar a IMPULSE
      </AppButton>
    </GlassSurface>
  )

  return (
    <Box className={className} sx={{ maxWidth: 600, mx: 'auto', p: 2 }}>
      <Stepper activeStep={step} alternativeLabel sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      {step === 0 && renderAgeVerification()}
      {step === 1 && needsParentalConsent && renderParentInfo()}
      {step === 1 && !needsParentalConsent && renderPrivacyConfig()}
      {step === 2 && needsParentalConsent && renderConsents()}
    </Box>
  )
}

export default ParentalConsentGate
