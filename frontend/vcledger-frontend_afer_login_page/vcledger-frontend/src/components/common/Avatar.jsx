import { getInitials } from '../../utils/formatters';

/**
 * Avatar
 * -------
 * Shows the customer's photo when one exists; otherwise a brass-rimmed
 * initials medallion, echoing the ledger's brass-fitting motif so the
 * "no photo yet" state still feels designed rather than like a gap.
 *
 * `size`: 'sm' | 'md' | 'lg'
 */
const SIZES = {
  sm: 'w-9 h-9 text-xs',
  md: 'w-14 h-14 text-base',
  lg: 'w-20 h-20 text-2xl',
};

export default function Avatar({ name, imageUrl, size = 'md' }) {
  const sizeClasses = SIZES[size] ?? SIZES.md;

  if (imageUrl) {
    return (
      <img
        src={imageUrl}
        alt={name}
        className={`${sizeClasses} rounded-full object-cover border-2 border-brass shadow-raised-sm`}
      />
    );
  }

  return (
    <div
      className={`${sizeClasses} rounded-full flex items-center justify-center font-display font-semibold border-2 border-brass bg-gradient-to-b from-maroon-light to-maroon text-paper-card shadow-raised-sm shrink-0`}
    >
      {getInitials(name)}
    </div>
  );
}
