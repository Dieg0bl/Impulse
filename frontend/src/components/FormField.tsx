import React from 'react';

interface FormFieldProps {
  id: string;
  label: React.ReactNode;
  hint?: React.ReactNode;
  description?: React.ReactNode;
  error?: string;
  children: React.ReactElement;
  required?: boolean;
  className?: string;
}

/**
 * FormField gestiona etiqueta, hint, error y asociación accesible.
 * Pasa automáticamente id/aria-describedby al child si admite props.
 */
const FormField: React.FC<FormFieldProps> = ({ id, label, hint, description, error, children, required, className }) => {
  const describedBy: string[] = [];
  if (hint) describedBy.push(`${id}-hint`);
  if (description) describedBy.push(`${id}-desc`);
  if (error) describedBy.push(`${id}-error`);

  const child = React.cloneElement(children, {
    id,
    'aria-invalid': !!error || undefined,
    'aria-describedby': describedBy.length ? describedBy.join(' ') : undefined,
  });

  return (
    <div className={`space-y-1 ${className || ''}`}>
      <label htmlFor={id} className="block text-sm font-medium text-gray-700 dark:text-gray-300">
        {label} {required && <span className="text-error-600" aria-hidden>*</span>}
      </label>
      {description && (
        <p id={`${id}-desc`} className="text-xs text-gray-500">{description}</p>
      )}
      {child}
      {hint && !error && (
        <p id={`${id}-hint`} className="text-xs text-gray-500">{hint}</p>
      )}
      {error && (
        <p id={`${id}-error`} className="text-xs text-error-600">{error}</p>
      )}
    </div>
  );
};

export default FormField;
