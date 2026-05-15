"use client";

import React, { useState, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion"; 
import Background3D from "./threejs/background3D";
import AnimatedCard from "./animation/card";
import styles from "./page.module.css";

// Interface bach TypeScript y3ref chno jay
interface PartnerData {
  sheetName: string;
  totalBr: number;
}

export default function VentilPage() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [message, setMessage] = useState<{ text: string; type: "error" | "success" | null }>({ text: "", type: null });
  
  // Les states jdad dyal l'Affichage w l'Excel
  const [resultData, setResultData] = useState<PartnerData[] | null>(null);
  const [excelBase64, setExcelBase64] = useState<string | null>(null);
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setMessage({ text: "", type: null });
    }
  };

  const handleUploadClick = () => {
    if (fileInputRef.current) fileInputRef.current.click();
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage({ text: "VEUILLEZ SÉLECTIONNER UN FICHIER", type: "error" });
      return;
    }
    setLoading(true);
    setMessage({ text: "", type: null });

    const formData = new FormData();
    formData.append("file", file);

    try {
      // 🚀 VITESSE x4000: URL Relative! 
      // Next.js (b l'Proxy li drna f next.config.ts) ghadi y-rediriha l'Spring Boot
      const apiUrl = "/api/excel/process";

      const response = await fetch(apiUrl, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("ÉCHEC DU TRAITEMENT DES DONNÉES");

      const json = await response.json();
      
      setResultData(json.data);
      setExcelBase64(json.fileBase64);
      setMessage({ text: "ANALYSE TERMINÉE AVEC SUCCÈS", type: "success" });

    } catch (error: any) {
      setMessage({ text: error.message, type: "error" });
    } finally {
      setLoading(false);
    }
  };

  // Fonction bach n-decodiw l'Base64 w n-téléchargéwh kima l'3ada
  const handleDownloadExcel = () => {
    if (!excelBase64) return;
    
    const byteCharacters = atob(excelBase64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
    
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = downloadUrl;
    link.setAttribute("download", "Recap_Ventilation_OS.xlsx");
    document.body.appendChild(link);
    link.click();
    link.parentNode?.removeChild(link);
  };

  // Reset bach n3awdo l'khedma
  const handleReset = () => {
    setFile(null);
    setResultData(null);
    setExcelBase64(null);
    setMessage({ text: "", type: null });
  };

  return (
    <main className={styles.mainContainer}>
      <Background3D />

      <AnimatedCard>
        
        <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
          <h1 className={styles.kyntusTitle}>KYNTUS</h1>
          <p className={styles.subtitle}>Ventilation OS</p>
        </motion.div>
        
        {/* ILA MAZAL MA-JENA L'DATA -> AFFICHAGE UPLOAD */}
        {!resultData ? (
          <motion.div exit={{ opacity: 0, scale: 0.9 }}>
            <div className={styles.uploadBox} onClick={handleUploadClick}>
              <input ref={fileInputRef} type="file" accept=".xlsx, .xls, .csv" className={styles.fileInput} onChange={handleFileChange} />
              <div className={styles.css3dFolder}>
                <div className={styles.folderBack}></div>
                <div className={styles.folderPaper}></div>
                <div className={styles.folderFront}></div>
              </div>
              <span className={styles.uploadLabel}>{file ? "MODIFIER LA SOURCE" : "SÉLECTIONNER LE FICHIER"}</span>
            </div>

            <AnimatePresence>
              {file && (
                <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: "auto" }} exit={{ opacity: 0, height: 0 }} className={styles.selectedFile}>
                  <div className={styles.css3dFile}></div> 
                  {file.name}
                </motion.div>
              )}
            </AnimatePresence>

            <button className={styles.submitBtn} onClick={handleUpload} disabled={loading || !file}>
              <span>{loading ? "ANALYSE EN COURS..." : "LANCER L'EXTRACTION"}</span>
            </button>
          </motion.div>
        ) : (
          
          /* ILA L'DATA WESLAT -> AFFICHAGE RÉSULTATS & TABLEAU */
          <motion.div className={styles.resultsContainer} initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}>
            
            <div className={styles.tableWrapper}>
              <table className={styles.premiumTable}>
                <thead>
                  <tr>
                    <th>Partenaire</th>
                    <th>Total BR</th>
                  </tr>
                </thead>
                <tbody>
                  {resultData.map((item, idx) => (
                    <tr key={idx}>
                      <td>{item.sheetName}</td>
                      <td>
                        {item.totalBr === 0 ? "-" : new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(item.totalBr)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <button className={styles.downloadBtn} onClick={handleDownloadExcel}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
              TÉLÉCHARGER EXCEL
            </button>
            
            <div style={{ textAlign: 'center' }}>
                <button className={styles.resetBtn} onClick={handleReset}>Analyser un autre fichier</button>
            </div>

          </motion.div>
        )}

        <AnimatePresence>
          {message.type && !resultData && (
            <motion.div initial={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.8 }} className={`${styles.message} ${styles[message.type]}`}>
              {message.text}
            </motion.div>
          )}
        </AnimatePresence>

      </AnimatedCard>
    </main>
  );
}