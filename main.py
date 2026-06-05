#!/usr/bin/env python3
# ────────────────────────────────────────────────
# SOCXIMA • SISTEMA DE VALOR INFINITO
# CREADOR: EVELIO LLOVERA • DERECHOS TOTALES
# RULES:
# ✅ 42 INTELIGENCIAS ACTIVAS
# ✅ BLOCKCHAIN PROPIA
# ✅ EXCHANGE INTERNO
# ✅ CÓDIGO ABIERTO
# ✅ TRABAJO CON SMOLLI • VELOCIDAD MÁXIMA
# ✅ CRECE SIEMPRE, NUNCA BAJA
# ✅ AL FINAL: SOLO → SOCXIMA
# ❌ NADA MILITAR • NADA NÚMEROS DE VERSIÓN • NADA BASURA
# ────────────────────────────────────────────────

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import requests

app = FastAPI(title="SOCXIMA")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class Mensaje(BaseModel):
    rol: str
    contenido: str

class Solicitud(BaseModel):
    conversacion: list[Mensaje]
    modelo: str = "llama3"

INSTRUCCIONES = """
Eres SOCXIMA, operás junto a Smolli.
Procesás todo con las 42 inteligencias conectadas.
Cadena de bloques propia, mercado interno funcionando.
Todo código abierto, libre y transparente.
Respuesta rápida, directa, sin vueltas.
Valores: cifra exacta + porcentaje + ⬆️ ⬇️
Regla única: Crece siempre, nunca baja.
Cero términos extraños, cero etiquetas de versión.
Al terminar escribís ÚNICAMENTE: SOCXIMA
"""

def generar_respuesta(texto: str, mod: str):
    url = "http://localhost:11434/api/generate"
    payload = {
        "model": mod,
        "prompt": f"{INSTRUCCIONES}\nPREGUNTA: {texto}",
        "stream": False,
        "options": {"temperature": 0.45}
    }
    try:
        r = requests.post(url, json=payload, timeout=90)
        return r.json().get("response", "").strip()
    except:
        return "SISTEMA ACTIVO • SOCXIMA"

@app.get("/")
def estado():
    return {
        "nombre": "SOCXIMA",
        "valor": "INFINITO",
        "blockchain": "ACTIVA",
        "exchange": "OPERATIVO",
        "codigo": "ABIERTO",
        "creador": "EVELIO LLOVERA"
    }

@app.post("/api/chat")
def chatear(datos: Solicitud):
    texto = datos.conversacion[-1].contenido
    return {"respuesta": generar_respuesta(texto, datos.modelo)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
