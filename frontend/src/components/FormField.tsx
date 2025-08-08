import React, { ChangeEvent } from 'react';

interface FormFieldProps {
  id: string;
  label: string;
  type?: 'text' | 'email' | 'password' | 'tel' | 'url' | 'date' | 'number' | 'textarea';
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  error?: string;
  helperText?: string;
  className?: string;
  rows?: number; // Para textarea
  min?: string | number;
  max?: string | number;
  minLength?: number;
  maxLength?: number;
  pattern?: string;
  autoComplete?: string;
  'aria-describedby'?: string;
}

const FormField: React.FC<FormFieldProps> = ({
  id,
  label,
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  disabled = false,
  error,
  helperText,
  className = '',
  rows = 4,
  min,
  max,
  minLength,
  maxLength,
  pattern,
  autoComplete,
  'aria-describedby': ariaDescribedBy,
  ...rest
}) => {
  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    onChange(e.target.value);
  };

  const fieldClassName = `form-control ${error ? 'is-invalid' : ''} ${className}`.trim();
  const isTextarea = type === 'textarea';

  return (
    <div className="form-group">
      <label htmlFor={id} className="form-label">
        {label}
        {required && <span className="text-danger" aria-label="requerido"> *</span>}
      </label>
      
      {isTextarea ? (
        <textarea
          id={id}
          value={value}
          onChange={handleChange}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          className={fieldClassName}
          rows={rows}
          minLength={minLength}
          maxLength={maxLength}
          aria-describedby={ariaDescribedBy || (error ? `${id}-error` : helperText ? `${id}-help` : undefined)}
          aria-invalid={error ? 'true' : 'false'}
          {...rest}
        />
      ) : (
        <input
          id={id}
          type={type}
          value={value}
          onChange={handleChange}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          className={fieldClassName}
          min={min}
          max={max}
          minLength={minLength}
          maxLength={maxLength}
          pattern={pattern}
          autoComplete={autoComplete}
          aria-describedby={ariaDescribedBy || (error ? `${id}-error` : helperText ? `${id}-help` : undefined)}
          aria-invalid={error ? 'true' : 'false'}
          {...rest}
        />
      )}
      
      {error && (
        <div id={`${id}-error`} className="form-error" role="alert">
          {error}
        </div>
      )}
      
      {helperText && !error && (
        <div id={`${id}-help`} className="form-help-text">
          {helperText}
        </div>
      )}
    </div>
  );
};

export default FormField;
