/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO.search;

import IO.view.SearchFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author Administrator
 */
public class SearchWordFile implements Runnable {

    private List<String> nameList;
    private List<JButton> buttonList;
    private String path;
    private String word;
    private SearchFrame sf;
    JScrollPane jScrollPane1;

    public SearchWordFile() {
    }

    public SearchWordFile(String path, String word, SearchFrame sf) {
        this.path = path;
        this.word = word;
        this.sf = sf;
    }

    //the thread method for running ohter thread to do what we want, one of this function is avoiding GUI threan  no response
    @Override
    public void run() {
        if (jScrollPane1 != null) {
            sf.remove(jScrollPane1);
        }
        sf.jLabel3.setText("processing....");
        this.nameList = new ArrayList<String>();
        this.buttonList = new ArrayList<JButton>();
        JTable table = null;
        File filePath = new File(path);
        searchWord(filePath, word);

        if (nameList != null && nameList.size() > 0) {

//            for (String name : nameList) {
//                sf.jTextArea1.append(name + "\n\r");
//            }
            for (int i = 0; i < nameList.size(); i++) {
                final int ii = i;
                JButton button = new JButton("open");
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            final File file = new File(nameList.get(ii));
                            java.awt.Desktop.getDesktop().open(file);
                        } catch (IOException ex) {
                            Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                buttonList.add(button);
            }
            Object[][] forTable = new Object[nameList.size()][2];
            for (int i = 0; i < nameList.size(); i++) {
                for (int j = 0; j < 2; j++) {
                    if (j == 0) {
                        forTable[i][j] = nameList.get(i);
                    } else {
                        forTable[i][j] = buttonList.get(i);
                    }
                }
            }

            String[] title = {"path", "button"};
            table = new JTable(forTable, title);
            table.getColumnModel().getColumn(1).setCellEditor(new MyRender(nameList, table));//设置编辑器
            table.getColumnModel().getColumn(1).setCellRenderer(new MyRender(nameList, table));
            table.getColumnModel().getColumn(0).setMaxWidth(250);
            table.getColumnModel().getColumn(0).setMinWidth(250);
        }
        sf.setLayout(new BorderLayout(5, 5));

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setPreferredSize(new Dimension(100, 150));

        sf.add(jScrollPane1, BorderLayout.SOUTH);

        sf.jLabel3.setText("complete");

    }

    //the recursive functio for traversing specify folder, if the folder within other folder the function will traverse it too.
    public void searchWord(File scrFile, String word) {
        if (scrFile.isDirectory()) {

            File[] fileArray = scrFile.listFiles();
            for (File f : fileArray) {
//                System.out.println(f);
                searchWord(f, word);
            }
        } else {
            search(scrFile, word);
        }
    }

    //get the file name to estimate what type of it and get content with different type method 
    private void search(File scrFile, String word) {
        String[] arrStr = null;
        String[] arrStrA = null;
        if (word.contains(" ")) {
            arrStr = word.split(" ");
        } else if (word.contains("-")) {
            arrStrA = word.split("-");
            System.out.println("reach");
        }

        boolean is03word = scrFile.getName().matches("^.+\\.(?i)(doc)$");
        if (is03word) {
            try {
                InputStream is = new FileInputStream(scrFile);
                WordExtractor ex = new WordExtractor(is);
                String text2003 = ex.getText();
                if (arrStr != null && arrStr.length > 0) {
                    for (int i = 0; i < arrStr.length; i++) {
                        if (text2003.toLowerCase().contains(arrStr[i].toLowerCase())) {
                            nameList.add(scrFile.getPath());
                            return;
                        }
                    }
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (text2003.toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    if (count == arrStrA.length) {
                        nameList.add(scrFile.getPath());
                    }
                } else if (text2003.toLowerCase().contains(word.toLowerCase())) {
                    System.out.println("true");
                    nameList.add(scrFile.getPath());
                }
            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (scrFile.getName().matches("^.+\\.(?i)(docx)$")) {
            try {
                OPCPackage opcPackage = POIXMLDocument.openPackage(scrFile.getPath());
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                String text2007 = extractor.getText();
                if (arrStr != null && arrStr.length > 0) {
                    for (int i = 0; i < arrStr.length; i++) {
                        if (text2007.toLowerCase().contains(arrStr[i].toLowerCase())) {
                            nameList.add(scrFile.getPath());
                            return;
                        }
                    }
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (text2007.toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    if (count == arrStrA.length) {
                        nameList.add(scrFile.getPath());
                    }
                } else if (text2007.toLowerCase().contains(word.toLowerCase())) {
                    System.out.println("true");
                    nameList.add(scrFile.getPath());
                }
            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (scrFile.getName().matches("^.+\\.(?i)(pdf)$")) {

            FileInputStream input = null;
            PDDocument pdfDocument = null;
            try {

                input = new FileInputStream(scrFile);
                PDFParser pdfParser = new PDFParser(input);
                pdfParser.parse();
                pdfDocument = pdfParser.getPDDocument();
                PDFTextStripper stripper = new PDFTextStripper();
                String content = stripper.getText(pdfDocument);
                if (arrStr != null && arrStr.length > 0) {
                    for (int i = 0; i < arrStr.length; i++) {
                        if (content.toLowerCase().contains(arrStr[i].toLowerCase())) {
                            nameList.add(scrFile.getPath());
                            return;
                        }
                    }
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (content.toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    if (count == arrStrA.length) {
                        nameList.add(scrFile.getPath());
                    }
                } else if (content.toLowerCase().contains(word.toLowerCase())) {
                    System.out.println("true");
                    nameList.add(scrFile.getPath());

                }

            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    input.close();
                    pdfDocument.close();
                } catch (IOException ex) {
                    Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (scrFile.getName().matches("^.+\\.(?i)(txt)$")) {
            BufferedReader in = null;
            try {
                in = new BufferedReader((new InputStreamReader(
                        new FileInputStream(scrFile), getCharset(scrFile.getAbsolutePath()))));
                String line = null;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    if (arrStr != null && arrStr.length > 0) {
                        for (int i = 0; i < arrStr.length; i++) {
                            if (line.toLowerCase().contains(arrStr[i].toLowerCase())) {
                                nameList.add(scrFile.getPath());
                                return;
                            }
                        }
                    } else if (arrStrA != null && arrStrA.length > 0) {
                        int count = 0;
                        for (int i = 0; i < arrStrA.length; i++) {
                            if (line.contains(arrStrA[i])) {
                                count++;
                            }
                        }
                        if (count == arrStrA.length) {
                            nameList.add(scrFile.getPath());
                        }
                    } else if (line.toLowerCase().contains(word.toLowerCase())) {
                        System.out.println("true");
                        nameList.add(scrFile.getPath());
                        return;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    // the jtable render to display button on it and create action in button
    class MyRender extends AbstractCellEditor implements TableCellRenderer, ActionListener, TableCellEditor {

        private static final long serialVersionUID = 1L;
        private JButton button = null;
        private JTable table;
        private String path;
        List<String> list = null;

        public MyRender() {
            button = new JButton("open");
            button.addActionListener(this);
        }

        public MyRender(List<String> list, JTable table) {
            this.list = list;
            button = new JButton("open");
            this.table = table;
            button.addActionListener(this);

        }

        @Override
        public Object getCellEditorValue() {

            return null;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // TODO Auto-generated method stub
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //i put the get values at method in actionperformed beacuse when we need to comfirm the path is in clicking button
            //not other timing
            Object selected = table.getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn() - 1);
            this.path = (String) selected;
            System.out.println(selected);
            for (String str : list) {
                if (str.equals(path)) {

                    try {
                        java.awt.Desktop.getDesktop().open(new File(str));
                    } catch (IOException ex) {
                        Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            table.selectAll();

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            // TODO Auto-generated method stub
            return button;
        }

    }

    //get waht charset of the txt file using
    private String getCharset(String fileName) throws IOException {

        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();

        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFrame(SearchFrame sf) {
        this.sf = sf;
    }

    public List<String> getNameList() {
        return this.nameList;
    }
}
