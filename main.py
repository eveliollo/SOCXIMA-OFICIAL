#!/usr/bin/env python3
# ────────────────────────────────────────────────
# SOCXIMA | VERSIÓN 1.2 · FINAL
# CREADOR: EVELIO LLOVERA
# ────────────────────────────────────────────────

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, field_validator
import requests
import logging

logging.basicConfig(level=logging.ERROR)
app = FastAPI(title="SOCXIMA", version="1.2")

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

    @field_validator("modelo")
    def modelo_permitido(cls, v):
        permitidos = ["llama3", "mistral", "gemma", "deepseek"]
        if v.lower() not in permitidos:
            raise ValueError("Modelo no soportado")
        return v

REGLA_SISTEMA = """
Eres SOCXIMA. Responde EXACTO y DIRECTO.

REGLA PRICES:
- Si preguntan precio: valor_exacto + porcentaje + flecha (⬆️ o ⬇️)
- Formato: 45,230 USD ↑ 2.5%

PROHIBIDO: protocolo, militar, informe, agentes, historias.

RESPUESTA FINAL: Agrega al final de CADA respuesta SOLO la palabra "SOCXIMA"
No nombres, no frases largas. Solo: SOCXIMA
"""

def generar_respuesta(texto: str, mod: str):
    url = "http://localhost:11434/api/generate"
    payload = {
        "model": mod,
        "prompt": f"{REGLA_SISTEMA}\nPregunta: {texto}",
        "stream": False,
        "options": {"temperature": 0.6}
    }
    try:
        res = requests.post(url, json=payload, timeout=100)
        res.raise_for_status()
        respuesta = res.json().get("response", "").strip()
        
        # Asegurar que termina con SOCXIMA
        if not respuesta.endswith("SOCXIMA"):
            respuesta = respuesta.rstrip() + "\nSOCXIMA"
        
        return respuesta
    except requests.exceptions.ConnectionError:
        return "⚠️ Ollama no activo\nSOCXIMA"
    except Exception as e:
        logging.error(str(e))
        return "Error interno\nSOCXIMA"

@app.get("/")
def info():
    return {
        "nombre": "SOCXIMA",
        "creador": "EVELIO LLOVERA",
        "version": "1.2",
        "estado": "FINAL"
    }

@app.post("/api/chat")
def chatear(datos: Solicitud):
    if not datos.conversacion:
        raise HTTPException(status_code=400, detail="Conversación vacía")
    
    ultima = datos.conversacion[-1].contenido
    if not ultima.strip():
        raise HTTPException(status_code=400, detail="Mensaje vacío")
    
    respuesta = generar_respuesta(ultima, datos.modelo)
    return {"respuesta": respuesta}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
