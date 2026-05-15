"use client";

import { motion } from "framer-motion";

export default function AnimatedText({ text }: { text: string }) {
  return (
    <motion.h1
      initial={{ opacity: 0, letterSpacing: "-10px" }}
      animate={{ opacity: 1, letterSpacing: "5px" }}
      transition={{ duration: 1.5, ease: "easeOut" }}
      style={{
        color: "#ffffff",
        textShadow: "0 0 10px #00f2fe, 0 0 20px #00f2fe, 0 0 40px #4facfe, 0 0 80px #00f2fe", // Glowing 9ase7
        textAlign: "center",
        fontFamily: "'Courier New', Courier, monospace", // Font Hacking/Tech
        fontWeight: 900,
        fontSize: "2.8rem",
        marginBottom: "35px",
        textTransform: "uppercase",
        letterSpacing: "5px",
      }}
    >
      {text}
    </motion.h1>
  );
}