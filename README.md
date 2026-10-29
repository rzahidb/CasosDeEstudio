# 🖥️ CasosDeEstudioProyecto
### Proyecto de Concurrencia en JavaFX

**Universidad Americana (UAM)**  
**Asignatura:** Programación de Aplicaciones de Escritorio  
**Lenguaje:** Java 21  
**Framework:** JavaFX 21.0.6  
**Gestor de dependencias:** Maven

---

## 📖 Descripción del proyecto

Este proyecto consiste en el desarrollo de una aplicación de escritorio utilizando **Java y JavaFX**, cuyo objetivo es implementar y demostrar los principales conceptos de la programación concurrente.

La aplicación integra tres casos de estudio que permiten comprender el manejo de hilos, la sincronización de recursos compartidos, la cancelación cooperativa y la actualización segura de interfaces gráficas.

Cada caso cuenta con su propia interfaz, controles interactivos, barras de progreso y registros de operaciones que permiten observar el comportamiento de los hilos en tiempo real.

### 📄 Documentación y evidencias

El proyecto cuenta con un informe técnico que documenta la implementación de los tres casos de estudio, incluyendo:

- Explicación del funcionamiento de cada caso.
- Fragmentos importantes del código fuente.
- Implementación de hilos y sincronización.
- Uso de Controllers, Services y Models.
- Implementación de barras de progreso en JavaFX.
- Pruebas de aceptación y evidencias de ejecución.
- Resultados y conclusiones.

### 📎 [CONSULTAR INFORME TÉCNICO Y EVIDENCIAS](https://github.com/rzahidb/CasosDeEstudio/blob/main/Informe_General_Concurrencia.docx.pdf)

El documento se encuentra disponible en formato PDF dentro de este repositorio.

---

## 🎯 Objetivos

### Objetivo general

Desarrollar una aplicación de escritorio que permita implementar y analizar mecanismos de concurrencia en Java, garantizando la integridad de los recursos compartidos y el funcionamiento adecuado de la interfaz gráfica.

### Objetivos específicos

- Implementar tareas concurrentes utilizando `Thread` y `Runnable`.
- Aplicar `synchronized` para proteger recursos compartidos.
- Implementar cancelación cooperativa mediante `interrupt()`.
- Coordinar la finalización de trabajadores utilizando `join()`.
- Evitar ejecuciones simultáneas duplicadas.
- Mantener una interfaz JavaFX operativa durante la ejecución.
- Representar el avance mediante barras de progreso.
- Permitir el reinicio seguro de las simulaciones.

---

## 🧩 Casos de estudio

### 🎟️ Caso 1 — Reserva concurrente de entradas

**Tema:** Concurrencia y sincronización de recursos compartidos.

Este caso simula un sistema de reservas para un evento con una cantidad limitada de entradas.

Tres agentes intentan realizar reservas simultáneamente, accediendo a un inventario compartido.

**Configuración:**

| Elemento | Cantidad |
|---|---:|
| Entradas disponibles | 20 |
| Agentes concurrentes | 3 |
| Intentos por agente | 10 |
| Solicitudes totales | 30 |
| Reservas esperadas | 20 |
| Solicitudes rechazadas | 10 |

**Conceptos implementados:**

- Creación de tres hilos concurrentes.
- Implementación de `Runnable`.
- Inventario compartido entre agentes.
- Sincronización mediante `synchronized`.
- Prevención de condiciones de carrera.
- Bloqueo de inicios duplicados.
- Actualización de contadores y barra de progreso.

**Clases principales:**

- `ReservasController.java`
- `ReservasService.java`
- `AgenteReserva.java`
- `InventarioEntradas.java`
- `ResultadoReservas.java`

La sincronización garantiza que la comprobación de disponibilidad y el descuento de entradas se realicen de manera segura.

**Resultado esperado:** 20 reservas confirmadas, 10 rechazadas y 0 entradas disponibles.

---

### 📦 Caso 2 — Procesamiento de pedidos con cancelación

**Tema:** Procesamiento en segundo plano y cancelación cooperativa.

Este caso representa un sistema encargado de procesar 30 pedidos secuencialmente mediante un trabajador independiente del hilo gráfico de JavaFX.

Cada pedido requiere una espera simulada de 200 milisegundos.

**Configuración:**

| Elemento | Valor |
|---|---|
| Total de pedidos | 30 |
| Trabajadores | 1 |
| Duración por pedido | 200 ms |
| Cancelación | Cooperativa |
| Seguimiento | Barra de progreso y contador |

**Conceptos implementados:**

- Procesamiento mediante un hilo secundario.
- Simulación de trabajo con `Thread.sleep()`.
- Cancelación mediante `interrupt()`.
- Manejo de `InterruptedException`.
- Conservación de pedidos completados.
- Control de estados del procesamiento.
- Reinicio seguro con un nuevo trabajador.
- Espera de finalización sin bloquear JavaFX.

**Clases principales:**

- `PedidosController.java`
- `PedidosService.java`
- `ProcesadorPedidos.java`
- `EstadoProcesamiento.java`

La aplicación permite cancelar durante el procesamiento, conservando únicamente los pedidos completados y evitando contabilizar operaciones interrumpidas.

**Resultado esperado:** 30 pedidos y 100 % de progreso cuando no exista cancelación.

---

### 💰 Caso 3 — Caja de ventas concurrente

**Tema:** Concurrencia, sincronización y control de hilos.

Este caso integra los conceptos anteriores mediante una caja compartida en la que tres vendedores registran ventas concurrentemente.

Cada vendedor debe realizar cinco ventas con un valor de C$100 por operación.

**Configuración:**

| Elemento | Cantidad |
|---|---:|
| Vendedores concurrentes | 3 |
| Ventas por vendedor | 5 |
| Valor por venta | C$100 |
| Ventas esperadas | 15 |
| Recaudación esperada | C$1,500 |

**Conceptos implementados:**

- Tres vendedores ejecutándose concurrentemente.
- Recurso compartido mediante la clase `Caja`.
- Registro sincronizado de ventas y recaudación.
- Protección de datos utilizando `synchronized`.
- Cancelación cooperativa de vendedores.
- Verificación de finalización de hilos.
- Reinicio seguro de la simulación.
- Barras de progreso individuales y general.

**Clases principales:**

- `CajaController.java`
- `CajaService.java`
- `Caja.java`
- `Vendedor.java`
- `ResultadoCaja.java`

La implementación protege conjuntamente el número de ventas y la recaudación, evitando inconsistencias cuando varios vendedores acceden a la caja.

**Resultado esperado:** 15 ventas confirmadas y una recaudación total de C$1,500.

---

## ⚙️ Tecnologías utilizadas

| Tecnología | Aplicación |
|---|---|
| Java 21 | Lenguaje principal |
| JavaFX 21.0.6 | Desarrollo de interfaces gráficas |
| FXML | Definición de las vistas |
| Maven | Gestión de dependencias y ejecución |
| IntelliJ IDEA | Entorno de desarrollo |
| Git | Control de versiones |
| GitHub | Repositorio y colaboración |

---

## 🏗️ Arquitectura del proyecto

La aplicación organiza sus componentes separando la presentación gráfica, la lógica de coordinación y los modelos de datos.

### Controllers

Gestionan las acciones del usuario y actualizan los controles visuales.

- `MenuController`
- `ReservasController`
- `PedidosController`
- `CajaController`

### Services

Administran la lógica de ejecución de las simulaciones.

Se encargan de crear, iniciar, coordinar y cancelar los hilos, además de comunicar sus resultados a los Controllers.

- `ReservasService`
- `PedidosService`
- `CajaService`

### Models

Representan los trabajadores, recursos compartidos, resultados y estados del sistema.

Entre ellos se encuentran:

- `AgenteReserva`
- `InventarioEntradas`
- `ProcesadorPedidos`
- `EstadoProcesamiento`
- `Caja`
- `Vendedor`
- `ResultadoReservas`
- `ResultadoCaja`

### Views

Las interfaces están desarrolladas mediante FXML:

- `menu-view.fxml`
- `reservas-view.fxml`
- `pedidos-view.fxml`
- `caja-view.fxml`

### Navegación

`ViewNavigator.java` administra los cambios de pantalla y coordina el cierre seguro cuando existen tareas activas.

---

## 🔄 Manejo de concurrencia

### Thread y Runnable

Permiten definir y ejecutar trabajos mediante hilos independientes.

### synchronized

Protege las secciones críticas en las que varios trabajadores acceden a un mismo recurso.

Se utiliza principalmente en el inventario de entradas y en la caja de ventas.

### interrupt()

Permite solicitar cooperativamente la cancelación de un trabajador.

### join()

Permite esperar la finalización efectiva de los hilos. En el proyecto, esta coordinación se realiza desde hilos secundarios para evitar bloquear la interfaz.

### Platform.runLater()

Permite enviar actualizaciones de forma segura al hilo gráfico de JavaFX.

Esto resulta fundamental para actualizar contadores, estados y barras de progreso mientras los trabajadores ejecutan sus operaciones.

---

## 📊 Barras de progreso

Las barras fueron desarrolladas mediante el componente `ProgressBar` de JavaFX.

Se declaran en los archivos FXML y se vinculan a sus Controllers utilizando `fx:id` y `@FXML`.

El progreso se calcula a partir de las operaciones completadas:

```java
double progreso = (double) operacionesCompletadas / totalOperaciones;
barraProgreso.setProgress(progreso);
```

**Implementación por caso:**

- **Reservas:** avance de las 30 solicitudes.
- **Pedidos:** avance de los 30 pedidos.
- **Caja de ventas:** una barra general para 15 ventas y tres barras individuales para cada vendedor.

Las actualizaciones gráficas se realizan de manera segura utilizando el hilo de JavaFX.

---

## 🚀 Instalación y ejecución

### Requisitos

- JDK 21.
- Maven.
- JavaFX 21.0.6 (gestionado mediante Maven).
- IntelliJ IDEA u otro entorno compatible con Java y Maven.

### 1. Clonar el repositorio

```bash
git clone https://github.com/rzahidb/CasosDeEstudio.git
```

### 2. Acceder al proyecto

```bash
cd CasosDeEstudio
```

### 3. Compilar y ejecutar

```bash
mvn clean javafx:run
```

También es posible abrir el proyecto desde IntelliJ IDEA, cargar las dependencias de Maven y ejecutar `Launcher.java`.

### 4. Utilizar la aplicación

Una vez iniciada, aparecerá el menú principal, desde el cual se pueden seleccionar los tres casos de estudio.

Cada simulación cuenta con controles para iniciar, observar el progreso y realizar las acciones correspondientes.

---

## 🧪 Pruebas y validación

Las pruebas de aceptación están diseñadas para verificar el cumplimiento de los requisitos de cada caso.

### Caso 1

- Cinco simulaciones completas.
- Verificación de 20 reservas confirmadas.
- Verificación de 10 solicitudes rechazadas.
- Inventario final igual a cero.
- Rechazo de ejecuciones duplicadas.
- Actualización segura de la interfaz.

### Caso 2

- Procesamiento completo de 30 pedidos.
- Cancelación durante una espera.
- Conservación del progreso parcial.
- Reinicio después de la cancelación.
- Cierre sin trabajadores activos.

### Caso 3

- Cinco simulaciones completas.
- Verificación de 15 ventas y C$1,500.
- Cancelación de vendedores.
- Consistencia entre ventas y recaudación.
- Prevención de inicios duplicados.
- Reinicio y cierre seguro.

Los resultados observados, las capturas y las explicaciones técnicas se documentan en el informe del proyecto.

---

## 📚 Informe técnico y evidencias

Como parte de los entregables académicos, se incorporó un informe general que reúne la documentación de los tres casos de estudio.

El informe incluye explicaciones sobre concurrencia, sincronización, cancelación cooperativa, implementación de interfaces y fragmentos relevantes del código.

También contiene la documentación de las pruebas de aceptación y sus respectivos apartados de evidencias.

### 📄 [ABRIR INFORME GENERAL Y EVIDENCIAS EN PDF](https://github.com/rzahidb/CasosDeEstudio/blob/main/Informe_General_Concurrencia.docx.pdf)

**Archivo:** `Informe_General_Concurrencia.docx.pdf`

---

## 👨‍💻 Desarrollo académico

**Universidad:** Universidad Americana (UAM)  
**Asignatura:** Programación de Aplicaciones de Escritorio  
**Proyecto:** Casos de estudio sobre concurrencia en Java  
**Modalidad:** Trabajo grupal

El repositorio contiene el código fuente, la configuración de ejecución y la documentación del proyecto.

---

## ✅ Conclusión

El desarrollo de estos tres casos permite comprender la importancia de administrar correctamente la ejecución concurrente dentro de una aplicación de escritorio.

A través del uso de hilos, sincronización, cancelación cooperativa y actualización segura de interfaces, se presentan soluciones a problemas comunes relacionados con el acceso simultáneo a recursos y la coordinación de trabajadores.

El proyecto demuestra cómo los mecanismos de concurrencia pueden integrarse con JavaFX para construir aplicaciones interactivas, organizadas y capaces de ejecutar tareas en segundo plano.

---

**Universidad Americana (UAM) — Programación de Aplicaciones de Escritorio · 2026**