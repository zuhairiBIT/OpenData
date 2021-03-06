package opendata;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import opendata.constant.Constant;
import opendata.constant.excel.SampleConstant;
import opendata.entity.Page;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static Page page;

    public static void main(String[] args){

        MongoClientURI uri = new MongoClientURI(Constant.URI);
        MongoClient mongoClient = new MongoClient(uri);
        MongoCursor<String> dbCursor = mongoClient.listDatabaseNames().iterator();

        Document doc = Jsoup.parse(SampleConstant.PAGE);
        Elements elements = doc.body().select(SampleConstant.SELECTOR);

        List<String> list = new ArrayList<>();
        String url = "";
        for(Element element : elements){
            url = element.attr("href");
            String[] temp = url.split("/");
            String filename = temp[temp.length -1];
            if (url.contains("http:")){
                list.add(SampleConstant.HOMEPAGE + url);
            } else if(url.charAt(0) == ','){
                url = url.substring(1);
                list.add(url);
            } else {
                list.add(SampleConstant.HOMEPAGE + url);
            }
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet httpRequest = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpRequest);
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity.equals(null)){
                    FileOutputStream fos = new FileOutputStream(filename);
                    httpEntity.writeTo(fos);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
