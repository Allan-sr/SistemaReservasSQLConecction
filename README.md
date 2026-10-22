# Sistema de Reserva de Recursos

EIF206 Programación 3, Universidad Nacional

- **Proyecto #1:** aplicación de escritorio por capas con persistencia en XML.
- **Proyecto #2 (en desarrollo):** versión distribuida Frontend-Backend con sockets, hilos y base de datos MySQL.

## Integrantes

- Derickson Aguilar Sanchez
- Allan Sanchez Ramirez

## Tecnologías utilizadas

- **Java 26**
- **Maven** (proyecto multi-módulo): gestión de dependencias y build
- **Swing**: interfaz gráfica
- **Sockets y hilos** (`java.net`, serialización de objetos): comunicación Frontend-Backend
- **MySQL**: persistencia del Proyecto #2 (en desarrollo)
- **JAXB** (`jakarta.xml.bind` + `jaxb-runtime`): persistencia en XML del Proyecto #1
- **iText**: generación de reportes en PDF
- **JFreeChart**: gráficas de barras (Estadísticas)
- **Gson**: manejo de JSON para la integración con IA
- **JUnit Jupiter** (Surefire + Failsafe): pruebas unitarias y de integración

---

# Proyecto #2: versión distribuida (Frontend-Backend-Base de Datos)

## Arquitectura

La aplicación se divide en dos procesos independientes que se comunican por sockets. Pueden ejecutarse en máquinas distintas, y varios Frontend pueden conectarse a un mismo Backend.

```
   FRONTEND (Swing, MVC)                          BACKEND (servidor)
 ┌──────────────────────┐                     ┌──────────────────────────┐
 │ Vista                │                     │ ServidorSocket           │
 │   ↓                  │                     │   ↓ (un hilo por cliente)│
 │ Controlador          │                     │ Service (negocio)        │
 │   ↓                  │      SOCKET         │   ↓                      │
 │ Proxy ── ClienteSocket ═══════════════════ │ DAO (datos) ──► MySQL    │
 └──────────────────────┘  Peticion/Respuesta └──────────────────────────┘
             └──────── COMPARTIDO: modelo, contratos, protocolo ────────┘
```

- **Backend:** servidor sin interfaz gráfica. Escucha en un puerto, atiende cada conexión en un hilo propio y es el único que accederá a la base de datos.
- **Frontend:** capa de presentación (MVC). No tiene ninguna referencia a la base de datos. Usa el patrón **Proxy**: los controladores trabajan contra una interfaz y un proxy local envía las peticiones al Service real, que es remoto.
- **Alertas asíncronas (planificado):** cuando un usuario registra una reserva, el Backend notifica a los demás Frontend activos, que la muestran en un área de alertas.

## Estructura del proyecto

```
SistemaReservas_Etapa1/
├── compartido/   Lo que ambos lados necesitan conocer
│   ├── modelo/       Reserva, Usuario, Recurso, Categoria, ...
│   ├── contratos/    Interfaces de los servicios (IReservaService, IUsuarioService)
│   ├── protocolo/    Peticion, Respuesta, Operaciones
│   └── negocio/      ResultadoReserva
├── backend/      Servidor
│   ├── servidor/     MainServidor, ServidorSocket
│   ├── negocio/      Services con las reglas de negocio
│   ├── datos/        DAOs
│   └── util/         DatosIniciales
├── frontend/     Aplicación Swing
│   ├── vista/        Ventanas
│   ├── controlador/  Controladores (MVC)
│   ├── proxy/        Proxies de los servicios (patrón Proxy)
│   ├── cliente/      ClienteSocket
│   └── util/         Sesion
└── prueba-conexion/  Prueba inicial de comunicación por socket
```

## Estado actual del Proyecto #2

### Actualización: Etapa 1 (base de comunicación) y Etapa 2 (login distribuido, en curso)

Hecho:

- Proyecto Maven multi-módulo (`compartido`, `backend`, `frontend`).
- Comunicación Cliente-Servidor por sockets con un hilo por cliente, con prueba `PING`/`PONG`.
- Protocolo de mensajes: `Peticion` (operación + datos) y `Respuesta` (éxito + datos + mensaje).
- Modelos del dominio `Serializable`, para poder enviarlos por el socket.
- Contratos (interfaces) de servicio en `compartido`, incluyendo `autenticar` en `IUsuarioService`.
- Clase `Operaciones` con los nombres de las operaciones compartidos por ambos lados.
- `UsuarioServiceProxy.autenticar`: el proxy envía la petición de login al servidor.

Pendiente:

- Despachador de operaciones en el servidor (hoy solo reconoce `PING`) y respuesta de `USUARIO_AUTENTICAR`.
- Controladores del Frontend usando interfaces y proxies, en lugar de los Services del Backend. **Mientras tanto, el módulo `frontend` aún no compila por completo.**
- Proxies e interfaces del resto de módulos: reservas, funcionarios, categorías, recursos, calendarios y estadísticas.
- Migración de los DAOs de XML a MySQL, y script de la base de datos.
- Alertas asíncronas de nuevas reservas (mostrar, ver y descartar).
- Pruebas unitarias (Surefire) y de integración (Failsafe) adaptadas a la nueva arquitectura.

## Cómo ejecutar lo disponible hoy

1. Clonar el repositorio y abrir la carpeta en IntelliJ IDEA como proyecto Maven.
2. Ejecutar `una.sistemareservas.servidor.MainServidor` (módulo `backend`). Escucha en el puerto **5000**.
3. Ejecutar `una.sistemareservas.cliente.MainClientePrueba` (módulo `frontend`). Envía un `PING` y debe imprimir `PONG`.

> La aplicación completa con ventanas todavía no se puede ejecutar en la versión distribuida, porque el Frontend está en proceso de migración.

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