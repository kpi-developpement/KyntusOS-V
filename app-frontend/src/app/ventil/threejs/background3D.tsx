"use client";

import { Canvas, useFrame } from "@react-three/fiber";
import { useMemo, useRef } from "react";
import * as THREE from "three";

function RoyalBlueWave() {
  const count = 320;
  const separation = 1.5;
  const pointsRef = useRef<THREE.Points>(null);

  const positions = useMemo(() => {
    const pos = new Float32Array(count * count * 3);
    let i = 0;
    for (let ix = 0; ix < count; ix++) {
      for (let iz = 0; iz < count; iz++) {
        pos[i] = ix * separation - (count * separation) / 2;
        pos[i + 1] = 0;
        pos[i + 2] = iz * separation - (count * separation) / 2;
        i += 3;
      }
    }
    return pos;
  }, [count, separation]);

  useFrame((state) => {
    const time = state.clock.getElapsedTime() * 0.6; // Vitesse t9ila w relax
    if (pointsRef.current) {
      const pos = pointsRef.current.geometry.attributes.position.array as Float32Array;
      let i = 0;
      for (let ix = 0; ix < count; ix++) {
        for (let iz = 0; iz < count; iz++) {
          pos[i + 1] = Math.sin((ix * 0.1) + time) * 2 + Math.cos((iz * 0.1) + time) * 2;
          i += 3;
        }
      }
      pointsRef.current.geometry.attributes.position.needsUpdate = true;
    }
  });

  return (
    <points ref={pointsRef} position={[0, -8, -40]}>
      <bufferGeometry>
        <bufferAttribute attach="attributes-position" count={positions.length / 3} array={positions} itemSize={3} />
      </bufferGeometry>
      {/* Royal Blue color l'mouja */}
      <pointsMaterial size={0.15} color="#2563eb" transparent opacity={0.6} sizeAttenuation />
    </points>
  );
}

export default function Background3D() {
  return (
    <div style={{ position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh", zIndex: -1, background: "#f8fafc" }}>
      <Canvas camera={{ position: [0, 10, 30], fov: 60 }}>
        {/* Fog byed bach y3ti profondeur w y-doub m3a l'background */}
        <fog attach="fog" args={["#7a9af1", 20, 80]} />
        <RoyalBlueWave />
      </Canvas>
    </div>
  );
}