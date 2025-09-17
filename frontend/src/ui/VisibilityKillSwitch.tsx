// VisibilityKillSwitch - Control de privacidad global según addenda IMPULSE
import React, { useState } from 'react'
// Eliminado MUI: Box, Typography, Dialog, DialogTitle, DialogContent, DialogActions, Alert
import { AppButton } from './AppButton'
import { GlassSurface } from './GlassSurface'
import { EyeOff, Shield, RotateCcw, AlertTriangle } from 'lucide-react'

export interface VisibilityKillSwitchProps {
  onConfirm: () => void
  onRevert?: () => void
  isHidden?: boolean
  loading?: boolean
  className?: string
}

export const VisibilityKillSwitch: React.FC<VisibilityKillSwitchProps> = ({
  onConfirm,
  onRevert,
  isHidden = false,
  loading = false,
  className
}) => {
  const [showConfirmDialog, setShowConfirmDialog] = useState(false)
  const [showRevertDialog, setShowRevertDialog] = useState(false)

  const handleHideClick = () => {
    setShowConfirmDialog(true)
  }

  const handleRevertClick = () => {
    setShowRevertDialog(true)
  }

  const handleConfirmHide = () => {
    setShowConfirmDialog(false)
    onConfirm()
  }

  const handleConfirmRevert = () => {
    setShowRevertDialog(false)
    onRevert?.()
  }

  if (isHidden) {
    return (
      <GlassSurface
        elevation={1}
        className={className}
        sx={{
          p: 3,
          backgroundColor: 'rgba(var(--color-warning), 0.1)',
          border: '1px solid rgba(var(--color-warning), 0.3)'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 16 }}>
          <EyeOff className="text-warning" size={24} />
          <span style={{ fontWeight: 600, color: 'var(--color-warning)' }}>Contenido oculto</span>
        </div>
        <div style={{ marginBottom: 24, color: 'var(--text-2)', fontSize: 15 }}>
          Todo tu contenido público está oculto. Solo tú y tus validadores pueden verlo. Puedes revertir esta acción cuando quieras.
        </div>

        <AppButton
          variant="outlined"
          color="warning"
          onClick={handleRevertClick}
          disabled={loading}
          loading={loading}
          icon={<RotateCcw size={16} />}
        >
          Hacer visible de nuevo
        </AppButton>

        {/* Dialog de confirmación para revertir */}
        {showRevertDialog && (
          <div className="modal-backdrop">
            <div className="modal-dialog" role="dialog" aria-modal="true" aria-labelledby="revert-title">
              <div className="modal-header" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <RotateCcw size={20} />
                <span id="revert-title" style={{ fontWeight: 600 }}>Hacer contenido visible</span>
              </div>
              <div className="modal-body" style={{ marginBottom: 16 }}>
                <div style={{ marginBottom: 12 }}>
                  ¿Estás seguro de que quieres hacer visible todo tu contenido público otra vez?
                </div>
                <div className="alert-info" style={{ background: 'rgba(var(--color-info),0.08)', color: 'var(--color-info)', borderRadius: 8, padding: 12, marginBottom: 8 }}>
                  Tu contenido volverá a ser visible según la configuración de visibilidad de cada reto (público/privado).
                </div>
              </div>
              <div className="modal-actions" style={{ display: 'flex', gap: 12, justifyContent: 'flex-end', padding: 16 }}>
                <AppButton variant="outlined" onClick={() => setShowRevertDialog(false)}>Cancelar</AppButton>
                <AppButton variant="contained" color="primary" onClick={handleConfirmRevert}>Sí, hacer visible</AppButton>
              </div>
            </div>
          </div>
        )}
      </GlassSurface>
    )
  }

  return (
    <>
      <GlassSurface elevation={1} className={className} sx={{ p: 3 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 16 }}>
          <Shield className="text-primary" size={24} />
          <span style={{ fontWeight: 600 }}>Control de privacidad</span>
        </div>
        <div style={{ marginBottom: 24, color: 'var(--text-2)', fontSize: 15 }}>
          Oculta de inmediato todo tu contenido público. Solo tú y tus validadores podrán verlo. Puedes revertirlo cuando quieras.
        </div>

        <AppButton
          variant="outlined"
          color="secondary"
          onClick={handleHideClick}
          disabled={loading}
          loading={loading}
          icon={<EyeOff size={16} />}
        >
          Ocultar todo ahora
        </AppButton>
      </GlassSurface>

      {/* Dialog de confirmación para ocultar */}
      {showConfirmDialog && (
        <div className="modal-backdrop">
          <div className="modal-dialog" role="dialog" aria-modal="true" aria-labelledby="hide-title">
            <div className="modal-header" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <AlertTriangle size={20} className="text-warning" />
              <span id="hide-title" style={{ fontWeight: 600 }}>Ocultar todo el contenido</span>
            </div>
            <div className="modal-body" style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 12 }}>
                ¿Estás seguro de que quieres ocultar todo tu contenido público?
              </div>
              <div className="alert-warning" style={{ background: 'rgba(var(--color-warning),0.08)', color: 'var(--color-warning)', borderRadius: 8, padding: 12, marginBottom: 8 }}>
                <strong>Esto ocultará inmediatamente:</strong>
                <ul style={{ margin: '8px 0', paddingLeft: '20px' }}>
                  <li>Todos tus retos públicos</li>
                  <li>Tus evidencias y progresos</li>
                  <li>Tu perfil público</li>
                  <li>Tus logros y estadísticas</li>
                </ul>
              </div>
              <div className="alert-info" style={{ background: 'rgba(var(--color-info),0.08)', color: 'var(--color-info)', borderRadius: 8, padding: 12 }}>
                <strong>Podrás revertir esta acción</strong> cuando quieras desde este mismo panel. Tus validadores seguirán teniendo acceso para validar tus evidencias.
              </div>
            </div>
            <div className="modal-actions" style={{ display: 'flex', gap: 12, justifyContent: 'flex-end', padding: 16 }}>
              <AppButton variant="outlined" onClick={() => setShowConfirmDialog(false)}>Cancelar</AppButton>
              <AppButton variant="contained" color="warning" onClick={handleConfirmHide} icon={<EyeOff size={16} />}>Sí, ocultar todo</AppButton>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default VisibilityKillSwitch
