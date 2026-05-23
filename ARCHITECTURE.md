# FinPulse Android (financeApp)

App fintech offline-first en Kotlin + Jetpack Compose + Room + Hilt.

## Capas

- `data/` — Room (SQLite), DataStore, repositorios, seed demo
- `domain/` — modelos, contratos de repositorio, casos de uso
- `ui/` — Compose, navegación, MVVM con ViewModels Hilt
- `di/` — módulos Hilt

## Base de datos (v1)

- `accounts`, `categories`, `transactions`
- `financial_insights`, `alerts`
- Importes en centavos (`amount_minor`)

## i18n

`values` (es), `values-en`, `values-pt`, `values-fr`, `values-de`

## Ejecutar en Android Studio

Ver [ANDROID_STUDIO.md](ANDROID_STUDIO.md) — dispositivos del **Device Manager** (no emulador Flutter).

## Próximos pasos (Etapa 4)

- CRUD de movimientos
- Presupuestos, metas, deudas
- Gráficos y exportación PDF
- Pantalla de inteligencia completa
