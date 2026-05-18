import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        // Direct local backend pour la démo
        destination: "http://localhost:8081/api/:path*",
      },
    ];
  },
};

export default nextConfig;