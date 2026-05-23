# Ejecutar FinPulse con Android Studio (sin Flutter)

FinPulse es una app **Android nativa** (Kotlin + Compose). No uses la extensión Flutter ni el emulador `flutter api35`.

## 1. Abrir el proyecto

1. Android Studio → **File → Open**
2. Carpeta: `AndroidStudioProjects/financeApp`
3. Espera a que Gradle sincronice (barra inferior)

## 2. Crear un dispositivo virtual (solo una vez)

**Opción A — Interfaz (recomendado)**

1. **Tools → Device Manager**
2. **+** → **Virtual Device**
3. Elige un teléfono (p. ej. Pixel 6) → **Next**
4. System image **API 35** (o 34) con Google APIs → **Download** si hace falta → **Next** → **Finish**
5. Nombre sugerido: `FinPulse_API_35` (evita nombres con "flutter")

**Opción B — Script**

```powershell
cd C:\Users\Usuario\AndroidStudioProjects\financeApp
.\scripts\setup-android-studio-device.ps1
```

## 3. Ejecutar la app

1. En la barra superior, configuración de ejecución: **app**
2. Dispositivo: elige tu AVD de Device Manager o un teléfono USB con depuración USB
3. Clic en **Run** (triángulo verde)

Gradle instalará `com.finpulse` en el dispositivo seleccionado.

## 4. Si usas Cursor / VS Code

- No uses **Run Flutter** ni el selector de dispositivo Flutter.
- Este repo desactiva Dart/Flutter en `.vscode/settings.json`.
- Para depurar en emulador, usa **Android Studio** o terminal:

```powershell
cd C:\Users\Usuario\AndroidStudioProjects\financeApp
.\gradlew.bat installDebug
```

(con el emulador ya iniciado desde Device Manager)

## 5. Teléfono físico

1. Activa **Opciones de desarrollador** y **Depuración USB**
2. Conecta por USB → acepta la huella RSA
3. En Android Studio, el dispositivo aparecerá en el desplegable junto a **app**

## Problema: "Unavailable on device flutter api35"

| Causa | Solución |
|--------|----------|
| Cursor/VS Code intenta ejecutar con Flutter | Abre y ejecuta desde **Android Studio** |
| Emulador Flutter apagado o incompatible | Usa **Device Manager** de Android Studio |
| Proyecto equivocado | Confirma que abriste la carpeta `financeApp` |

## SDK

Ruta local (automática en `local.properties`):

`C:\Users\Usuario\AppData\Local\Android\Sdk`
