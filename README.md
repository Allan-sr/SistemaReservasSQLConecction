# Sistema de Reserva de Recursos - Proyecto #2

## Integrantes
- Derickson Aguilar Sanchez
- Allan Sanchez Ramirez

## Cómo ejecutar

### 1. Base de datos (Oracle)

1. Con Oracle XE instalado, abrir SQL Developer y conectarse como `system`.
2. Crear el usuario del proyecto:
   ```sql
   CREATE USER reservas IDENTIFIED BY reservas123;
   GRANT CONNECT, RESOURCE TO reservas;
   ALTER USER reservas QUOTA UNLIMITED ON USERS;
   ```
3. Crear una conexión con el usuario `reservas` (host `localhost`, puerto `1521`, SID `xe`).
4. Abrir `db/SistemaReservas_Limpio.sql` con esa conexión y ejecutarlo con F5.

### 2. Backend (servidor)

1. Abrir la carpeta `SistemaReservas_Etapa1` en IntelliJ IDEA como proyecto Maven.
2. Ejecutar la clase `una.sistemareservas.servidor.MainServidor` (módulo `backend`).
3. Antes de ejecutarla, definir la variable de entorno `RESERVAS_DB_PASSWORD=reservas123`
   (Run > Edit Configurations > Environment variables).
4. El servidor escucha en el puerto 5000.

### 3. Frontend (aplicación)

1. Con el servidor encendido, ejecutar la clase `una.sistemareservas.app.Main` (módulo `frontend`).
2. Se puede ejecutar varias veces para simular varios usuarios (Edit Configurations > Modify options > Allow multiple instances).
3. Para conectarse a un servidor en otra máquina, pasar su IP como argumento al ejecutar `Main`.

### Usuarios de prueba

| Rol | Usuario | Clave |
|---|---|---|
| Administrador | `admin` | `admin` |
| Funcionario | `1001` | `1001` |
| Funcionario | `1002` | `1002` |
| Funcionario | `1003` | `1003` |
| Funcionario | `1004` | `1004` |

Para ver las alertas: abrir dos ventanas, entrar con `1001` y `1002`, y crear una reserva con `1002`.

### Extracción con IA

Requiere una clave de Gemini en un archivo `config.properties` junto al servidor:

```properties
GEMINI_API_KEY=su-clave-aqui
```