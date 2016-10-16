package IO.CopyFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class CopyFileB {

    int count = 0;

    public CopyFileB() {
    }

    ;
    public CopyFileB(String srcFolder, String targetFolder, ArrayList<String> arr) throws IOException {
        copy(srcFolder, targetFolder, arr);
    }

    private void copy(String srcFolder, String targetFolder, ArrayList<String> arr) throws IOException {
        File scrFile = new File(srcFolder);
        File desFile = new File(targetFolder);
        if (!desFile.exists()) {
            desFile.mkdirs();
        }
        fileAction(scrFile, desFile, arr);
    }

    private void fileAction(File scrFile, File destFile, ArrayList<String> arr) throws IOException {

        if (scrFile.isDirectory()) {

            File[] fileArray = scrFile.listFiles();
            for (File f : fileArray) {
                System.out.println(f);
                fileAction(f, destFile, arr);

            }
        } else {
            for (String name : arr) {
                if (!(name == null)) {
                    if (scrFile.getName().endsWith(name)) {
                        File[] destFileArray = destFile.listFiles();
                        for (File f : destFileArray) {
                            System.out.println(scrFile.getName());
                            System.out.println(f.getName());
                            if (!(scrFile.getName().equals(f.getName()))) {
                                File newFile = new File(destFile, scrFile.getName());
                                copyFileStart(scrFile, newFile);

                            } else {
                                count++;
                                String countStr = String.valueOf(count) + scrFile.getName();
                                File newFile = new File(destFile, countStr);
                                copyFileStart(scrFile, newFile);
                            }
                        }
                        System.out.println("------------------");
                    }
                }
            }
        }
    }

    private void copyFileStart(File scrFile, File destFile)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                scrFile));
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile));

        byte[] bys = new byte[1024];
        int len = 0;

        while ((len = bis.read(bys)) != -1) {
            System.out.println(len);
            bos.write(bys, 0, len);
        }

        bos.close();
        bis.close();
    }
}
