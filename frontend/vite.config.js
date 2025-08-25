import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    resolve: {
        extensions: ['.tsx', '.ts', '.jsx', '.js']
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false
            }
        }
    },
    build: {
        rollupOptions: {
            onwarn: function (warning, warn) {
                if (warning.code === 'UNUSED_EXTERNAL_IMPORT') return;
                warn(warning);
            }
        }
    },
    esbuild: {
        loader: 'tsx',
        include: /\.[jt]sx?$/,
        exclude: []
    }
});
