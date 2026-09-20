/**
 * Tailwind design tokens for VCLedger.
 *
 * The visual language is "the shopkeeper's ledger book" — a maroon cloth-bound
 * bahi-khata with brass corner fittings, cream paper pages, and stamped ink
 * headings. Every color, radius and shadow below exists to serve that idea,
 * not as generic defaults, so change them here rather than in components.
 */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        paper: {
          DEFAULT: '#F6ECD9', // open ledger page
          card: '#FFFBF0', // raised paper card (receipts, inputs)
          line: '#E4D3AE', // faint rule lines on the page
        },
        maroon: {
          DEFAULT: '#7A2E2E', // ledger cloth cover
          dark: '#551F1F',
          light: '#9A4444',
        },
        brass: {
          DEFAULT: '#C9962C', // corner fittings, embossed accents
          light: '#E3B655',
          dark: '#9C7420',
        },
        ink: {
          DEFAULT: '#2B241C', // primary text, like iron-gall ink
          soft: '#6B5D48',
        },
        credit: {
          DEFAULT: '#1F6F6B', // customer is in credit / advance paid
          soft: '#E4F1EF',
        },
        debit: {
          DEFAULT: '#B3401A', // customer owes money, record button
          soft: '#FBEAE1',
        },
      },
      fontFamily: {
        display: ['"Fraunces"', 'serif'],
        body: ['"Manrope"', 'sans-serif'],
      },
      boxShadow: {
        // Raised paper / embossed button — the core skeuomorphic surface.
        raised: '0 1px 0 rgba(255,255,255,0.6) inset, 0 -1px 0 rgba(122,46,46,0.08) inset, 0 8px 16px -6px rgba(43,36,28,0.25)',
        'raised-sm': '0 1px 0 rgba(255,255,255,0.5) inset, 0 4px 8px -3px rgba(43,36,28,0.22)',
        // Pressed / inset surface — text inputs, active tabs.
        pressed: '0 2px 4px rgba(43,36,28,0.18) inset, 0 1px 0 rgba(255,255,255,0.4)',
        brassRing: '0 0 0 2px rgba(201,150,44,0.55)',
      },
      backgroundImage: {
        cloth: 'radial-gradient(circle at 30% 20%, rgba(255,255,255,0.06), transparent 45%), linear-gradient(160deg, #7A2E2E 0%, #5E2323 100%)',
      },
      keyframes: {
        pulseRing: {
          '0%': { transform: 'scale(1)', opacity: '0.55' },
          '100%': { transform: 'scale(1.9)', opacity: '0' },
        },
        stamp: {
          '0%': { transform: 'scale(1.15)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
      },
      animation: {
        pulseRing: 'pulseRing 1.6s cubic-bezier(0.2,0.6,0.4,1) infinite',
        stamp: 'stamp 0.18s ease-out',
      },
    },
  },
  plugins: [],
};
