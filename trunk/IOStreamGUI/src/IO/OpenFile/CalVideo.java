package IO.OpenFile;

import IO.OpenFile.OpenVideo;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class CalVideo implements Runnable {

    boolean flag;
    File file;
    public int count;
    public long time;

    public CalVideo() {

    }

    public CalVideo(String str) {

        this.file = new File(str);
    }

    private synchronized void videoOpen(File file) throws IOException, InterruptedException {
        File[] arrFile = file.listFiles();

        for (File f : arrFile) {
            if (f.isDirectory()) {
                System.out.println(f);
                videoOpen(f);
            } else if (f.getName().endsWith(".mp4")
                    || f.getName().endsWith(".avi")
                    || f.getName().endsWith(".wmv")
                    || f.getName().endsWith(".mkv")) {
                count++;
            }
        }

    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if (flag) {
            try {
                videoOpen(file);

            } catch (IOException ex) {
                Logger.getLogger(OpenVideo.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(OpenVideo.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
//            catch(NullPointerException e){
//                  
//            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("runtime of the program：" + (endTime - startTime) + "ms");
        this.time = (endTime - startTime);

    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public long check() {
        return time;
    }
}
