import { type ClassValue, clsx } from 'clsx';

/**
 * Utility function to merge class names conditionally
 * Based on the popular cn utility pattern
 */
export function cn(...inputs: ClassValue[]) {
  return clsx(inputs);
}
