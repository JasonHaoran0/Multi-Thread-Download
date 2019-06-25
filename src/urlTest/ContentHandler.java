package urlTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContentHandler implements Runnable {
    private static Lock lock=new ReentrantLock();
    private String url;
    private List<String> contentTexts;

    ContentHandler(String url, List<String> contentTexts) {
        this.url = url;
        this.contentTexts = contentTexts;
    }

    private List<String> getContentText() {
        lock.lock();
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            StringBuffer content=new StringBuffer();
            String text="";
            if (connection.getResponseCode() == 200) {
               BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
                text=br.readLine();
               while(text==null){
                   content.append(text);
               }
                contentTexts.add(content.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return contentTexts;
    }

    @Override
    public void run(){
        getContentText();
    }

}
