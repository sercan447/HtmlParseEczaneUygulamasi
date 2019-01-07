package sercandevops.com.htmlparseeczaneuygulamasi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Adapter.EczaneAdapter;
import Model.Eczane;
import Model.EczaneDetay;


public class MainActivity extends AppCompatActivity{


    WebView webView;
    String tokenText="";
    TextView v;
    ListView listView;
    Spinner spinner;

    Document document;
    List<EczaneDetay> eczaneList;
    EczaneAdapter eczaneAdapter;

    final String ilceler[] = {"Adalar", "Arnavutköy", "Ataşehir", "Avcılar", "Bağcılar", "Bahçelievler", "Bakırköy",
            "Başakşehir", "Bayrampaşa", "Beşiktaş", "Beykoz", "Beylikdüzü", "Beyoğlu", "Büyükçekmece", "Çatalca", "Çekmeköy",
            "Esenler", "Esenyurt", "Eyüp", "Fatih", "Gaziosmanpaşa", "Güngören", "Kadıköy", "Kağıthane", "Kartal", "Küçükçekmece",
            "Maltepe", "Pendik", "Sancaktepe", "Sarıyer", "Şile", "Silivri", "Şişli", "Sultanbeyli", "Sultangazi", "Tuzla",
            "Ümraniye", "Üsküdar", "Zeytinburnu"};
    final int ilceId[] = {1, 33, 34, 2, 3, 4, 5, 35, 6, 7, 8, 36, 9, 10, 11, 37, 13, 38, 14, 15, 16, 17, 18, 19,
                            20, 21, 22, 23, 39, 24, 27, 25, 28, 26, 40, 29, 30, 31, 32};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);
        listView = (ListView)findViewById(R.id.listcomponentim);
        v = (TextView)findViewById(R.id.textview);
        webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsBridge(),"Android");

        this.getToken();

      /*  v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */


         ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ilceler);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position !=0){
                    String idm = String.valueOf(ilceId[position]);
                    getEczane(idm);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void getEczane(String id){

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                view.loadUrl("javascript:window.Android.htmlEczaneDetay(" +
                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        String URL = "http://apps.istanbulsaglik.gov.tr/Eczane/nobetci?id="+id+"&token="+tokenText.toString();
                Log.i("Cevapp",URL);
        webView.loadUrl(URL);
    }


    public void getToken(){
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view,String url){
                super.onPageFinished(view,url);

                view.loadUrl("javascript:window.Android.htmlContentForToken(" +
                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        webView.loadUrl("http://apps.istanbulsaglik.gov.tr/Eczane");
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){

                tokenText =(String)msg.obj;

            }else if(msg.what == 2){
              Eczane ecz = parseHtml((String)msg.obj);
              eczaneList = ecz.getEczaneDetay();
              eczaneAdapter = new EczaneAdapter(eczaneList,getApplicationContext(),MainActivity.this);

              listView.setAdapter(eczaneAdapter);
            }
        }
    };

    private Eczane parseHtml(String obj) {

        document = Jsoup.parse(obj);
        Elements table = document.select("table.ilce-nobet-detay");
        Elements ilceDetay = table.select("caption>b");

        Eczane eczane = new Eczane();
        eczane.setTarih(ilceDetay.get(0).text());
        eczane.setIlceIsmi(ilceDetay.get(1).text());

        List<EczaneDetay> eczaneDetayList = new ArrayList<>();
        Elements eczaneDetayElement = document.select("table.nobecti-eczane");


            for(Element el : eczaneDetayElement){

                EczaneDetay  eczaneDetay = getEczaneDetay(el);
                if(eczaneDetay != null){
                    eczaneDetayList.add(eczaneDetay);
                }
            }
            eczane.setEczaneDetay(eczaneDetayList);

         //Log.i("cevappp",""+eczane);

         return eczane;
    }

    public EczaneDetay getEczaneDetay(Element e){

        EczaneDetay eczaneDetay = new EczaneDetay();

        Elements eczaneIsmiTag = e.select("thead");
        String eczaneIsmi = eczaneIsmiTag.select("div").attr("title");

              eczaneDetay.setEczaneIsmi(eczaneIsmi);
        Elements trTags = e.select("tbody>tr");
        Elements adresTags = trTags.select("tr#adres");
        String adresTagsId = adresTags.select("td").get(1).text();
            eczaneDetay.setAdres(adresTagsId);
        Elements telTags = trTags.select("tr#Tel");
        String tel = telTags.select("label").get(1).text();
            eczaneDetay.setTelefon(tel);
        Element faxTags = trTags.get(2);
        String fax = faxTags.select("label").get(1).text();

            if(!fax.equals("")){
                eczaneDetay.setFax(fax);
            }

        Log.e("cevapp",""+eczaneDetay);

        return eczaneDetay;
    }



    class JsBridge extends MainActivity{


        @JavascriptInterface
        public void htmlContentForToken(String str){

            String s[] = str.split("token");

            if(s.length > 1){

              String token2[] = s[1].split(Pattern.quote("}"));
              String t = token2[0].replaceAll(" ","").replaceAll(":","").replaceAll("\"","");


                Message message = new Message();
                message.what = 1;
                message.obj = t;

                handler.sendMessage(message);
            }
        }//METHOD

        @JavascriptInterface
        public void htmlEczaneDetay(String str){

            Message message = new Message();
            message.what = 2;
            message.obj = str;

            handler.sendMessage(message);
        }


    }//CLASS
}
