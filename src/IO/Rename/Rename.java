/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO.Rename;

import java.io.File;

/**
 *
 * @author Administrator
 */
public class Rename {

    public Rename() {

    }

    public Rename(String str) {
        createFile(str);
    }

    public static void createFile(String desPath) {
        File file = new File(desPath);
        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isDirectory()) {
                createFile(f.getPath());
            } else {
                String newName = f.getName().replaceAll(" ", "");
                // System.out.println(newName);
                File file1 = new File(file.getAbsolutePath() + "//" + newName);
                // System.out.println(file.getAbsolutePath() );
                // System.out.println(file1.getAbsolutePath());

                f.renameTo(file1);
            }
        }
        String newName = file.getName().replaceAll(" ", "");
        System.out.println(newName);
        int index = file.getAbsolutePath().lastIndexOf("\\");
        String pathName = file.getAbsolutePath().substring(0, index);
        File file2 = new File(pathName + "\\" + newName);
        System.out.println(file2.getAbsolutePath());
        file.renameTo(file2);
        System.out.println(file.getAbsolutePath());

    }
}
