"use client";

import { motion } from "framer-motion";
import React from "react";

export default function AnimatedCard({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", width: "100%", padding: "20px", zIndex: 10 }}>
      <motion.div
        initial={{ opacity: 0, y: 50, scale: 0.95 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.8, ease: [0.25, 1, 0.5, 1] }}
        style={{
          width: "100%",
          maxWidth: "580px",
          position: "relative",
          borderRadius: "40px", // Plus arrondi pour un look organique
          background: "rgba(255, 255, 255, 0.85)", // White Glass
          backdropFilter: "blur(40px)",
          WebkitBackdropFilter: "blur(40px)",
          border: "1px solid rgba(255, 255, 255, 1)",
          boxShadow: "0 25px 50px -12px rgba(30, 58, 138, 0.25)",
          overflow: "hidden", // Bach l'Aura matkhrejch mn l'card
        }}
      >
        {/* L'Aura 1 : Royal Blue Blob (Kayt7rek b chkel fluide) */}
        <motion.div
          animate={{
            x: [0, 50, -50, 0],
            y: [0, -30, 30, 0],
            scale: [1, 1.2, 0.8, 1],
          }}
          transition={{
            duration: 12,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          style={{
            position: "absolute",
            top: "-10%",
            left: "-10%",
            width: "300px",
            height: "300px",
            background: "rgba(37, 99, 235, 0.15)", // Royal Blue
            filter: "blur(60px)",
            borderRadius: "50%",
            pointerEvents: "none",
            zIndex: 0,
          }}
        />

        {/* L'Aura 2 : Deep Blue Blob */}
        <motion.div
          animate={{
            x: [0, -60, 40, 0],
            y: [0, 40, -40, 0],
            scale: [1, 0.9, 1.3, 1],
          }}
          transition={{
            duration: 15,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          style={{
            position: "absolute",
            bottom: "-10%",
            right: "-10%",
            width: "250px",
            height: "250px",
            background: "rgba(30, 58, 138, 0.12)", // Deep Blue
            filter: "blur(50px)",
            borderRadius: "50%",
            pointerEvents: "none",
            zIndex: 0,
          }}
        />

        {/* Contenu de la carte (Fo9 l'Aura) */}
        <div style={{ padding: "50px 40px", position: "relative", zIndex: 10 }}>
          {children}
        </div>
      </motion.div>
    </div>
  );
}