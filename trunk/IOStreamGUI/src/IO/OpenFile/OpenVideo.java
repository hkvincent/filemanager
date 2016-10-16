/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO.OpenFile;

import IO.view.VideoPath;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Administrator
 */
public class OpenVideo implements Runnable {

    String player = null;
    boolean flag;
    File file;
    VideoPath vb;
    long time;

    public int count = 0;

    public OpenVideo() {

    }

    public OpenVideo(String str, String player, VideoPath jf, String time) {
        this.player = player;
        this.file = new File(str);
        vb = jf;
        this.time = Long.parseLong(time);

    }

    private synchronized void videoOpen(File file) throws IOException, InterruptedException {
        File[] arrFile = file.listFiles();
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(this.file + "\\List.txt", true));
        for (File f : arrFile) {
            if (f.isDirectory()) {
                videoOpen(f);
            } else {
                if (f.getName().endsWith(".mp4")
                        || f.getName().endsWith(".avi")
                        || f.getName().endsWith(".wmv")
                        || f.getName().endsWith(".mkv")) {
                    System.out.println(f.getName());
                    Runtime.getRuntime().exec(
                            player
                            .concat(" ").concat(f.getAbsolutePath()));
                    System.out
                            .println(player
                                    .concat(" ").concat(f.getAbsolutePath()));

                    vb.jpb.setValue(++count);
                    System.out.println(vb.jpb.getValue());
                    writer.write(count + ": " + f.getName());
                    writer.write("\r\n");
                    this.wait(this.time);

                }
            }
        }
        writer.close();
    }

//    private void Open(String desPath) throws IOException,
//            InterruptedException {
//        File file = new File(desPath);
//        videoOpen(file);
//
//    }
    @Override
    public void run() {
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
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return this.flag;
    }
}
