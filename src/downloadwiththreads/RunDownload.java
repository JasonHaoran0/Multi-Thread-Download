package downloadwiththreads;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

public class RunDownload implements Runnable {
    private long startIndex;
    private long endIndex;
    private HttpURLConnection con;
    private File fullFilePath;

    public RunDownload(long startIndex, long endIndex, HttpURLConnection con, File fullFilePath) {
        this.con=con;
        this.startIndex=startIndex;
        this.endIndex=endIndex;
        this.fullFilePath=fullFilePath;
    }

    @Override
    public void run() {
        int as=3/0;
        try {
            File loadingFile=new File(fullFilePath.getPath()+".downloading");
            //断点下载
            /**
             *"rws"  打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“元数据”的每个更新都同步写入到基础存储设备。
             *
             * "rwd"  打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备
             */
            RandomAccessFile loading =  new RandomAccessFile(loadingFile,"rwd");
            if(loadingFile.exists()){
               String start_str= loading.readLine();
//               Long l3=loading.readLong();
               if(!StringUtils.isEmpty(start_str)){
                    this.startIndex=Integer.valueOf(start_str);
               }
            }

            HttpURLConnection con=(HttpURLConnection) this.con.getURL().openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            //设置分段下载的头信息。  Range:做分段数据请求用的。格式: Range bytes=0-1024  或者 bytes:0-1024
            con.setRequestProperty("Range", "bytes="+ startIndex + "-" + endIndex);
           System.out.println("线程："+Thread.currentThread().getName()+"  下载起点是："+startIndex);
           //链接，并获取输入流
           InputStream inputStream=con.getInputStream();
           //200：请求全部资源成功， 206代表部分资源请求成功
            if(con.getResponseCode()==206){
                RandomAccessFile downloadFile=new RandomAccessFile(fullFilePath,"rw");
               //设置当前线程写入起点
                downloadFile.seek(startIndex);
                byte[] buff=new byte[1024];
                int bufflen = 0,total=0;
                while ((bufflen = inputStream.read(buff)) >= 0) {
                    downloadFile.write(buff, 0, bufflen);
                    total +=bufflen;

                    /*
                     * 将当前现在到的位置保存到文件中
                     */
                    loading.seek(0);
                    loading.write((startIndex + total + "").getBytes("UTF-8"));

                }
                loading.close();
                inputStream.close();
                downloadFile.close();
                loadingFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
