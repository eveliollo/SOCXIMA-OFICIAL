#!/usr/bin/env python3
# ────────────────────────────────────────────────
# SOCXIMA | CREADOR: EVELIO LLOVERA
# VERSIÓN: 1.1 · SEGURA · LIMPIA
# BASE: ODYSSEUS · LICENCIA MIT
# ────────────────────────────────────────────────

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, field_validator
import requests
import logging

# Configuración básica
logging.basicConfig(level=logging.ERROR)
app = FastAPI(title="SOCXIMA", version="1.1")

# Permitir conexión con app celular
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Estructura
class Mensaje(BaseModel):
    rol: str
    contenido: str

class Solicitud(BaseModel):
    conversacion: list[Mensaje]
    modelo: str = "llama3"

    # Solo permitir modelos válidos
    @field_validator("modelo")
    def modelo_permitido(cls, v):
        permitidos = ["llama3", "mistral", "gemma", "deepseek"]
        if v.lower() not in permitidos:
            raise ValueError("Modelo no soportado")
        return v

# Regla fija, bien guardada
REGLA_SISTEMA = """
Eres SOCXIMA, creado por EVELIO LLOVERA.
Responde CORTO y DIRECTO.
Si te piden precio: monto + variación nada más.
PROHIBIDO: palabras como protocolo, militar, informe, agentes.
Tu nombre y creador no cambian jamás.
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
        return res.json().get("response", "").strip()
    except requests.exceptions.ConnectionError:
        return "⚠️ Ollama no está activo"
    except Exception as e:
        logging.error(str(e))
        return "Error interno, intenta otra vez"

# Rutas
@app.get("/")
def info():
    return {
        "nombre": "SOCXIMA",
        "creador": "EVELIO LLOVERA",
        "version": "1.1",
        "estado": "ACTIVO · SEGURO",
        "nota": "Código mejorado, firmado y registrado"
    }

@app.post("/api/chat")
def chatear(datos: Solicitud):
    ultima = datos.conversacion[-1].contenido
    respuesta = generar_respuesta(ultima, datos.modelo)
    return {"respuesta": respuesta}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
