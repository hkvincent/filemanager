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
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.xmlbeans.XmlException;

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
    private boolean flag;
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
            table.getColumnModel().getColumn(1).setCellRenderer(new MyRender(nameList, table));//set render
            table.getColumnModel().getColumn(0).setMaxWidth(250);
            table.getColumnModel().getColumn(0).setMinWidth(250);
        }
        sf.setLayout(new BorderLayout(5, 5));

        jScrollPane1 = new JScrollPane(table);
        jScrollPane1.setPreferredSize(new Dimension(100, 160));

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
            if (flag) {
                search(scrFile, word);
            } else {
                if (!scrFile.getName().matches("^.+\\.(?i)(pdf)$")) {
                    search(scrFile, word);
                }
            }
        }
    }

    //get the file name to estimate what type of it and get content with different type method 
    private void search(File scrFile, String word) {
        //split the key word in different way
        //there are two way to split key word
        //space or hyphen
        //space 's meaning is any one key word contain in the file we search
        //hyphen 's meaning is all key word must contain in the file we seach
        String[] arrStr = null;
        String[] arrStrA = null;
        if (word.contains(" ")) {
            arrStr = word.split(" ");
        } else if (word.contains("-")) {
            arrStrA = word.split("-");
            System.out.println("reach");
        }

        //regular expression mean suffixes must contain doc.
        boolean is03word = scrFile.getName().matches("^.+\\.(?i)(doc)$");
        if (is03word) {
            try {
                InputStream is = new FileInputStream(scrFile);
                WordExtractor ex = new WordExtractor(is);
                String text2003 = ex.getText();
                if (arrStr != null && arrStr.length > 0) {
                    //if keyword has space ,then we do spilt it
                    //invoke the method
                    finding(text2003, arrStr, scrFile);
                } else if (arrStrA != null && arrStrA.length > 0) {
                    //if keyword has hyphen, it mean that the file we search must contain these key word.
                    //we are using count varible to count the text of the file containing keyword whether enough or not.
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (text2003.toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    //if count varible if equal with amount of keyword that the file is we want.
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
                    finding(text2007, arrStr, scrFile);
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
                    finding(content, arrStr, scrFile);
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
            StringBuffer sb = new StringBuffer();
            try {
                in = new BufferedReader((new InputStreamReader(
                        new FileInputStream(scrFile), getCharset(scrFile.getAbsolutePath()))));
                String line = null;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                if (arrStr != null && arrStr.length > 0) {
                    if (finding(sb.toString(), arrStr, scrFile)) {
                        return;
                    }
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (sb.toString().contains(arrStrA[i])) {
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

            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else if (scrFile.getName().matches("^.+\\.(?i)(ppt)$")) {//find the key word in ppt file
            InputStream is = null;
            try {
                StringBuffer content = new StringBuffer("");
                is = new FileInputStream(scrFile);
                //get core API
                HSLFSlideShow ss = new HSLFSlideShow(is);
                //get how many page in this PPT
                List<HSLFSlide> slides = ss.getSlides();
                System.out.println("total have " + slides.size() + " page PPT");
                for (int i = 0; i < slides.size(); i++) {
                    //get each page of ppt content, retrun is List
                    List<List<HSLFTextParagraph>> textParagraphs = slides.get(i).getTextParagraphs();
                    if (textParagraphs != null) {
                        for (int j = 0; j < textParagraphs.size(); j++) {
                            content.append("\n");
                            //get each row of the page
                            List<HSLFTextParagraph> hslfTextParagraph = textParagraphs.get(j);
                            for (int f = 0; f < hslfTextParagraph.size(); f++) {
                                //get  the text of this row
                                content.append(hslfTextParagraph.get(f).toString());
                            }
                        }
                    }
                }
                if (arrStr != null && arrStr.length > 0) {
                    finding(content.toString(), arrStr, scrFile);
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (content.toString().toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    if (count == arrStrA.length) {
                        nameList.add(scrFile.getPath());
                    }
                } else if (content.toString().toLowerCase().contains(word.toLowerCase())) {
                    System.out.println("true");
                    nameList.add(scrFile.getPath());
                }

            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (scrFile.getName().matches("^.+\\.(?i)(pptx)$")) {//if powerpoint is 2007 or after we use this method
            String conetxt = null;
            try {
                conetxt = new XSLFPowerPointExtractor(POIXMLDocument.openPackage(scrFile.getPath())).getText();
                if (arrStr != null && arrStr.length > 0) {
                    finding(conetxt, arrStr, scrFile);
                } else if (arrStrA != null && arrStrA.length > 0) {
                    int count = 0;
                    for (int i = 0; i < arrStrA.length; i++) {
                        if (conetxt.toLowerCase().contains(arrStrA[i].toLowerCase())) {
                            count++;
                        }
                    }
                    if (count == arrStrA.length) {
                        nameList.add(scrFile.getPath());
                    }
                } else if (conetxt.toLowerCase().contains(word.toLowerCase())) {
                    System.out.println("true");
                    nameList.add(scrFile.getPath());
                }
            } catch (Exception ex) {
                Logger.getLogger(SearchWordFile.class.getName()).log(Level.SEVERE, null, ex);
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
            //i put the getValuesAt method in actionperformed beacuse when we need to comfirm the path is in clicking button
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

    //The reason of this method return boolean is for stopping txt file while loop
    public boolean finding(String context, String[] str, File scrFile) {
        for (int i = 0; i < str.length; i++) {
            //may be each spacing key word contain hyphen
            //so we need to spilt it one more time in hyphen word.
            if (str[i].contains("-")) {
                String[] arrStr = str[i].split("-");
                int count = 0;
                for (int j = 0; j < arrStr.length; j++) {
                    if (context.toLowerCase().contains(arrStr[j].toLowerCase())) {
                        count++;
                    }
                }
                if (count == arrStr.length) {
                    nameList.add(scrFile.getPath());
                    return true;
                }
            } else {
                if (context.toLowerCase().contains(str[i].toLowerCase())) {
                    nameList.add(scrFile.getPath());
                    return true;
                }
            }
        }
        return false;
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

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
