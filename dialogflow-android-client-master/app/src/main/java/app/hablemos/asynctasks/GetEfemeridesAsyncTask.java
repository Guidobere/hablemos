package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
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
            String htmlAniversario = document.select("#cajadia div.eldia").get(1).parents().html();//$("#cajadia div.eldia")[3].parentElement.innerText.split("\n\n\n")
            Document documentAniversarios = Jsoup.parse(htmlAniversario);
            documentAniversarios.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            documentAniversarios.select("br").append("\\n");
            documentAniversarios.select("p").prepend("\\n\\n");
            String s = documentAniversarios.html().replaceAll("\\\\n", "\n");
            String htmlString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            String[] childrenAniversario = htmlString.split("\n\n\n\n\n\n");
            for(String str : childrenAniversario) {
                if (str.contains("Aniversarios:")) {
                    String[] anivs = str.split("Aniversarios:")[1].trim().split("\n\n\n\n\n");
                    for (String aniv : anivs) {
                        String anivParsed = aniv.replace("\n", "").replace(".", "").replace("hoy ", "").trim();
                        cumples.add(anivParsed);
                    }
                    break;
                }
            }
        }
        return cumples;
    }
}