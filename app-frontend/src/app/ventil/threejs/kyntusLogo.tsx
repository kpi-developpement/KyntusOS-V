"use client";

import { Canvas, useFrame } from "@react-three/fiber";
import { Text } from "@react-three/drei";
import { useState, useRef, Suspense } from "react";
import * as THREE from "three";

function SlicedText() {
  const [hovered, setHovered] = useState(false);
  
  const topRef = useRef<THREE.Mesh>(null);
  const midRef = useRef<THREE.Mesh>(null);
  const botRef = useRef<THREE.Mesh>(null);

  useFrame((state, delta) => {
    const targetOffset = hovered ? 0.5 : 0;
    const speed = 10; 
    
    if (topRef.current) {
      topRef.current.position.x = THREE.MathUtils.lerp(topRef.current.position.x, -targetOffset, delta * speed);
      topRef.current.position.y = THREE.MathUtils.lerp(topRef.current.position.y, targetOffset * 0.1, delta * speed);
    }
    if (botRef.current) {
      botRef.current.position.x = THREE.MathUtils.lerp(botRef.current.position.x, targetOffset, delta * speed);
      botRef.current.position.y = THREE.MathUtils.lerp(botRef.current.position.y, -targetOffset * 0.1, delta * speed);
    }
    if (midRef.current) {
      midRef.current.position.x = THREE.MathUtils.lerp(midRef.current.position.x, targetOffset * 0.1, delta * speed);
      midRef.current.scale.setScalar(hovered ? 1.05 : 1);
    }
  });

  const fontProps = {
    font: "https://fonts.gstatic.com/s/montserrat/v25/JTUHjIg1_i6t8kCHKm4532VJOt5-QNF37w.ttf",
    fontSize: 3, // Kberna l'font
    letterSpacing: 0.1,
    fontWeight: 900,
  };

  return (
    <group 
      onPointerOver={() => {
        setHovered(true);
        document.body.style.cursor = 'pointer';
      }} 
      onPointerOut={() => {
        setHovered(false);
        document.body.style.cursor = 'auto';
      }}
    >
      {/* Deep Blue Color */}
      <Text ref={topRef} {...fontProps} color="#1e3a8a" clipRect={[-10, 0.4, 10, 10]}>KYNTUS</Text>
      
      {/* Wst kaytbeddel lounou */}
      <Text ref={midRef} {...fontProps} color={hovered ? "#2563eb" : "#1e3a8a"} clipRect={[-10, -0.4, 10, 0.4]}>KYNTUS</Text>
      
      <Text ref={botRef} {...fontProps} color="#1e3a8a" clipRect={[-10, -10, 10, -0.4]}>KYNTUS</Text>
    </group>
  );
}

export default function KyntusLogo() {
  return (
    <div style={{ width: "100%", height: "140px", marginBottom: "15px", position: "relative" }}>
      <Canvas camera={{ position: [0, 0, 7], fov: 45 }}>
        <ambientLight intensity={2.5} />
        <Suspense fallback={null}>
          <SlicedText />
        </Suspense>
      </Canvas>
    </div>
  );
}