// Igual que la versión que compilaba en Hostinger. El build usa webpack (ver package.json):
// con Next 16.4 el worker de PostCSS de Turbopack se cae en Hostinger (Node 22).
// eslint-disable-next-line import/no-anonymous-default-export
export default {
  plugins: {
    '@tailwindcss/postcss': {},
  },
}
