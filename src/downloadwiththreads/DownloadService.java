package downloadwiththreads;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadService {
    //下载地址
    private String url ;
    //下载目录
    private String filePath;
    //线程数量
    private int threadNum;
    DownloadService(String url,String filePath,int threadNum){
        this.url=url;
        this.filePath=filePath;
        this.threadNum=threadNum;
    }
    public HttpURLConnection getHttpCon() throws IOException {
        URL url=new URL(this.url);
        HttpURLConnection con= (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        return con;
    }
    //获取下载文件的名称
    private File getFileSavePath(){
        File dir=new File(filePath);
        //如果没有文件夹，创建文件夹
        if(!dir.exists()){
            dir.mkdir();
        }
        String filename=this.url.substring(this.url.lastIndexOf("/")+1);
        return   new File(this.filePath,filename);
    }

    public void download() throws IOException {
        //创建线程池
        ExecutorService pool= Executors.newCachedThreadPool();

        HttpURLConnection con=getHttpCon();

        if(con.getResponseCode()==200){
            long len= con.getContentLength();
            //预先占用磁盘空间
            RandomAccessFile raf=new RandomAccessFile(getFileSavePath(),"rw");
            raf.setLength(len);

            long bufferSize=len/threadNum;
            //开始下载
            System.out.println("开始下载。。。。");
            Long a=System.currentTimeMillis();
            for(int i=0;i<threadNum;i++){
                long  startIndex = i * bufferSize;
               long endIndex = (i+1) * bufferSize-1;
                if (i == (threadNum - 1)) {
                    endIndex = len;//是否减1
                }

                   //执行
                   RunDownload runDownload = new RunDownload(startIndex, endIndex, con, getFileSavePath());
                //execute 无返回参数，在run方法中捕获异常
                pool.execute(runDownload);
                //submit 可返回参数（实现Callable接口时），会吃掉异常
                /* Future f= pool.submit(runDownload);
                try {
                    f.get();
               }catch (Exception e){
                   e.printStackTrace();
               }*/
            }
            pool.shutdown();
            try {
                //阻塞当前线程，直到线程池中的线程执行完
                pool.awaitTermination(50, TimeUnit.SECONDS);
                System.out.println("下载完成。耗时："+(System.currentTimeMillis()-a)/1000+"s");

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

        }
    }

}
