package app.hablemos.model.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import app.hablemos.asynctasks.GetDatosAsyncTask;
import app.hablemos.asynctasks.GetEfemeridesAsyncTask;
import app.hablemos.asynctasks.GetGoleadoresAsyncTask;
import app.hablemos.asynctasks.GetPartidosActualesAsyncTask;
import app.hablemos.asynctasks.GetPartidosAsyncTask;
import app.hablemos.asynctasks.GetPartidosProximaFechaAsyncTask;
import app.hablemos.asynctasks.GetPlantelAsyncTask;
import app.hablemos.asynctasks.GetPosicionesAsyncTask;
import app.hablemos.asynctasks.GetResultadoUltimoPartidoAsyncTask;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

public class ConversionMaps {

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

    public static String getReferenciaEquipo(String nombreEquipo) {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("ALD", "aldosivi");
        mapa.put("ARG", "argentinos");
        mapa.put("ATU", "atltucuman");
        mapa.put("BAN", "banfield");
        mapa.put("BEL", "belgrano");
        mapa.put("BOC", "bocajuniors");
        mapa.put("COL", "colon");
        mapa.put("DYJ", "defyjusticia");
        mapa.put("EST", "estudianteslp");
        mapa.put("GIM", "gimnasialp");
        mapa.put("GOD", "godoycruz");
        mapa.put("HUR", "huracan");
        mapa.put("IND", "independiente");
        mapa.put("LAN", "lanus");
        mapa.put("NOB", "newells");
        mapa.put("PAT", "patronato");
        mapa.put("RAC", "racingclub");
        mapa.put("RIV", "riverplate");
        mapa.put("CEN", "rosariocentral");
        mapa.put("SLO", "sanlorenzo");
        mapa.put("SMA", "sanmartinsj");
        mapa.put("SMT", "sanmartint");
        mapa.put("TAL", "talleresc");
        mapa.put("TIG", "tigre");
        mapa.put("UNI", "union");
        mapa.put("VEL", "velez");
        return mapa.get(nombreEquipo);
    }

    public static HashMap<String,String> getClasicos() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("aldosivi", "El clásico rival es Talleres Fútbol Club de Mar del Plata, pero por la diferencia de categorías durante tanto tiempo, ahora se lo rivaliza con el Club Atlético Alvarado en lo que se conoce como el \"Clásico Marplatense\". En primera división su rival es Tigre.");
        mapa.put("argentinos", "El clásico rival es el Club Atlético Platense y se conoce como el \"Clásico del Noroeste\", aunque también se lo rivaliza con el Club Atlético All Boys por la ubicación geográfica. En primera división su rival es Velez Sarsfield.");
        mapa.put("atltucuman", "El \"Clásico Tucumano\" une a Atlético Tucumán y a San Martín de Tucumán. Como sus nombres lo indican, ambos provenientes de la provincia más chica de Argentina.");
        mapa.put("banfield", "Su clásico rival de toda la vida es el Club Atlético Lanús en lo que se llama el \"Clásico del Sur\".");
        mapa.put("belgrano", "Su clásico rival es el Club Atlético Talleres de Córdoba en el \"Superclásico Cordobés\", ya que ambos se ubican en la misma provincia. También tiene rivalidad con Instituto de Córdoba por la misma razón.");
        mapa.put("bocajuniors", "Su clásico rival es River Plate. Al ser los dos equipos más populares del país se lo considera el \"Superclásico del fútbol argentino\".");
        mapa.put("colon", "Su rival de toda la vida es el Club Unión de Santa Fe, por la ubicación de ambos. A esta rivalidad se la llama el \"Clásico Santafesino\".");
        mapa.put("defyjusticia", "Defensa y Justicia tiene rivalidad con Quilmes y Arsenal de Sarandí, entre otras. Su rival en primera división es Patronato de Paraná.");
        mapa.put("estudianteslp", "Su rival del \"Clásico Platense\" es Gimnasia y Esgrima de La Plata ya que ambos se ubican en la misma ciudad. Al clásico también se lo conoce como \"Clásico de la ciudad de las diagonales\" o \"Clásico de La Plata\".");
        mapa.put("gimnasialp", "Su rival del \"Clásico Platense\" es Estudiantes de La Plata ya que ambos se ubican en la misma ciudad. Al clásico también se lo conoce como \"Clásico de la ciudad de las diagonales\" o \"Clásico de La Plata\".");
        mapa.put("godoycruz", "Su clásico rival es Andes Talleres Sport Club, con quien disputa el \"Clásico Godoycruceño\". Hoy en día tiene una rivalidad con San Martín de San Juan en el \"Clásico de Cuyo\".");
        mapa.put("huracan", "Su clásico de toda la vida es San Lorenzo de Almagro, por la cercanía en la que se encuentran.");
        mapa.put("independiente", "El clásico rival es Racing Club y al mismo se lo llama \"Clásico de Avellaneda\". La ubicación de sus estadios es la más cercana de todas, sólo los separan 300 metros.");
        mapa.put("lanus", "Su rival de toda la vida es el Club Atlético Banfield en el \"Clásico del Sur\".");
        mapa.put("newells", "El \"Clásico Rosarino\" es el clásico más importante de la ciudad de Rosario en la provincia de Santa Fe. Junta a Newells Old Boys con su rival eterno Rosario Central.");
        mapa.put("patronato", "Patronato tiene una clásica rivalidad deportiva con el Club Atlético Paraná, de la misma ciudad, con el que conforma el \"Clásico Paranaense\". En primera división lo disputa contra Defensa y Justicia.");
        mapa.put("racingclub", "Se lo rivaliza con el Club Atlético Independiente en el \"Clásico de Avellaneda\". La rivalidad aumenta al estar sus estadios tan cerca, sólo los separan 300 metros.");
        mapa.put("riverplate", "Su clásico rival es Boca Juniors. Al ser los dos equipos más populares del país se lo considera el \"Superclásico del fútbol argentino\".");
        mapa.put("rosariocentral", "El \"Clásico Rosarino\" es el clásico más importante de Rosario. Enfrenta a Rosario Central con su rival de toda la vida Newells Old Boys.");
        mapa.put("sanlorenzo", "Su clásico rival es el Club Atlético Huracán. Por la cercanía, se podría decir que es un clásico de barrio.");
        mapa.put("sanmartinsj", "El clásico más importante de la provincia es con el Club Sportivo Desamparados debido a que son los dos clubes más relevantes de la ciudad. Hoy en día se lo rivaliza en el \"Clásico de Cuyo\" con Godoy Cruz de Mendoza.");
        mapa.put("sanmartint", "El \"Clásico Tucumano\" une a San Martín de Tucumán y a Atlético Tucumán como rivales de toda la vida. Como sus nombres lo indican, ambos son provenientes de la provincia más chica de Argentina.");
        mapa.put("talleresc", "Uno de sus rivales clásicos es Instituto de Córdoba, aunque éste actualmente presenta rivalidad con Racing de Córdoba. Su principal rival es el Club Atlético Belgrano de Córdoba en el \"Superclásico Cordobés\", ya que ambos se ubican en la misma provincia. ");
        mapa.put("tigre", "Su rival histórico es el Club Atlético Platense con quien disputa el \"Clasico de Zona Norte\". En primera división disputa su clásico con Argentinos Juniors.");
        mapa.put("union", "Su rival de toda la vida es Colón de Santa Fe, por la ubicación de ambos. A esta rivalidad se la llama el \"Clásico Santafesino\".");
        mapa.put("velez", "Su clásico rival es Ferro Carril Oeste, en el llamado \"Clásico del Oeste\". Su rival en primera división es Argentinos Juniors.");
        return mapa;
    }

    public static HashMap<String,String> getDerby() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("aldosivi", "tigre");
        mapa.put("argentinos", "velez");
        mapa.put("atltucuman", "sanmartint");
        mapa.put("banfield", "lanus");
        mapa.put("belgrano", "talleresc");
        mapa.put("bocajuniors", "riverplate");
        mapa.put("colon", "union");
        mapa.put("defyjusticia", "patronato");
        mapa.put("estudianteslp", "gimnasialp");
        mapa.put("gimnasialp", "estudianteslp");
        mapa.put("godoycruz", "sanmartinsj");
        mapa.put("huracan", "sanlorenzo");
        mapa.put("independiente", "racingclub");
        mapa.put("lanus", "banfield");
        mapa.put("newells", "rosariocentral");
        mapa.put("patronato", "defyjusticia");
        mapa.put("racingclub", "independiente");
        mapa.put("riverplate", "bocajuniors");
        mapa.put("rosariocentral", "newells");
        mapa.put("sanlorenzo", "huracan");
        mapa.put("sanmartinsj", "godoycruz");
        mapa.put("sanmartint", "atltucuman");
        mapa.put("talleresc", "belgrano");
        mapa.put("tigre", "aldosivi");
        mapa.put("union", "colon");
        mapa.put("velez", "argentinos");
        return mapa;
    }
}