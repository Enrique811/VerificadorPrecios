# Spec Kit

## Objetivo

Esta carpeta queda organizada para trabajar especificaciones funcionales y tecnicas del proyecto con una estructura estable: contexto actual, requerimientos, features, decisiones, tareas y plantillas.

## Estructura

```text
spec/
|-- README.md
|-- arquitectura.md
|-- tecnologias.md
|-- flujo-principal.md
|-- current-state/
|   `-- README.md
|-- requirements/
|   `-- README.md
|-- features/
|   `-- README.md
|-- decisions/
|   `-- README.md
|-- tasks/
|   `-- README.md
`-- templates/
    |-- feature-spec.md
    |-- adr.md
    `-- task-plan.md
```

## Uso Recomendado

1. Mantener el analisis del sistema actual en los documentos raiz de `spec/`.
2. Usar `current-state/` como indice y futura zona de consolidacion.
3. Definir necesidades de negocio o sistema en `requirements/`.
4. Crear una especificacion por iniciativa en `features/`.
5. Registrar decisiones importantes en `decisions/`.
6. Bajar la ejecucion a planes concretos en `tasks/`.
7. Reutilizar `templates/` para mantener consistencia.

## Convenciones

- Un archivo por feature o cambio relevante.
- Nombres en `kebab-case`.
- Fechas en formato `YYYY-MM-DD`.
- Cada feature debe enlazar requerimientos, decisiones y tareas relacionadas.

## Estado Actual

El contexto inicial ya disponible en la carpeta es:

- [arquitectura.md](./arquitectura.md)
- [tecnologias.md](./tecnologias.md)
- [flujo-principal.md](./flujo-principal.md)
