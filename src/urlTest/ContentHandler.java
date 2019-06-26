package urlTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContentHandler implements Runnable {
    private static Lock lock=new ReentrantLock();
    private String url;
    private String cookie;
    private List<String> contentTexts;

    ContentHandler(String url, String cookie,List<String> contentTexts) {
        this.url = url;
        this.cookie=cookie;
        this.contentTexts = contentTexts;
    }

    private List<String> getContentText() {
//        lock.lock();
        BufferedReader br=null;
        StringBuffer content=new StringBuffer();
        try {
            URL u1 = new URL(url);
            HttpURLConnection connection1 = (HttpURLConnection) u1.openConnection();
            //设置请求头
            cookie=cookie.substring(0,cookie.indexOf(";"));
            connection1.addRequestProperty("Cookie",cookie);
            connection1.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            connection1.addRequestProperty("Referer","https://item.jd.com/5010050.html");
            if (connection1.getResponseCode() == 200) {
                br= new BufferedReader(new InputStreamReader(connection1.getInputStream(),"gbk"));
               String text="";
                while((text=br.readLine())!=null){
                   content.append(text);
               }
                   contentTexts.add(content.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            lock.unlock();
        }
        return contentTexts;
    }

    @Override
    public void run(){
        getContentText();
    }

}
