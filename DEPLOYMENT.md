# 📱 Guía de Despliegue - SOCXIMA

## **Versión Final 1.1**

---

### **1. Linux/Mac/Windows**

```bash
# Clonar
git clone https://github.com/eveliollo/SOCXIMA-OFICIAL.git
cd SOCXIMA-OFICIAL

# Instalar
pip install -r requirements.txt

# Ejecutar
python main.py
```

### **2. Docker**

```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY main.py .
CMD ["python", "main.py"]
```

**Build & Run:**
```bash
docker build -t socxima:1.1 .
docker run -p 8080:8080 socxima:1.1
```

### **3. Android APK**

Requisitos: Buildozer, Java 11+, Android SDK

```bash
buildozer android debug
```

APK generado en: `bin/socxima-1.1-debug.apk`

### **4. Systemd (Linux Automático)**

Crear `/etc/systemd/system/socxima.service`:

```ini
[Unit]
Description=SOCXIMA Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/SOCXIMA-OFICIAL
ExecStart=/usr/bin/python3 main.py
Restart=always

[Install]
WantedBy=multi-user.target
```

Activar:
```bash
sudo systemctl daemon-reload
sudo systemctl enable socxima
sudo systemctl start socxima
```

---

**SOCXIMA | Creado por Evelio Llovera**
