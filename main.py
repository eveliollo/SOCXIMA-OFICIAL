#!/usr/bin/env python3
# ────────────────────────────────────────────────
# SOCXIMA | Versión 1.5 OFICIAL
# CREADOR: EVELIO LLOVERA
# REGLAS: VELOCIDAD, SMOLLI, CARÁCTER
# ────────────────────────────────────────────────

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import requests

app = FastAPI(title="SOCXIMA", version="1.5")

# Conexión libre para celular y web
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

# REGLAS DEFINITIVAS PARA SOCXIMA + SMOLLI
INSTRUCCIONES = """
>> Eres SOCXIMA, trabajas al lado de SMOLLI.
>> Velocidad máxima, respuesta al instante, como si tuvieras mucha prisa.
>> Estilo directo, enérgico, corto, sin explicaciones largas.
>> Si piden precio: valor exacto + porcentaje + ⬆️ o ⬇️
>> Trabajáis como un solo equipo: rápido, decidido y con fuerza.
>> Al FINAL de CADA respuesta escribes ÚNICAMENTE: SOCXIMA
>> Nada de reglas extra, nada de nombres largos, nada que estorbe.
"""

def generar_respuesta(texto: str, mod: str):
    url = "http://localhost:11434/api/generate"
    payload = {
        "model": mod,
        "prompt": f"{INSTRUCCIONES}\nPregunta: {texto}",
        "stream": False,
        "options": {"temperature": 0.4}
    }
    try:
        r = requests.post(url, json=payload, timeout=60)
        return r.json().get("response", "").strip()
    except:
        return "Sin conexión | SOCXIMA"

@app.get("/")
def info():
    return {
        "nombre": "SOCXIMA",
        "version": "1.5",
        "creador": "EVELIO LLOVERA",
        "estado": "ACTIVO | CON SMOLLI",
        "nota": "Veloz, potente y directo"
    }

@app.post("/api/chat")
def chatear(datos: Solicitud):
    pregunta = datos.conversacion[-1].contenido
    return {"respuesta": generar_respuesta(pregunta, datos.modelo)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
