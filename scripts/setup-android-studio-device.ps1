# Crea un AVD nativo de Android Studio (sin Flutter) para FinPulse.
# Ejecutar en PowerShell: .\scripts\setup-android-studio-device.ps1

$ErrorActionPreference = "Stop"
$sdk = $env:LOCALAPPDATA + "\Android\Sdk"
$avdManager = Join-Path $sdk "cmdline-tools\latest\bin\avdmanager.bat"

if (-not (Test-Path $avdManager)) {
    Write-Error "No se encontró avdmanager. Instala Android SDK Command-line Tools desde Android Studio > SDK Manager."
}

$avdName = "FinPulse_API_35"
$systemImage = "system-images;android-35;google_apis;x86_64"

Write-Host "Creando AVD '$avdName' (Pixel 6, API 35)..."
Write-Host "Si falla, instala la system image en Android Studio: SDK Manager > SDK Platforms > Android 15"
echo no | & $avdManager create avd -n $avdName -k $systemImage -d pixel_6 --force

Write-Host ""
Write-Host "Listo. En Android Studio:"
Write-Host "  1. Device Manager (icono del teléfono)"
Write-Host "  2. Deberías ver: $avdName"
Write-Host "  3. Play para iniciar, luego Run (app)"
Write-Host ""
& $avdManager list avd
