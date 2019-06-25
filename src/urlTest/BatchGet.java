package urlTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BatchGet {
    ExecutorService pool= Executors.newCachedThreadPool();
    private static List<String> urlList=new ArrayList<>();
    private String p_id;
    private int page;
    BatchGet(String p_id,int page){
    this.p_id=p_id;this.page=page;
    }

    public List<String> getList() {
        for (int i = 0; i < page; i++) {
            UrlHandler u=new UrlHandler(p_id,urlList,i);
            pool.submit(u);
        }
        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return urlList;
    }
    public static void main(String[] args){
        new BatchGet("a0a",100).getList();
       for(String url:urlList){
           System.out.println(url);
       }
    }
}

