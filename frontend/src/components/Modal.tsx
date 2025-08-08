import * as React from 'react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;

  return (
    <button
      type="button"
      className="modal-overlay"
      aria-label="Cerrar modal"
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000
      }}
      onClick={onClose}
      onKeyDown={e => {
        if (e.key === 'Enter' || e.key === ' ' || e.key === 'Escape') {
          onClose();
        }
      }}
      tabIndex={0}
    >
      <div
        className="modal-content"
        style={{
          backgroundColor: '#fff',
          borderRadius: '8px',
          maxWidth: '500px',
          width: '90%',
          maxHeight: '80vh',
          overflow: 'auto',
          boxShadow: '0 4px 20px rgba(0, 0, 0, 0.15)'
        }}
        onClick={e => e.stopPropagation()}
      >
        <div
          className="modal-header"
          style={{
            padding: '1rem 1.5rem',
            borderBottom: '1px solid #e9ecef',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}
        >
          <h3 style={{ margin: 0, color: '#2c3e50' }}>{title}</h3>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              fontSize: '1.5rem',
              cursor: 'pointer',
              color: '#6c757d'
            }}
            aria-label="Cerrar modal"
          >
            ×
          </button>
        </div>
        <div className="modal-body">
          {children}
        </div>
      </div>
    </button>
  );
};

export default Modal;
