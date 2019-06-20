package downloadwiththreads;

import java.io.IOException;

public class TestDownload {
    public static void main (String[] args){
        try {
            new DownloadService("http://xzc.197746.com/thunder58mwwdbyjb.zip", "D:\\downlaodtest\\", 3).download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
