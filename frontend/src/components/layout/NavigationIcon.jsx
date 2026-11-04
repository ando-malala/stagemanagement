const paths = {
  warning: 'M12 3 2 21h20L12 3z M12 9v5 M12 17v.1',
  arrow: 'M5 12h14 M14 7l5 5-5 5',
  refresh: 'M20 7v5h-5 M4 17v-5h5 M6 7a7 7 0 0 1 12-1l2 3 M4 15l2 3a7 7 0 0 0 12-1',
  MD: 'M4 4h16v16H4z M8 8h8 M8 12h5 M8 16h6',
  TB: 'M3 3h7v7H3z M14 3h7v7h-7z M3 14h7v7H3z M14 14h7v7h-7z',
  ST: 'M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M10 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M20 21v-2a4 4 0 0 0-3-3.87 M17 3.13a4 4 0 0 1 0 7.75',
  EN: 'M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M10 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M16 11l2 2 4-4',
  SP: 'M3 7h18v13H3z M8 7V4h8v3 M3 12a24 24 0 0 0 18 0 M10 13h4',
  RA: 'M5 3h10l4 4v14H5z M14 3v5h5 M9 12h6 M9 16h6',
  EV: 'M9 4H5v17h14V4h-4 M9 3h6v4H9z M8 14l3 3 5-6',
  SO: 'M4 5h16v12H4z M12 17v4 M8 21h8 M8 9h8 M8 13h5',
  DO: 'M3 6h7l2 3h9v11H3z M3 6V4h7l2 2h7v3',
  AL: 'M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9 M10 21h4',
  UT: 'M12 3l8 3v6c0 5-8 9-8 9s-8-4-8-9V6z M9 12l2 2 4-4',
  RE: 'M4 5h16 M4 12h16 M4 19h16 M8 3v4 M16 10v4 M9 17v4',
  JA: 'M4 4h16v17H4z M8 2v4 M16 2v4 M8 10h8 M8 14h8 M8 18h4'
};

export default function NavigationIcon({ name }) {
  return <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor"
    strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" focusable="false">
    <path d={paths[name] ?? paths.MD} />
  </svg>;
}
