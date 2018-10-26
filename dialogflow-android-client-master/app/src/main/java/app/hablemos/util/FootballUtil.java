package app.hablemos.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import app.hablemos.asynctasks.GetDatosAsyncTask;
import app.hablemos.asynctasks.GetEfemeridesAsyncTask;
import app.hablemos.asynctasks.GetGoleadoresAsyncTask;
import app.hablemos.asynctasks.GetPartidosActualesAsyncTask;
import app.hablemos.asynctasks.GetPartidosAsyncTask;
import app.hablemos.asynctasks.GetResultadoUltimoPartidoAsyncTask;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

public class FootballUtil {

    public static HashMap<String,String> getMapaEquipos() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("aldosivi", "Aldosivi");
        mapa.put("argentinos", "Argentinos");
        mapa.put("atltucuman", "Atl Tucuman");
        mapa.put("banfield", "Banfield");
        mapa.put("belgrano", "Belgrano");
        mapa.put("bocajuniors", "Boca Juniors");
        mapa.put("colon", "Colon");
        mapa.put("defyjusticia", "Def y Justicia");
        mapa.put("estudianteslp", "Estudiantes (LP)");
        mapa.put("gimnasialp", "Gimnasia (LP)");
        mapa.put("godoycruz", "Godoy Cruz");
        mapa.put("huracan", "Huracan");
        mapa.put("independiente", "Independiente");
        mapa.put("lanus", "Lanus");
        mapa.put("newells", "Newells");
        mapa.put("patronato", "Patronato");
        mapa.put("racingclub", "Racing Club");
        mapa.put("riverplate", "River Plate");
        mapa.put("rosariocentral", "Rosario Central");
        mapa.put("sanlorenzo", "San Lorenzo");
        mapa.put("sanmartinsj", "San Martin (SJ)");
        mapa.put("sanmartint", "San Martin (T)");
        mapa.put("talleresc", "Talleres (C)");
        mapa.put("tigre", "Tigre");
        mapa.put("union", "Union");
        mapa.put("velez", "Velez");
        return mapa;
    }

    public static String obtenerMarcadores(String goles) {
        String separador = "";
        StringBuilder marcadores = new StringBuilder();
        String[] marcadoresArray = goles.split(";");
        int contador = 1;
        for (String str : marcadoresArray) {
            String[] submarcadoresArray = str.split("'");
            String tiempo = submarcadoresArray[0].trim();
            String marcador = submarcadoresArray[1].trim().replace("e.c.", "en contra").replace(".", "");
            if (contador == marcadoresArray.length) {
                marcadores.append(" y ");
            } else {
                marcadores.append(separador);
                separador = ", ";
            }
            marcadores.append(marcador).append(" a los ").append(tiempo).append(" minutos");
            contador++;
        }
        return marcadores.append(".").toString();
    }

    public static String modificarNombresEquiposPrimera(String input){
        input = input
                .replace("Argentinos Juniors", "Argentinos")
                .replace("Argentinos", "Argentinos Juniors")
                .replace("Atletico Tucuman", "Atl Tucumán")
                .replace("Atl Tucuman", "Atlético Tucumán")
                .replace("Belgrano (Cba)", "Belgrano")
                .replace("Belgrano", "Belgrano de Córdoba")
                .replace("Colon (SF)", "Colon")
                .replace("Colon", "Colón de Santa Fe")
                .replace("Def y Justicia", "Defensa y Justicia")
                .replace("Estudiantes (La Plata)", "Estudiantes (LP)")
                .replace("Estudiantes (LP)", "Estudiantes de La Plata")
                .replace("Gimnasia (La Plata)", "Gimnasia (LP)")
                .replace("Gimnasia (LP)", "Gimnasia y Esgrima de La Plata")
                .replace("Godoy Cruz (Mendoza)", "Godoy Cruz")
                .replace("Huracan", "Huracán")
                .replace("Lanus", "Lanús")
                .replace("Newells Old Boys", "Newells")
                .replace("Newells", "Newells Old Boys")
                .replace("San Martin (San Juan)", "San Martin (SJ)")
                .replace("San Martin (SJ)", "San Martín de San Juan")
                .replace("San Martin (Tucuman)", "San Martin (T)")
                .replace("San Martin (T)", "San Martín de Tucumán")
                .replace("Talleres (Cordoba)", "Talleres (C)")
                .replace("Talleres (C)", "Talleres de Córdoba")
                .replace("Union (Santa Fe)", "Union")
                .replace("Union", "Unión de Santa Fe")
                .replace("Velez Sarsfield", "Velez")
                .replace("Velez", "Velez Sarsfield");
        return input;
    }

    public static HashMap<String,String> getMapaConversionVisual() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("Argentinos", "Argentinos Juniors");
        mapa.put("Atletico Tucuman", "Atlético Tucumán");
        mapa.put("Belgrano (Cba)", "Belgrano de Córdoba");
        mapa.put("Colon (SF)", "Colón de Santa Fe");
        mapa.put("Estudiantes (La Plata)", "Estudiantes de La Plata");
        mapa.put("Gimnasia (La Plata)", "Gimnasia y Esgrima de La Plata");
        mapa.put("Godoy Cruz (Mendoza)", "Godoy Cruz");
        mapa.put("Huracan", "Huracán");
        mapa.put("Lanus", "Lanús");
        mapa.put("San Martin (San Juan)", "San Martín de San Juan");
        mapa.put("San Martin (Tucuman)", "San Martín de Tucumán");
        mapa.put("Talleres (Cordoba)", "Talleres de Córdoba");
        mapa.put("Union (Santa Fe)", "Unión de Santa Fe");
        return mapa;
    }

    public static String obtenerStringGolUnico(String marcador, String goles) {
        return "\nEl gol " + marcador + " lo marcó " +
                goles.split("'")[1].replace(";", "").replace(".", "").trim() +
                " a los " + goles.split("'")[0] + " minutos.";
    }

    public static String getGoleadores(List<String> goleadores) {
        String separador = "";
        StringBuilder respuesta = new StringBuilder();
        for (int i = 0; i < goleadores.size(); i++) {
            if (i == (goleadores.size() - 1)) {
                respuesta.append(" y ");
            } else {
                respuesta.append(separador);
                separador = ", ";
            }
            respuesta.append(goleadores.get(i).replace(". ", " ").replace(".", " "));
        }
        return respuesta.toString();
    }

    /* SERVICIOS SCRAPPING */
    public static DatosEquipo getDatosAsync(String pagina) {
        DatosEquipo datos = new DatosEquipo();
        try {
            datos = new GetDatosAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static List<Partido> getPartidosAsync(String pagina) {
        List<Partido> partidos = new ArrayList<>();
        try {
            partidos = new GetPartidosAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidos;
    }

    public static List<PartidoActual> getPartidosActuales() {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetPartidosActualesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    public static List<PartidoActual> getPartidosPasados(String fecha) {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetResultadoUltimoPartidoAsyncTask(fecha).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    public static List<String> getEfemerides() {
        List<String> cumples = new ArrayList<>();
        try {
            cumples = new GetEfemeridesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cumples;
    }

    public static TreeMap<Integer,List<String>> getGoleadores() {
        TreeMap<Integer, List<String>> goleadores = new TreeMap<>();
        try {
            goleadores = new GetGoleadoresAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goleadores;
    }

    /* COMPARATORS */
    public static Comparator<Partido> comparadorDeFecha = new Comparator<Partido>() {
        @Override
        public int compare(Partido partido1, Partido partido2) {
            return partido1.getDiaDePartido().compareTo(partido2.getDiaDePartido());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorDePromedios = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPromedio(), eq2.getPromedio());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorGolesAFavor = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getGolesAFavor(), eq2.getGolesAFavor());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorGolesEnContra = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getGolesEnContra(), eq2.getGolesEnContra());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorDiferenciaGol = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getDiferencia(), eq2.getDiferencia());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosGanados = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosGanados(), eq2.getPartidosGanados());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosEmpatados = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosEmpatados(), eq2.getPartidosEmpatados());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosPerdidos = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosPerdidos(), eq2.getPartidosPerdidos());
        }
    };
}