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
public class CopyFile {

    public CopyFile() {
    }

    ;
    public CopyFile(String srcFolder, String targetFolder, ArrayList<String> boxArr) throws IOException {
        copy(srcFolder, targetFolder,   boxArr);
    }

    private void copy(String srcFolder, String targetFolder, ArrayList<String> boxArr) throws IOException {
        File scrFile = new File(srcFolder);
        File desFile = new File(targetFolder);
        if (!desFile.exists()) {
            desFile.mkdirs();
        }
        fileAction(scrFile, desFile,boxArr);
    }

    private void fileAction(File scrFile, File destFile, ArrayList<String> boxArr) throws IOException {

        if (scrFile.isDirectory()) {

            File[] fileArray = scrFile.listFiles();
            for (File f : fileArray) {
//                System.out.println(f);
                fileAction(f, destFile,boxArr);

            }
        } else {
            for (String name : boxArr) {
                if (scrFile.getName().endsWith(name)) {
                    File newFile = new File(destFile, scrFile.getName());
                    copyFileStart(scrFile, newFile);
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
