package proyectoenjava;

import java.util.Scanner;
/**
 * SISTEMA INTELIGENTE DE GESTION DE EMERGENCIAS
 */
public class ProyectoJavaa {

    static Scanner teclado = new Scanner(System.in);

    // ---------- 1. DATOS DE CADA EMERGENCIA (arreglos paralelos, indice 0 sin usar) ----------
    static int[] codEmergencia = new int[101];
    static int[] puntajePrioridad = new int[101];
    static int[] diaRegistro = new int[101];
    static String[] tipoEmergencia = new String[101];
    static String[] estado = new String[101];
    static String[] nivelPrioridad = new String[101];
    static boolean[] heridos = new boolean[101];
    static boolean[] riesgoVida = new boolean[101];
    static boolean[] incendio = new boolean[101];
    static String[] nivelIncendio = new String[101];
    static double[] ubicacionX = new double[101];
    static double[] ubicacionY = new double[101];
    static double[] tiempoDespacho = new double[101];
    static double[] tiempoLlegada = new double[101];
    static double[] tiempoAtencion = new double[101];
    static int[] horaRecepcionH = new int[101];
    static int[] horaRecepcionM = new int[101];

    // ---------- 2. ARREGLOS DE UNIDADES (0=Disponible, 1=Ocupada; indice 0 sin usar) ----------
    static int[] bomberos = new int[11];
    static int[] ambulancias = new int[11];
    static int[] patrullas = new int[11];
    // Columnas 1-10 Bomberos | 11-20 Ambulancias | 21-30 Patrullas
    static int[][] matrizAsignacion = new int[101][31];

    // Estaciones de respuesta (plano cartesiano); indice 0 sin usar
    static double[] estacionX = new double[4];
    static double[] estacionY = new double[4];
    static String[] nombreEstacion = new String[4];

    // Ranking de tipos de emergencia; indice 0 sin usar
    static String[] nombreTipo = new String[4];
    static int[] contadorTipo = new int[4];

    // Estadisticas semanales (hasta 5 semanas por mes); indice 0 sin usar
    static int[] emergenciasPorSemana = new int[6];

    // ---------- VARIABLES DE CONTROL Y ESTADISTICAS GLOBALES ----------
    static int totalEmergencias = 0;
    static double sumaTiempoDespacho = 0, sumaTiempoLlegada = 0, sumaTiempoAtencion = 0, sumaPrioridad = 0;
    static int contApoyoExterno = 0, contUnidadesApoyoExterno = 0;
    static int totalBomberosUsados = 0, totalAmbulanciasUsadas = 0, totalPatrullasUsadas = 0;
    static int contPrioridadAlta = 0, contPrioridadMedia = 0, contPrioridadBaja = 0;
    static int contLlegadasRegistradas = 0, contEmergenciasFinalizadas = 0;

    // ================= PROGRAMA PRINCIPAL =================
    public static void main(String[] args) {
        inicializar();
        int opcionMenu;
        do {
            limpiarPantalla();
            System.out.println();
            System.out.println("======================================================");
            System.out.println("     SISTEMA INTELIGENTE DE GESTION DE EMERGENCIAS     ");
            System.out.println("======================================================");
            System.out.println("1. Registrar nueva emergencia (calcula prioridad y despacha)");
            System.out.println("2. Registrar llegada de unidades a una emergencia");
            System.out.println("3. Finalizar una emergencia");
            System.out.println("4. Ver estado de las unidades");
            System.out.println("5. Ver historial de emergencias");
            System.out.println("6. Ver reportes estadisticos y matematicos");
            System.out.println("7. Ver detalle completo de una emergencia");
            System.out.println("8. Salir del sistema");
            System.out.println("------------------------------------------------------");
            opcionMenu = leerEnteroEnRango("Ingrese una opcion: ", 1, 8);
            limpiarPantalla();
            switch (opcionMenu) {
                case 1 -> {
                    if (totalEmergencias >= 100) {
                        System.out.println("Se alcanzo el limite maximo de 100 emergencias registradas. No se pueden registrar mas.");
                    } else {
                        totalEmergencias++;
                        registrarEmergencia(totalEmergencias);
                    }
                    pausar();
                }
                case 2 -> {
                    registrarLlegada();
                    pausar();
                }
                case 3 -> {
                    finalizarEmergencia();
                    pausar();
                }
                case 4 -> {
                    mostrarEstadoUnidades();
                    pausar();
                }
                case 5 -> {
                    mostrarHistorial();
                    pausar();
                }
                case 6 -> {
                    generarReportes();
                    pausar();
                }
                case 7 -> {
                    mostrarDetalleEmergencia();
                    pausar();
                }
                case 8 -> System.out.println("Saliendo del sistema...");
                default -> {
                    System.out.println("Opcion no valida. Intente nuevamente.");
                    pausar();
                }
            }
        } while (opcionMenu != 8);
        teclado.close();
    }

    static void pausar() {
        System.out.println();
        System.out.println("Presione ENTER para volver al menu...");
        teclado.nextLine();
    }

    static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    static void inicializar() {
        for (int i = 1; i <= 10; i++) {
            bomberos[i] = 0;
            ambulancias[i] = 0;
            patrullas[i] = 0;
        }
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 30; j++) {
                matrizAsignacion[i][j] = 0;
            }
            estado[i] = "Sin Registrar";
            tiempoLlegada[i] = 0;
            tiempoAtencion[i] = 0;
        }
        for (int i = 1; i <= 5; i++) {
            emergenciasPorSemana[i] = 0;
        }
        for (int i = 1; i <= 3; i++) {
            contadorTipo[i] = 0;
        }
        nombreTipo[1] = "Incendio";
        nombreTipo[2] = "Robo";
        nombreTipo[3] = "Accidente";
        estacionX[1] = 0;
        estacionY[1] = 0;
        nombreEstacion[1] = "Estacion Central";
        estacionX[2] = 15;
        estacionY[2] = 10;
        nombreEstacion[2] = "Estacion Norte";
        estacionX[3] = 8;
        estacionY[3] = -12;
        nombreEstacion[3] = "Estacion Sur";
    }

    // ================= SUBPROCESOS: REGISTRO DE EMERGENCIA =================

    static void registrarEmergencia(int idEmerg) {
        System.out.println("--- REGISTRO DE INCIDENTE #" + idEmerg + " ---");
        codEmergencia[idEmerg] = idEmerg;

        DatosBasicos datos = leerDatosBasicosEmergencia(idEmerg);
        nivelIncendio[idEmerg] = datos.nivelIncendioActual;

        // 1. Primero se calcula la prioridad: no depende de que unidades se despachen,
        //    solo de riesgoVida/incendio/nivelIncendio/heridos, que ya se leyeron arriba.
        ResultadoPrioridad resultadoPrioridad = calcularYMostrarPrioridad(
                idEmerg, datos.hayRiesgoVida, datos.hayIncendio, datos.nivelIncendioActual, datos.numHeridos);

        mostrarEstacionCercana(datos.x, datos.y);

        // 2. Luego se decide y despacha QUE unidades se envian.
        String explicacion = asignarUnidadesEmergencia(
                idEmerg, datos.tipoSeleccionado, datos.hayIncendio, datos.nivelIncendioActual, datos.hayHeridos, datos.numHeridos);

        // 3. Ahora tiene sentido preguntar cuanto se demoro en despachar,
        //    porque ya sabemos que se despacho.
        registrarTiempoDespacho(idEmerg);

        actualizarRankingTipo(datos.tipoSeleccionado);

        System.out.println("=> Se despacharon unidades para la emergencia #" + idEmerg + " (" + datos.tipoSeleccionado
                + ") porque corresponde a " + explicacion + ", obteniendo un indice de prioridad de "
                + resultadoPrioridad.puntaje + " puntos (Prioridad " + resultadoPrioridad.nivel + ").");
        System.out.println("Estado actual de la emergencia: " + estado[idEmerg]);
    }

    static class DatosBasicos {
        String tipoSeleccionado;
        double x, y;
        boolean hayHeridos;
        int numHeridos;
        boolean hayRiesgoVida;
        boolean hayIncendio;
        String nivelIncendioActual;
    }

    static DatosBasicos leerDatosBasicosEmergencia(int idEmerg) {
        DatosBasicos datosLeidos = new DatosBasicos();
        System.out.println("Tipo de incidente:");
        System.out.println("  1. Incendio");
        System.out.println("  2. Robo");
        System.out.println("  3. Accidente");
        int opcionTipo = leerEnteroEnRango("Ingrese el numero de la opcion: ", 1, 3);
        datosLeidos.tipoSeleccionado = nombreTipo[opcionTipo];
        tipoEmergencia[idEmerg] = datosLeidos.tipoSeleccionado;

        datosLeidos.x = leerReal("Coordenada X de la emergencia (km): ");
        datosLeidos.y = leerReal("Coordenada Y de la emergencia (km): ");
        ubicacionX[idEmerg] = datosLeidos.x;
        ubicacionY[idEmerg] = datosLeidos.y;

        int hora = leerEnteroEnRango("Hora de recepcion (0-23): ", 0, 23);
        int minuto = leerEnteroEnRango("Minuto de recepcion (0-59): ", 0, 59);
        horaRecepcionH[idEmerg] = hora;
        horaRecepcionM[idEmerg] = minuto;
        if (hora < 6) {
            System.out.println("  [AVISO] La emergencia se recibe de madrugada (" + hora + ":" + minuto
                    + "). Puede haber menos personal disponible y tiempos de respuesta mas altos.");
        }

        int diaIngresado = leerEnteroEnRango("Dia del mes en que ocurre (1-30), para las estadisticas: ", 1, 30);
        diaRegistro[idEmerg] = diaIngresado;
        int semanaCalculada = ((diaIngresado - 1) / 7) + 1;
        emergenciasPorSemana[semanaCalculada]++;

        datosLeidos.hayHeridos = leerLogico("Existen heridos? (V/F): ");
        heridos[idEmerg] = datosLeidos.hayHeridos;
        datosLeidos.numHeridos = 0;
        if (datosLeidos.hayHeridos) {
            datosLeidos.numHeridos = leerEnteroEnRango("Cantidad de personas heridas: ", 1, 999999);
        }

        datosLeidos.hayRiesgoVida = leerLogico("Existe riesgo de vida inminente? (V/F): ");
        riesgoVida[idEmerg] = datosLeidos.hayRiesgoVida;

        datosLeidos.hayIncendio = datosLeidos.tipoSeleccionado.equals("Incendio");
        incendio[idEmerg] = datosLeidos.hayIncendio;

        datosLeidos.nivelIncendioActual = "";
        if (datosLeidos.hayIncendio) {
            System.out.println("Para clasificar el incendio, elige el nivel que mejor coincida con esta descripcion:");
            System.out.println();
            System.out.println("  NIVEL BAJO: Incendio pequeno y contenido. Ejemplo: una sarten en llamas o un pequeno fuego en un brasero. No se propaga, se controla facilmente.");
            System.out.println("  NIVEL MEDIO: Incendio que ya se esta propagando. Ejemplo: un incendio que afecta a varias habitaciones o a un garaje con muchos materiales. Hay mucho humo y puede afectar a vecinos.");
            System.out.println("  NIVEL ALTO: Incendio fuera de control. Ejemplo: un gran incendio forestal que arrasa hectareas o un incendio en la ciudad que ya ha consumido varias casas y amenaza a todo un sector. Hay alto riesgo de victimas.");
            System.out.println();
            System.out.println("Nivel del incendio:");
            System.out.println("  1. Bajo");
            System.out.println("  2. Medio");
            System.out.println("  3. Alto");
            int opcionNivelIncendio = leerEnteroEnRango("Ingrese el numero de la opcion: ", 1, 3);
            if (opcionNivelIncendio == 1) datosLeidos.nivelIncendioActual = "Bajo";
            if (opcionNivelIncendio == 2) datosLeidos.nivelIncendioActual = "Medio";
            if (opcionNivelIncendio == 3) datosLeidos.nivelIncendioActual = "Alto";
        }
        return datosLeidos;
    }

    static class ResultadoPrioridad {
        int puntaje;
        String nivel;
    }

    static ResultadoPrioridad calcularYMostrarPrioridad(
            int idEmerg, boolean hayRiesgoVida, boolean hayIncendio, String nivelIncendioActual, int numHeridos) {
        System.out.println("--- RESULTADO DEL REGISTRO #" + idEmerg + " ---");

        int puntaje = calcularPrioridad(hayRiesgoVida, hayIncendio, nivelIncendioActual, numHeridos);
        puntajePrioridad[idEmerg] = puntaje;
        sumaPrioridad += puntaje;
        String nivel = clasificarPrioridad(puntaje);
        nivelPrioridad[idEmerg] = nivel;
        if (nivel.equals("Alta")) contPrioridadAlta++;
        if (nivel.equals("Media")) contPrioridadMedia++;
        if (nivel.equals("Baja")) contPrioridadBaja++;

        System.out.println("--- INDICE DE PRIORIDAD ---");
        if (hayRiesgoVida) {
            System.out.println("  + Riesgo de vida: 40 pts");
        }
        if (hayIncendio) {
            int ptsIncendio = 10;
            if (nivelIncendioActual.equals("Medio")) ptsIncendio = 20;
            if (nivelIncendioActual.equals("Alto")) ptsIncendio = 30;
            System.out.println("  + Incendio (Nivel " + nivelIncendioActual + "): " + ptsIncendio + " pts");
        }
        if (numHeridos > 5) {
            System.out.println("  + Mas de 5 heridos: 20 pts");
        }
        System.out.println("  TOTAL: " + puntaje + " puntos -> Prioridad " + nivel);

        ResultadoPrioridad resultado = new ResultadoPrioridad();
        resultado.puntaje = puntaje;
        resultado.nivel = nivel;
        return resultado;
    }

    static void mostrarEstacionCercana(double x, double y) {
        int estacionCerca = estacionMasCercana(x, y);
        double distanciaCercana = calcularDistancia(x, y, estacionX[estacionCerca], estacionY[estacionCerca]);
        double distanciaMetros = distanciaCercana * 1000;
        System.out.println("Estacion mas cercana: " + nombreEstacion[estacionCerca] + " a " + distanciaCercana
                + " km (" + distanciaMetros + " m)");
    }

    static String asignarUnidadesEmergencia(int idEmerg, String tipoSeleccionado, boolean hayIncendio, String nivelIncendioActual,
                                             boolean hayHeridos, int numHeridos) {
        int bomberosRequeridos = 0;
        int ambulanciasRequeridas = 0;
        int patrullasRequeridas;
        String explicacion;

        if (hayIncendio) {
            if (nivelIncendioActual.equals("Bajo")) bomberosRequeridos = 1;
            if (nivelIncendioActual.equals("Medio")) bomberosRequeridos = 3;
            if (nivelIncendioActual.equals("Alto")) bomberosRequeridos = 5;
        }

        if (hayHeridos) {
            ambulanciasRequeridas = numHeridos / 4;
            int resto = numHeridos - (numHeridos / 4) * 4;
            if (resto > 0) ambulanciasRequeridas++;
            patrullasRequeridas = ambulanciasRequeridas;
            if (tipoSeleccionado.equals("Robo")) {
                explicacion = "un robo con personas heridas";
            } else {
                explicacion = "una emergencia con personas heridas";
            }
            System.out.println("Personas heridas reportadas: " + numHeridos
                    + " -> se despacha 1 Ambulancia y 1 Patrulla por cada 4 heridos.");
        } else {
            if (tipoSeleccionado.equals("Robo")) {
                patrullasRequeridas = 1;
                ambulanciasRequeridas = 0;
                explicacion = "un robo sin personas heridas";
            } else {
                ambulanciasRequeridas = 0;
                patrullasRequeridas = 1;
                explicacion = "un incidente sin personas heridas reportadas";
            }
        }

        System.out.println("Regla aplicada: la emergencia corresponde a " + explicacion);
        if (hayIncendio) {
            System.out.println("Bomberos requeridos segun nivel de incendio (" + nivelIncendioActual + "): " + bomberosRequeridos);
        }

        estado[idEmerg] = "Pendiente";
        despacharUnidades(idEmerg, bomberosRequeridos, ambulanciasRequeridas, patrullasRequeridas);
        estado[idEmerg] = "Despachada";

        return explicacion;
    }

    /** Pregunta el tiempo de despacho DESPUES de saber que unidades se enviaron
     *  (se llama luego de asignarUnidadesEmergencia, no antes). */
    static void registrarTiempoDespacho(int idEmerg) {
        double minutosDespacho = leerRealPositivo("Tiempo que demoro en despachar (minutos): ");
        limpiarPantalla();
        tiempoDespacho[idEmerg] = minutosDespacho;
        sumaTiempoDespacho += minutosDespacho;
    }

    static void actualizarRankingTipo(String tipoSeleccionado) {
        int posicionTipo = indiceTipo(tipoSeleccionado);
        contadorTipo[posicionTipo]++;
    }

    static void despacharUnidades(int idEmerg, int bomberosRequeridos, int ambulanciasRequeridas, int patrullasRequeridas) {
        boolean hayFaltante = false;
        System.out.println("--- DESPACHO DE UNIDADES ---");

        for (int i = 1; i <= 10 && bomberosRequeridos > 0; i++) {
            if (bomberos[i] == 0) {
                bomberos[i] = 1;
                matrizAsignacion[idEmerg][i] = 1;
                bomberosRequeridos--;
                totalBomberosUsados++;
                System.out.println("  -> Bomberos unidad #" + i + " despachada.");
            }
        }
        if (bomberosRequeridos > 0) {
            System.out.println("  [ALERTA] Faltan " + bomberosRequeridos + " unidad(es) de Bomberos. Se solicita apoyo externo.");
            hayFaltante = true;
            contUnidadesApoyoExterno += bomberosRequeridos;
        }

        for (int i = 1; i <= 10 && ambulanciasRequeridas > 0; i++) {
            if (ambulancias[i] == 0) {
                ambulancias[i] = 1;
                matrizAsignacion[idEmerg][i + 10] = 1;
                ambulanciasRequeridas--;
                totalAmbulanciasUsadas++;
                System.out.println("  -> Ambulancia #" + i + " despachada.");
            }
        }
        if (ambulanciasRequeridas > 0) {
            System.out.println("  [ALERTA] Faltan " + ambulanciasRequeridas + " Ambulancia(s). Se solicita apoyo externo.");
            hayFaltante = true;
            contUnidadesApoyoExterno += ambulanciasRequeridas;
        }

        for (int i = 1; i <= 10 && patrullasRequeridas > 0; i++) {
            if (patrullas[i] == 0) {
                patrullas[i] = 1;
                matrizAsignacion[idEmerg][i + 20] = 1;
                patrullasRequeridas--;
                totalPatrullasUsadas++;
                System.out.println("  -> Patrulla #" + i + " despachada.");
            }
        }
        if (patrullasRequeridas > 0) {
            System.out.println("  [ALERTA] Faltan " + patrullasRequeridas + " Patrulla(s). Se solicita apoyo externo.");
            hayFaltante = true;
            contUnidadesApoyoExterno += patrullasRequeridas;
        }

        if (hayFaltante) {
            contApoyoExterno++;
        }
    }

    static void registrarLlegada() {
        if (totalEmergencias == 0) {
            System.out.println("No hay emergencias registradas.");
            return;
        }
        String mensajeId = "Numero de emergencia (1 a " + totalEmergencias + "): ";
        int idEmerg = leerEnteroEnRango(mensajeId, 1, totalEmergencias);
        if (!estado[idEmerg].equals("Despachada")) {
            System.out.println("Esta emergencia no esta en estado Despachada (estado actual: " + estado[idEmerg] + ").");
            return;
        }
        double minutosLlegada = leerRealPositivo("Tiempo que demoraron en llegar las unidades (minutos): ");
        limpiarPantalla();
        System.out.println("--- RESULTADO DE LLEGADA - EMERGENCIA #" + idEmerg + " ---");
        tiempoLlegada[idEmerg] = minutosLlegada;
        sumaTiempoLlegada += minutosLlegada;
        contLlegadasRegistradas++;
        estado[idEmerg] = "En Atencion";
        System.out.println("Emergencia #" + idEmerg + " actualizada a estado: EN ATENCION");
    }

    static void finalizarEmergencia() {
        if (totalEmergencias == 0) {
            System.out.println("No hay emergencias registradas aun.");
            return;
        }
        String mensajeId = "Numero de emergencia a finalizar (1 a " + totalEmergencias + "): ";
        int idFinalizar = leerEnteroEnRango(mensajeId, 1, totalEmergencias);

        boolean puedeFinalizar = estado[idFinalizar].equals("En Atencion");
        if (!puedeFinalizar) {
            System.out.println("No se puede finalizar: la emergencia debe estar en estado EN ATENCION (registre primero la llegada de las unidades con la opcion 2). Estado actual: "
                    + estado[idFinalizar]);
            return;
        }

        double minutosAtencion = leerRealPositivo("Tiempo que duro la atencion (minutos): ");
        limpiarPantalla();
        tiempoAtencion[idFinalizar] = minutosAtencion;
        sumaTiempoAtencion += minutosAtencion;
        contEmergenciasFinalizadas++;
        System.out.println("--- LIBERANDO UNIDADES DE LA EMERGENCIA #" + idFinalizar + " ---");

        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idFinalizar][i] == 1) {
                bomberos[i] = 0;
                matrizAsignacion[idFinalizar][i] = 0;
                System.out.println("  -> Bombero #" + i + " disponible nuevamente.");
            }
        }
        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idFinalizar][i + 10] == 1) {
                ambulancias[i] = 0;
                matrizAsignacion[idFinalizar][i + 10] = 0;
                System.out.println("  -> Ambulancia #" + i + " disponible nuevamente.");
            }
        }
        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idFinalizar][i + 20] == 1) {
                patrullas[i] = 0;
                matrizAsignacion[idFinalizar][i + 20] = 0;
                System.out.println("  -> Patrulla #" + i + " disponible nuevamente.");
            }
        }
        estado[idFinalizar] = "Finalizada";
        System.out.println("Emergencia #" + idFinalizar + " FINALIZADA. Duracion de atencion: " + minutosAtencion + " minutos.");
    }

    static void mostrarEstadoUnidades() {
        System.out.println("--- ESTADO ACTUAL DE UNIDADES (0=Libre, 1=Ocupada) ---");
        System.out.println("BOMBEROS:");
        for (int i = 1; i <= 10; i++) {
            System.out.print("[" + bomberos[i] + "] ");
        }
        System.out.println();
        System.out.println("AMBULANCIAS:");
        for (int i = 1; i <= 10; i++) {
            System.out.print("[" + ambulancias[i] + "] ");
        }
        System.out.println();
        System.out.println("PATRULLAS:");
        for (int i = 1; i <= 10; i++) {
            System.out.print("[" + patrullas[i] + "] ");
        }
        System.out.println();
    }

    static void mostrarHistorial() {
        System.out.println("--- HISTORIAL DE EMERGENCIAS ---");
        if (totalEmergencias == 0) {
            System.out.println("Aun no hay emergencias registradas.");
            return;
        }
        for (int i = 1; i <= totalEmergencias; i++) {
            System.out.println("#" + codEmergencia[i] + " | Tipo: " + tipoEmergencia[i] + " | Prioridad: "
                    + puntajePrioridad[i] + " (" + nivelPrioridad[i] + ") | Estado: " + estado[i]);
        }
    }

    static void mostrarDetalleEmergencia() {
        if (totalEmergencias == 0) {
            System.out.println("Aun no hay emergencias registradas.");
            return;
        }
        String mensajeId = "Numero de emergencia a consultar (1 a " + totalEmergencias + "): ";
        int idEmerg = leerEnteroEnRango(mensajeId, 1, totalEmergencias);

        System.out.println("======================================================");
        System.out.println("  DETALLE DE LA EMERGENCIA #" + codEmergencia[idEmerg]);
        System.out.println("======================================================");
        System.out.println("Tipo: " + tipoEmergencia[idEmerg]);
        System.out.println("Ubicacion (X,Y): (" + ubicacionX[idEmerg] + ", " + ubicacionY[idEmerg] + ") km");
        System.out.println("Hora de recepcion: " + horaRecepcionH[idEmerg] + ":" + horaRecepcionM[idEmerg]);
        System.out.println("Dia del mes: " + diaRegistro[idEmerg]);
        System.out.println("Heridos: " + (heridos[idEmerg] ? "V" : "F"));
        System.out.println("Riesgo de vida: " + (riesgoVida[idEmerg] ? "V" : "F"));
        System.out.println("Incendio: " + (incendio[idEmerg] ? "V" : "F"));
        if (incendio[idEmerg]) {
            System.out.println("Nivel del incendio: " + nivelIncendio[idEmerg]);
        }
        System.out.println("Prioridad: " + puntajePrioridad[idEmerg] + " puntos (" + nivelPrioridad[idEmerg] + ")");
        System.out.println("Estado actual: " + estado[idEmerg]);
        System.out.println();
        System.out.println("--- TIEMPOS REGISTRADOS ---");
        System.out.println("Tiempo de despacho: " + tiempoDespacho[idEmerg] + " min");
        if (tiempoLlegada[idEmerg] > 0) {
            System.out.println("Tiempo de llegada: " + tiempoLlegada[idEmerg] + " min");
        } else {
            System.out.println("Tiempo de llegada: aun no registrado");
        }
        if (tiempoAtencion[idEmerg] > 0) {
            System.out.println("Duracion de atencion: " + tiempoAtencion[idEmerg] + " min");
        } else {
            System.out.println("Duracion de atencion: aun no registrada");
        }
        System.out.println();
        System.out.println("--- UNIDADES ASIGNADAS ---");
        boolean hayUnidad = false;
        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idEmerg][i] == 1) {
                System.out.println("  Bombero #" + i);
                hayUnidad = true;
            }
        }
        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idEmerg][i + 10] == 1) {
                System.out.println("  Ambulancia #" + i);
                hayUnidad = true;
            }
        }
        for (int i = 1; i <= 10; i++) {
            if (matrizAsignacion[idEmerg][i + 20] == 1) {
                System.out.println("  Patrulla #" + i);
                hayUnidad = true;
            }
        }
        if (!hayUnidad) {
            System.out.println("  (Sin unidades asignadas actualmente; la emergencia ya fue finalizada o no tuvo unidades disponibles)");
        }
    }

    // ================= SUBPROCESOS: REPORTES =================

    static void generarReportes() {
        System.out.println("======================================================");
        System.out.println("           REPORTE ESTADISTICO Y MATEMATICO            ");
        System.out.println("======================================================");
        if (totalEmergencias == 0) {
            System.out.println("No hay datos suficientes para generar reportes.");
            return;
        }
        mostrarReporteTiempos();
        mostrarReportePrioridadYTipos();
        mostrarReporteRecursos();
        mostrarReporteTemporal();
        mostrarTablasVerdad();
    }

    static void mostrarReporteTiempos() {
        double promDespacho = sumaTiempoDespacho / totalEmergencias;
        double promPrioridad = sumaPrioridad / totalEmergencias;
        System.out.println("1. Total de emergencias registradas: " + totalEmergencias);
        System.out.println("2. Tiempo promedio de despacho: " + promDespacho + " min");
        if (contLlegadasRegistradas > 0) {
            double promLlegada = sumaTiempoLlegada / contLlegadasRegistradas;
            System.out.println("3. Tiempo promedio de llegada: " + promLlegada + " min");
        } else {
            System.out.println("3. Tiempo promedio de llegada: sin datos (ninguna llegada registrada todavia)");
        }
        if (contEmergenciasFinalizadas > 0) {
            double promAtencion = sumaTiempoAtencion / contEmergenciasFinalizadas;
            System.out.println("4. Duracion promedio de atencion: " + promAtencion + " min");
        } else {
            System.out.println("4. Duracion promedio de atencion: sin datos (ninguna emergencia finalizada todavia)");
        }
        System.out.println("5. Indice de prioridad promedio: " + promPrioridad + " puntos");
    }

    static void mostrarReportePrioridadYTipos() {
        System.out.println();
        System.out.println("6. Emergencias por nivel de prioridad:");
        System.out.println("   Alta: " + contPrioridadAlta + " | Media: " + contPrioridadMedia + " | Baja: " + contPrioridadBaja);
        System.out.println();
        System.out.println("7. Emergencias por tipo (ranking de frecuencia):");
        for (int i = 1; i <= 3; i++) {
            System.out.println("   " + nombreTipo[i] + ": " + contadorTipo[i]);
        }
        int maxIdx = 1;
        int maxVal = contadorTipo[1];
        for (int i = 2; i <= 3; i++) {
            if (contadorTipo[i] > maxVal) {
                maxVal = contadorTipo[i];
                maxIdx = i;
            }
        }
        System.out.println("   >> Tipo mas frecuente: " + nombreTipo[maxIdx] + " (" + maxVal + " casos)");
    }

    static void mostrarReporteRecursos() {
        System.out.println();
        System.out.println("8. Recursos utilizados (unidades despachadas historicamente):");
        System.out.println("   Bomberos: " + totalBomberosUsados + " | Ambulancias: " + totalAmbulanciasUsadas
                + " | Patrullas: " + totalPatrullasUsadas);
        System.out.println();
        System.out.println("9. Apoyo externo solicitado:");
        System.out.println("   Emergencias que requirieron apoyo externo: " + contApoyoExterno);
        System.out.println("   Total de unidades que faltaron (Bomberos+Ambulancias+Patrullas): " + contUnidadesApoyoExterno);
    }

    static void mostrarReporteTemporal() {
        System.out.println();
        System.out.println("10. Reporte semanal (emergencias por semana del mes):");
        for (int i = 1; i <= 5; i++) {
            if (emergenciasPorSemana[i] > 0) {
                System.out.println("   Semana " + i + ": " + emergenciasPorSemana[i] + " emergencia(s)");
            }
        }
        System.out.println("11. Reporte mensual: total del mes = " + totalEmergencias + " emergencias");
    }

    static void mostrarTablasVerdad() {
        System.out.println();
        System.out.println("12. Tablas de verdad que justifican las decisiones automaticas:");
        System.out.println();
        System.out.println("  Regla: Incendio Y Heridos -> despachar Bomberos y Ambulancias");
        System.out.println("  Incendio | Heridos | Resultado (Y)");
        System.out.println("     V     |    V    |  V  (se despachan Bomberos y Ambulancias)");
        System.out.println("     V     |    F    |  F");
        System.out.println("     F     |    V    |  F");
        System.out.println("     F     |    F    |  F");
        System.out.println();
        System.out.println("  Regla: Riesgo de vida O Heridos > 5 -> Prioridad se incrementa");
        System.out.println("  Riesgo | Heridos>5 | Resultado (O)");
        System.out.println("     V    |     V       |  V (se activa la condicion)");
        System.out.println("     V    |     F       |  V");
        System.out.println("     F    |     V       |  V");
        System.out.println("     F    |     F       |  F");
    }

    // ================= FUNCIONES =================

    static int leerEnteroEnRango(String mensaje, int minVal, int maxVal) {
        int valor;
        boolean rangoOk;
        do {
            System.out.println(mensaje);
            String entradaTexto = teclado.nextLine().trim();
            rangoOk = true;
            valor = Integer.MIN_VALUE;
            try {
                valor = Integer.parseInt(entradaTexto);
                if (valor < minVal || valor > maxVal) {
                    rangoOk = false;
                }
            } catch (NumberFormatException e) {
                rangoOk = false;
            }
            if (!rangoOk) {
                System.out.println("  [ERROR] Ingrese un numero entero valido entre " + minVal + " y " + maxVal + ". Intente nuevamente.");
            }
        } while (!rangoOk);
        return valor;
    }

    static double leerReal(String mensaje) {
        double valor;
        boolean formatoOk;
        do {
            System.out.println(mensaje);
            String entradaTexto = teclado.nextLine().trim().replace(',', '.');
            formatoOk = true;
            valor = 0;
            try {
                valor = Double.parseDouble(entradaTexto);
            } catch (NumberFormatException e) {
                formatoOk = false;
            }
            if (!formatoOk) {
                System.out.println("  [ERROR] Ingrese un numero valido. Intente nuevamente.");
            }
        } while (!formatoOk);
        return valor;
    }

    static double leerRealPositivo(String mensaje) {
        double valor;
        boolean formatoOk;
        do {
            System.out.println(mensaje);
            String entradaTexto = teclado.nextLine().trim().replace(',', '.');
            formatoOk = true;
            valor = 0;
            try {
                valor = Double.parseDouble(entradaTexto);
                if (valor <= 0) {
                    formatoOk = false;
                }
            } catch (NumberFormatException e) {
                formatoOk = false;
            }
            if (!formatoOk) {
                System.out.println("  [ERROR] Ingrese un numero positivo valido. Intente nuevamente.");
            }
        } while (!formatoOk);
        return valor;
    }

    static boolean leerLogico(String mensaje) {
        String entradaTexto;
        boolean formatoOk;
        do {
            System.out.println(mensaje);
            entradaTexto = teclado.nextLine().trim().toUpperCase();
            formatoOk = entradaTexto.equals("V") || entradaTexto.equals("F");
            if (!formatoOk) {
                System.out.println("  [ERROR] Responda unicamente con V (Verdadero) o F (Falso). Intente nuevamente.");
            }
        } while (!formatoOk);
        return entradaTexto.equals("V");
    }

    static int calcularPrioridad(boolean hayRiesgoVida, boolean hayIncendio, String nivelIncendioActual, int numHeridos) {
        int puntaje = 0;
        if (hayRiesgoVida) {
            puntaje += 40;
        }
        if (hayIncendio) {
            if (nivelIncendioActual.equals("Bajo")) puntaje += 10;
            if (nivelIncendioActual.equals("Medio")) puntaje += 20;
            if (nivelIncendioActual.equals("Alto")) puntaje += 30;
        }
        if (numHeridos > 5) {
            puntaje += 20;
        }
        return puntaje;
    }

    static String clasificarPrioridad(int puntaje) {
        if (puntaje > 60) {
            return "Alta";
        } else if (puntaje >= 31) {
            return "Media";
        } else {
            return "Baja";
        }
    }

    static int indiceTipo(String tipo) {
        int indiceEncontrado = 3;
        for (int numeroTipo = 1; numeroTipo <= 3; numeroTipo++) {
            if (tipo.equals(nombreTipo[numeroTipo])) {
                indiceEncontrado = numeroTipo;
            }
        }
        return indiceEncontrado;
    }

    static double calcularDistancia(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    static int estacionMasCercana(double x, double y) {
        int indiceEstacionCercana = 1;
        double distanciaMinima = calcularDistancia(x, y, estacionX[1], estacionY[1]);
        for (int numeroEstacion = 2; numeroEstacion <= 3; numeroEstacion++) {
            double distanciaActual = calcularDistancia(x, y, estacionX[numeroEstacion], estacionY[numeroEstacion]);
            if (distanciaActual < distanciaMinima) {
                distanciaMinima = distanciaActual;
                indiceEstacionCercana = numeroEstacion;
            }
        }
        return indiceEstacionCercana;
    }

}