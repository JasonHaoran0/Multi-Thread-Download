package urlTest;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadingCrawel {
    private static ExecutorService pool= Executors.newCachedThreadPool();
    private static List<String> contentList=new ArrayList<>();
    private String p_id;
    String cookie;

    ThreadingCrawel(String p_id){
        this.p_id=p_id;
    }

    public List<String> getContentText() throws IOException {
        List<String> urlList=new BatchGet(p_id,10).getList();
        for(String url :urlList){
            //获取cookie
            if(cookie==null) {
                URL u = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
                StringBuffer content = new StringBuffer();
                String text = "";
                connection.setConnectTimeout(10000);
//                Map<String, List<String>> headers = connection.getHeaderFields();
                cookie = connection.getHeaderField("set-cookie");
            }
            pool.execute(new ContentHandler(url,cookie,contentList));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return contentList;
    }

    public static  void main(String[] args){
        try {
            new ThreadingCrawel("100002713257").getContentText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String text:contentList){
            System.out.println(text);
        }
    }
}
