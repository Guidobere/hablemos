package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.DatosEquipo;

public class GetEfemeridesAsyncTask extends AsyncTask<Void, Void, List<String>> {

    @Override
    protected List<String> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/calendario.php").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> cumples = new ArrayList<>();
        if (document != null) {
            Elements children = document.select("#cajadia div.eldia[style='background: #5ac382']").parents().get(0).children();//$("#cajadia div.eldia[style='background: #5ac382']").parent().children("div.cumplejuga").text()
            for (Element child : children) {
                if (child.hasClass("cumplejuga")) {
                    String cumple = child.text().replace(".", "").replace("hoy ", "");
                    cumples.add(cumple);
                }
            }
            String[] childrenAniversario = document.select("#cajadia div.eldia").get(3).parents().text().split("\n\n\n");//$("#cajadia div.eldia")[3].parentElement.innerText.split("\n\n\n")
            for(String str : childrenAniversario) {
                if (str.startsWith("Aniversarios:")) {
                    String[] aniversarios = str.trim().replace("Aniversarios:\n", "").trim().split("\n\n");
                    for(String aniv : aniversarios) {
                        cumples.add(aniv.replace(".", ""));
                    }
                }
            }
        }
        return cumples;
    }
}