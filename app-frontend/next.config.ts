import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // L'Herba hna: Configuration dyal Reverse Proxy
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        // F l'Prod Docker ghadi y3tih BACKEND_URL (http://backend:8081)
        // F l'Local ghadi y-fall-back l localhost:8081
        destination: `${process.env.BACKEND_URL || "http://localhost:8081"}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;