/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO.FileAction;

import java.io.File;

/**
 *
 * @author Administrator
 */
public class CutNPaste {

    public CutNPaste() {
        
    }
    
    public CutNPaste(String str){
        moveFile(str);
    }

    private void moveFile(String desPath) {
        File file = new File(desPath);
        File[] fileArray = file.listFiles();

        System.out.println(file);
        for (File f : fileArray) {
            if (!f.isDirectory()) {
                String name = f.getName();
                // System.out.println(f.getName());
                int index = name.lastIndexOf(".");
                String exceptName = name.substring(index + 1);
				// System.out.println(exceptName);
                // System.out.println(!exceptName.equals("torrent"));
                if (!exceptName.equals("torrent") && !exceptName.equals("cfg")
                        && !exceptName.equals("downloading")
                        && !exceptName.equals("tud")
                        && !exceptName.equals("td")) {
                    if (index > 0) {
                        String numberString = name.substring(0, index);
                        File Folder = new File(file, numberString);
                        // System.out.println(Folder);
                        System.out.println(Folder.mkdir());
                        File fileObject = new File(file, name);
                        File newFileObject = new File(file.getAbsolutePath()
                                + "/" + numberString + "//" + name);
                        fileObject.renameTo(newFileObject);
                    }
                }
            }
        }

    }
}
