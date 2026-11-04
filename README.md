# Sistema de Reserva de Recursos

EIF206 Programación 3, Universidad Nacional

- **Proyecto #1:** aplicación de escritorio por capas con persistencia en XML.
- **Proyecto #2:** versión distribuida Frontend-Backend con sockets, hilos, base de datos Oracle y alertas asíncronas.

## Integrantes

- Derickson Aguilar Sanchez
- Allan Sanchez Ramirez

## Tecnologías utilizadas

- **Java 26**
- **Maven** (proyecto multi-módulo): gestión de dependencias y build
- **Swing**: interfaz gráfica
- **Sockets y hilos** (`java.net`, serialización de objetos): comunicación Frontend-Backend y alertas
- **Oracle Database XE** + JDBC (`ojdbc11`) + **HikariCP** (pool de conexiones): persistencia del Proyecto #2
- **JAXB** (`jakarta.xml.bind` + `jaxb-runtime`): persistencia en XML del Proyecto #1
- **iText**: generación de reportes en PDF
- **JFreeChart**: gráficas de barras (Estadísticas)
- **Gson**: manejo de JSON para la integración con IA (Gemini)
- **JUnit Jupiter** (Surefire + Failsafe): pruebas unitarias y de integración

---

# Proyecto #2: versión distribuida (Frontend-Backend-Base de Datos)

## Arquitectura

La aplicación se divide en dos procesos independientes que se comunican por sockets. Pueden ejecutarse en máquinas distintas, y varios Frontend pueden conectarse a un mismo Backend.

```
   FRONTEND (Swing, MVC)                          BACKEND (servidor)
 ┌──────────────────────┐                     ┌──────────────────────────┐
 │ Vista                │                     │ ServidorSocket (5000)    │
 │   ↓                  │                     │   ↓ (un hilo por cliente)│
 │ Controlador          │                     │ Service (negocio)        │
 │   ↓                  │      SOCKET         │   ↓                      │
 │ Proxy ── ClienteSocket ═══════════════════ │ DAO (datos) ──► Oracle   │
 │                      │  Peticion/Respuesta │                          │
 │ ListenerAlertas ◄═════════ puerto 5001 ═══ │ ServidorAlertas          │
 └──────────────────────┘      (alertas)      └──────────────────────────┘
             └──────── COMPARTIDO: modelo, contratos, protocolo ────────┘
```

- **Backend:** servidor sin interfaz gráfica. Atiende cada conexión en un hilo propio y es el único que accede a la base de datos Oracle. La creación y cancelación de reservas se ejecutan bajo un candado para evitar doble reserva del mismo recurso.
- **Frontend:** capa de presentación (MVC). No tiene ninguna referencia a la base de datos. Usa el patrón **Proxy**: los controladores trabajan contra una interfaz (módulo `compartido`) y un proxy local envía la petición por socket al Service real, que es remoto.
- **Alertas asíncronas:** cada Frontend abre una segunda conexión, dedicada solo a recibir avisos (puerto 5001). Cuando un funcionario crea una reserva, el Backend avisa a todos los demás clientes conectados y la alerta aparece en el panel de la derecha, donde se puede **ver** el detalle o **descartar** con confirmación (el descarte es local). Las alertas son solo de la sesión activa.
- **Conexiones a Oracle:** el Backend usa un pool de conexiones (HikariCP) para atender varios clientes a la vez sin agotar los recursos de Oracle.

## Estructura del proyecto

```
SistemaReservas_Etapa1/
├── compartido/   Lo que ambos lados necesitan conocer
│   ├── modelo/       Reserva, Usuario, Recurso, Categoria, ...
│   ├── contratos/    Interfaces de los servicios (IReservaService, IUsuarioService, ...)
│   ├── protocolo/    Peticion, Respuesta, Operaciones
│   └── negocio/      ResultadoReserva, ReservaExtraidaDTO, NotificacionReserva
├── backend/      Servidor
│   ├── servidor/     MainServidor, ServidorSocket, ServidorAlertas, GestorNotificaciones
│   ├── negocio/      Services con las reglas de negocio
│   ├── datos/        DAOs y ConexionOracle (pool HikariCP)
│   └── util/         DatosIniciales
├── frontend/     Aplicación Swing
│   ├── vista/        Ventanas (incluye AlertasView)
│   ├── controlador/  Controladores (MVC)
│   ├── proxy/        Proxies de los servicios (patrón Proxy)
│   ├── cliente/      ClienteSocket, ListenerAlertas
│   └── util/         Sesion, ReportePDFService
└── prueba-conexion/  Clientes de prueba manual por socket
```

## Funcionalidades

- Login y cambio de clave.
- Reservas (crear, cancelar, listar las propias, comprobante en PDF) y extracción de datos con IA a partir de una frase.
- Funcionarios, Categorías y Recursos (administrador).
- Calendarización de recursos y de actividades.
- Estadísticas con gráficas y reportes PDF.
- Alertas asíncronas de reservas creadas por otros usuarios.

## Base de datos (Oracle)

1. Instalar Oracle Database XE y abrir Oracle SQL Developer.
2. Conectarse como administrador (`system`) y crear el usuario del proyecto:
   ```sql
   CREATE USER reservas IDENTIFIED BY reservas123;
   GRANT CONNECT, RESOURCE TO reservas;
   ALTER USER reservas QUOTA UNLIMITED ON USERS;
   ```
3. Crear una conexión con el usuario `reservas` (host `localhost`, puerto `1521`, SID `xe`).
4. Abrir `db/SistemaReservas_Limpio.sql` con esa conexión y ejecutarlo con **F5** (Run Script). Crea las tablas y carga los datos de prueba. Al final debe mostrar: USUARIOS 5, FUNCIONARIOS 4, CATEGORIAS 5, RECURSOS 10, RESERVAS 5, RESERVA_RECURSOS 11.

> El script contiene claves de prueba en texto plano; es solo para desarrollo. No ejecutarlo sobre tablas existentes.

## Configuración

La contraseña de la base de datos **no está en el código**: se lee de una variable de entorno.

| Variable | Obligatoria | Descripción |
|---|---|---|
| `RESERVAS_DB_PASSWORD` | Sí | Contraseña del usuario de Oracle (en el ejemplo, `reservas123`) |
| `RESERVAS_DB_USER` | No | Usuario de Oracle (por defecto `reservas`) |
| `GEMINI_API_KEY` | Solo para IA | También puede ir en `config.properties` |

En IntelliJ: *Run > Edit Configurations* > configuración de `MainServidor` > **Environment variables** > `RESERVAS_DB_PASSWORD=reservas123`.

### Funcionalidad de Inteligencia Artificial

La pantalla de Reservas permite llenar el formulario a partir de una frase en lenguaje natural, usando la API de Gemini (Google). La extracción se ejecuta en el **Backend**, y la clave se lee de `GEMINI_API_KEY` o de un archivo `config.properties` en la carpeta desde donde se ejecuta el servidor:

```properties
GEMINI_API_KEY=su-clave-aqui
```

`config.properties` está excluido del control de versiones (`.gitignore`). **El resto del sistema funciona sin esta clave.**

## Cómo ejecutar

1. Tener Oracle encendido con la base cargada (sección anterior).
2. Abrir `SistemaReservas_Etapa1` en IntelliJ como proyecto Maven.
3. Ejecutar `una.sistemareservas.servidor.MainServidor` (módulo `backend`) con `RESERVAS_DB_PASSWORD` configurada. Escucha en el puerto **5000** (peticiones) y **5001** (alertas).
4. Ejecutar `una.sistemareservas.app.Main` (módulo `frontend`). Se puede ejecutar varias veces para simular varios usuarios (en *Edit Configurations*, activar *Allow multiple instances*). Para conectarse a otra máquina, pasar la IP del servidor como primer argumento.
5. Usuarios de prueba:
   - **Administrador:** `admin` / `admin`
   - **Funcionarios:** `1001` / `1001`, `1002` / `1002`, `1003` / `1003`, `1004` / `1004`

### Probar las alertas

Abrir dos ventanas del Frontend, entrar con `1001` y `1002`, y crear una reserva con `1002`: la alerta aparece en la ventana de `1001`.

## Pruebas

- **Unitarias** (JUnit Jupiter + Surefire, clases `*Test`): validaciones de los servicios y serialización del protocolo. No necesitan base de datos.
  ```
  mvn test
  ```
- **De integración** (JUnit Jupiter + Failsafe, clases `*IT`): levantan el servidor real en puertos de prueba (5099 y 5098) y lo usan por sockets contra Oracle, incluyendo el flujo completo de la alerta asíncrona. Requieren Oracle encendido y `RESERVAS_DB_PASSWORD` definida.
  ```
  mvn verify
  ```

---

# Proyecto #1: versión de escritorio (XML)

Primera versión del sistema, desarrollada como aplicación de escritorio por capas con persistencia en archivos XML.

## Cómo ejecutar el Proyecto #1

1. Abrir la carpeta del Proyecto #1 en IntelliJ IDEA y cargarla como proyecto Maven (clic derecho en `pom.xml` > "Add as Maven Project", si no se detecta automáticamente).
2. Ejecutar la clase `una.sistemareservas.app.Main`.
3. En el primer arranque, el sistema crea automáticamente un usuario administrador y un funcionario de prueba:
   - **Administrador:** id `admin`, clave `admin`
   - **Funcionario:** id `1001`, clave `1001`

## Funcionalidad de Inteligencia Artificial

La pantalla de Reservas permite llenar el formulario automáticamente a partir de una frase en lenguaje natural, usando la API de Gemini (Google). La clase que la implementa (`AIExtractorService`) está en el módulo `backend`.

**Esta función requiere una clave de API propia**: no se incluye en el repositorio por ser información sensible.

Para habilitarla:

1. Genere una clave de API gratuita en [Google AI Studio](https://aistudio.google.com/apikey).
2. Cree un archivo `config.properties` en la raíz del proyecto (mismo nivel que el `pom.xml` y el `.gitignore`) con lo siguiente:
   ```properties
   GEMINI_API_KEY=su-clave-aqui
   ```
3. Este archivo está excluido del control de versiones (`.gitignore`) por seguridad.

**El resto del sistema funciona completamente sin esta clave.** Si no se configura, el botón "Extraer con IA" mostrará un mensaje indicando que la clave no está configurada, y el resto de las funcionalidades (login, reservas manuales, funcionarios, categorías, recursos, calendarización, estadísticas, reportes PDF) opera con normalidad.