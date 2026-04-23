# ADR: Migrar Generacion de Codigo de Barras a ZXing

## Metadata

- id: `ADR-001`
- fecha: `2026-04-22`
- estado: `accepted`

## Contexto

La implementacion anterior dependia de una fuente `Code 128`, logica manual embebida en la UI y render de texto sobre imagen, lo que hacia dificil mantener soporte correcto para EAN-8, EAN-13 y UPC-A.

## Decision

Adoptar ZXing como motor unico de generacion de codigos de barras y encapsularlo detras de `BarcodeFacade`.

## Consecuencias

### Positivas

- separacion clara entre UI y generacion;
- soporte nativo de formatos estandar;
- fallback controlado a `CODE128`;
- menor dependencia de fuentes para renderizar barras.

### Negativas

- se agregan dos jars al proyecto;
- la validacion visual depende ahora de PNGs generados por ZXing.

## Alternativas Consideradas

- mantener fuente `Code 128` y corregir validaciones manuales;
- crear una implementacion propia de barras.

## Referencias

- feature: [../features/barcode-zxing-refactor.md](../features/barcode-zxing-refactor.md)
- tarea: [../tasks/barcode-zxing-migration.md](../tasks/barcode-zxing-migration.md)
