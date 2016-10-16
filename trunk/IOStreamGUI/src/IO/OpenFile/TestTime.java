/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO.OpenFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class TestTime {

    public int count;
    File file;
    public long time;

    public TestTime(String str) throws IOException, InterruptedException {

        this.file = new File(str);
        test(file);
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

    public void test(File file) {
        long startTime = System.currentTimeMillis();
        try {

            videoOpen(file);

        } catch (IOException ex) {
            Logger.getLogger(TestTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {

        }
        long endTime = System.currentTimeMillis();
        System.out.println("runtime of the program：" + (endTime - startTime) + "ms");
        this.time = (endTime - startTime);
    }
}
