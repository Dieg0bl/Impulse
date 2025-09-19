import React from 'react';
import { Loader2 } from 'lucide-react';

type SpinnerSize = number | 'sm' | 'md' | 'lg';

const sizeMap: Record<'sm' | 'md' | 'lg', number> = {
	sm: 16,
	md: 24,
	lg: 40
};

const LoadingSpinner: React.FC<{ size?: SpinnerSize; className?: string }> = ({ size = 'md', className = '' }) => {
	const numericSize = typeof size === 'number' ? size : sizeMap[size];
	return (
		<span role="status" aria-live="polite" className={className}>
			<Loader2 className="animate-spin" width={numericSize} height={numericSize} />
			<span className="sr-only">Cargando…</span>
		</span>
	);
};

export { LoadingSpinner };
export default LoadingSpinner;
