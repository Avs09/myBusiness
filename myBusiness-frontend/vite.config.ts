// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import { fileURLToPath } from 'url';
import { visualizer } from 'rollup-plugin-visualizer';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Extrae nombre de paquete para dividir vendor chunks
function getPackageName(id: string): string | null {
  const nm = 'node_modules/';
  const idx = id.indexOf(nm);
  if (idx === -1) return null;
  let pkgPath = id.slice(idx + nm.length);
  const parts = pkgPath.split('/');
  if (parts[0].startsWith('@') && parts.length > 1) {
    return `${parts[0]}/${parts[1]}`;
  }
  return parts[0];
}

export default defineConfig({
  plugins: [
    react(),
    visualizer({
      filename: 'bundle-stats.html',
      open: false, // true si quieres abrirlo tras build
      gzipSize: true,
      brotliSize: true,
    }),
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  build: {
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            const pkg = getPackageName(id);
            if (pkg) {
              return `vendor_${pkg.replace('@', '').replace('/', '_')}`;
            }
          }
        },
      },
    },
  },
});
