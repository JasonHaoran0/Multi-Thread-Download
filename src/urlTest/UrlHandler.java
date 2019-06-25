package urlTest;


import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UrlHandler implements Runnable{
    private String p_id;
    private List<String> urlList;
    private int page;

    /**
    //饿汉模式：创建简单，但是不能灵活使用
    public static final UrlHandler urlHandler =new UrlHandler();

    private UrlHandler(){};

    public static UrlHandler getInstance(){
        return urlHandler;
    }
     */

    /**
    //懒汉模式:运用灵活，要考虑线程安全问题
    public static UrlHandler urlHandler;

    private UrlHandler(){}

    public static UrlHandler getInstance(){
            if(urlHandler==null){
                synchronized(UrlHandler.class){
                    if(urlHandler==null){
                        urlHandler=new UrlHandler();
                    }
                }
            }
            return urlHandler;
    }
*/

    /**
    //静态内部类,避免了同步的锁等待，又线程安全，避免反射实例化
    static class UrlHandleHolder{
        public static final UrlHandler INSTANCE=new UrlHandler();
    }
    public static UrlHandler getInstance(){
        return UrlHandleHolder.INSTANCE;
    }
*/
   private static Lock lock =new ReentrantLock();
    UrlHandler(String p_id,List<String> urlList,int page){
        this.p_id=p_id;this.urlList=urlList;this.page=page;
    }
    public String getPId(){
        return this.p_id;
    }
    public List<String> getUrlList(){
        return this.urlList;
    }
    public void addList(int page){
       lock.lock();
        try{//https://sclub.jd.com/comment/productPageComments.action?callback=fetchJSON_comment98vv35&productId=100002713257&score=0&sortType=5&page=1&pageSize=10&isShadowSku=0&rid=0&fold=1
            String url = "https://sclub.jd.com/comment/productPageComments.action?callback=fetchJSON_comment98vv35&productId="+
                    p_id+"&score=0&sortType=5&page="+page+"&pageSize=10&isShadowSku=0&rid=0&fold=1";
            urlList.add(url);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

@Override
    public void run(){
        addList(page);
}
}
