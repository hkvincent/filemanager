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
public class OpenVideo  {

    String player = null;


    public OpenVideo() {

    }

    public OpenVideo(String str, String player) {
        this.player = player;
        try {
            Open(str);
        } catch (IOException ex) {
            Logger.getLogger(OpenVideo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(OpenVideo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private  synchronized void  videoOpen(File file) throws IOException, InterruptedException {
        File[] arrFile = file.listFiles();
        for (File f : arrFile) {
            if (f.isDirectory()) {
                videoOpen(f);
            } else {
                if (f.getName().endsWith(".mp4")
                        || f.getName().endsWith(".avi")
                        || f.getName().endsWith(".wmv")
                        || f.getName().endsWith(".mkv")) {
                    System.out.println(f.getAbsolutePath());
                    Runtime.getRuntime().exec(
                            player
                            .concat(" ").concat(f.getAbsolutePath()));
                    System.out
                            .println(player
                                    .concat(" ").concat(f.getAbsolutePath()));

                    this.wait(2000);

                }
            }
        }
    }

    private void Open(String desPath) throws IOException,
            InterruptedException {
        File file = new File(desPath);
        videoOpen(file);

    }


}
