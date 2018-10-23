package app.hablemos.footballActions;

import app.hablemos.services.FootballService;

public abstract class FootballActionExecutor {

    public abstract String ejecutarAccion(String[] pedido, String equipoAbuelo, FootballService footballService);
}