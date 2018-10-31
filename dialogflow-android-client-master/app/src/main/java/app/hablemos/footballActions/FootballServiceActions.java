package app.hablemos.footballActions;

import app.hablemos.services.FootballService;

public enum FootballServiceActions {

    POSICION("posicionEquipo", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getPosicionEquipo(equipoAbuelo);
                else
                    return footballService.getPosicionEquipo(equipo);
            }
        }
    }),
    TOPN("topN", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            int n = Integer.parseInt(pedido[2].trim());
            return footballService.getTopNEquipos(n);
        }
    }),
    BOTTOMN("bottomN", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            int j = Integer.parseInt(pedido[2].trim());
            return footballService.getBottomNEquipos(j);
        }
    }),
    TABLA("tablaDePosiciones", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getTablaPosiciones();
        }
    }),
    EQUIPOENPOSICION("equipoEnPosicion", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            int posicion = Integer.parseInt(pedido[2].trim());
            return footballService.getEquipoEnPosicion(posicion);
        }
    }),
    PROXIMOPARTIDO("proximoPartido", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getProximoPartido(equipoAbuelo);
                else
                    return footballService.getProximoPartido(equipo);
            }
        }
    }),
    ULTIMOPARTIDO("ultimoPartido", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getUltimoPartido(equipoAbuelo);
                else
                    return footballService.getUltimoPartido(equipo);
            }
        }
    }),
    DATOS("datosEquipo", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getDatosEquipo(equipoAbuelo);
                else
                    return footballService.getDatosEquipo(equipo);
            }
        }
    }),
    ESTADISTICAS("estadisticasEquipo", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getEstadisticasEquipo(equipoAbuelo);
                else
                    return footballService.getEstadisticasEquipo(equipo);
            }
        }
    }),
    COMPARACION("comparacionEquipos", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo1 = pedido[2].trim();
            String equipo2 = pedido[3].trim();
            if (!equipo1.equalsIgnoreCase(equipo2)) {
                if ((equipo1.equalsIgnoreCase("miEquipo") || equipo2.equalsIgnoreCase("miEquipo")) && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                    return "Su equipo no esta configurado, consulte a su tutor";
                } else {
                    if (equipo1.equalsIgnoreCase("miEquipo")) {
                        if (equipo2.equalsIgnoreCase(equipoAbuelo))
                            return "Ambos equipos son iguales, no se puede comparar";
                        return footballService.getComparacionEquipos(equipoAbuelo, equipo2);
                    } else if (equipo2.equalsIgnoreCase("miEquipo")) {
                        if (equipo1.equalsIgnoreCase(equipoAbuelo))
                            return "Ambos equipos son iguales, no se puede comparar";
                        return footballService.getComparacionEquipos(equipo1, equipoAbuelo);
                    } else
                        return footballService.getComparacionEquipos(equipo1, equipo2);
                }
            } else
                return "Ambos equipos son iguales, no se puede comparar";
        }
    }),
    LIBERTADORES("equiposLibertadores", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getLibertadores();
        }
    }),
    SUDAMERICANA("equiposSudamericana", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getSudamericana();
        }
    }),
    DESCENSO("equiposDescenso", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getDescienden();
        }
    }),
    EFEMERIDES("futbolEfemerides", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEfemerides();
        }
    }),
    GOLEADOR("futbolGoleador", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getGoleador();
        }
    }),
    EQUIPOMASGOLEADOR("equipoMasGoleador", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMasGoleador();
        }
    }),
    EQUIPOMASGOLEADO("equipoMasGoleado", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMasGoleado();
        }
    }),
    EQUIPOMAYORDIFERENCIA("equipoMayorDiferencia", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMayorDiferenciaDeGol();
        }
    }),
    EQUIPOMASPARTIDOSGANADOS("equipoMasPartidosGanados", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMasPartidosGanados();
        }
    }),
    EQUIPOMASPARTIDOSEMPATADOS("equipoMasPartidosEmpatados", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMasPartidosEmpatados();
        }
    }),
    EQUIPOMASPARTIDOSPERDIDOS("equipoMasPartidosPerdidos", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            return footballService.getEquipoMasPartidosPerdidos();
        }
    }),
    CLASICO("clasicoEquipo", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String equipo = pedido[2].trim();
            if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                return "Su equipo no esta configurado, consulte a su tutor";
            } else {
                if (equipo.equalsIgnoreCase("miEquipo"))
                    return footballService.getClasicoEquipo(equipoAbuelo);
                else
                    return footballService.getClasicoEquipo(equipo);
            }
        }
    }),
    CLASICOHISTORIAL("clasicoHistorialEquipo", new FootballActionExecutor() {

        @Override
        public String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService) {
            String respuesta = pedido[2].trim();
            if (respuesta.equalsIgnoreCase("si")) {
                String equipo = pedido[3].trim();
                if (equipo.equalsIgnoreCase("miEquipo") && equipoAbuelo.equalsIgnoreCase("ninguno")) {
                    return "Su equipo no esta configurado, consulte a su tutor";
                } else {
                    if (equipo.equalsIgnoreCase("miEquipo"))
                        return footballService.getHistorialClasico(equipoAbuelo);
                    else
                        return footballService.getHistorialClasico(equipo);
                }
            } else {
                return "irAlMenuYa";
            }
        }
    });

    private FootballActionExecutor footballActionExecutor;
    private String accion;

    FootballServiceActions(String accion, FootballActionExecutor footballActionExecutor) {
        this.accion = accion;
        this.footballActionExecutor = footballActionExecutor;
    }

    public FootballActionExecutor getFootballActionExecutor() {
        return footballActionExecutor;
    }

    public String getAccion() {
        return accion;
    }
}
