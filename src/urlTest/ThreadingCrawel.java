package urlTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadingCrawel {
    private static ExecutorService pool= Executors.newCachedThreadPool();
    private static List<String> contentList;

    public List<String> getContentText(){
        List<String> urlList=new BatchGet("",10).getList();
        for(String url :urlList){
            pool.execute(new ContentHandler(url,contentList));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return contentList;
    }
}
