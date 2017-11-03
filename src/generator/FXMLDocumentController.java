/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.hssf.usermodel.HSSFRow;

/**
 *
 * @author Archax
 */
public class FXMLDocumentController implements Initializable {
    
    private int quantity;
    private List<Column> list;
    
    @FXML
    private void handleQuitAction(ActionEvent event) {
        System.out.println("quit");
        Platform.exit();
    }
    
    @FXML
    private void handleOpenAction(ActionEvent event) {
        System.out.println("open");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);
        while (!"application/octet-stream".equals(new MimetypesFileTypeMap().getContentType(file))){
            file = fileChooser.showOpenDialog(null);
        }       
        prepareFile(file);
    }
    
   @FXML
    private void handleGenerateAction(ActionEvent event) {
        System.out.println("Generate");
        String filename = "D:/NewExcelFile.xls" ;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("FirstSheet"); 
        List<HSSFRow> rList = new ArrayList();
        for (int i = 0; i < quantity + 1; i++){
            rList.add(sheet.createRow((short)i));
        }
        for (int i = 0; i < list.size(); i++){
            list.get(i).Generate(rList, i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quantity = 0;
        list = new ArrayList();
    }    
    
    private void prepareFile(File file){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element t = (Element) doc.getElementsByTagName("Columns").item(0);
            quantity = Integer.parseInt(t.getAttribute("quantity"));
            NodeList nList = doc.getElementsByTagName("Column");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);  
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;                    
                    String name = eElement
                            .getElementsByTagName("name")
                            .item(0)
                            .getTextContent();
                    Boolean numbers = Boolean.getBoolean(eElement
                            .getElementsByTagName("numbers")
                            .item(0)
                            .getTextContent());
                                  
                    List<Relation> relations = new ArrayList();
                    if (eElement.hasAttribute("relations")){
                        NodeList rList = eElement.getElementsByTagName("relations");
                        for (int j = 0; j < eElement.getElementsByTagName("relations").getLength(); j++){
                            Node rNode = rList.item(j);  
                            if (rNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element rElement = (Element) rNode; 
                                String type = rElement
                                    .getElementsByTagName("type")
                                    .item(0)
                                    .getTextContent();
                                String element = rElement
                                    .getElementsByTagName("element")
                                    .item(0)
                                    .getTextContent();
                                String dir = rElement
                                    .getElementsByTagName("dir")
                                    .item(0)
                                    .getTextContent();
                                relations.add(new Relation(type, element, dir));
                            }
                        }
                    }                    
                    String type = eElement
                            .getElementsByTagName("type")
                            .item(0)
                            .getTextContent();
                    
                    String max = eElement
                            .getElementsByTagName("max")
                            .item(0)
                            .getTextContent();
                            
                    String min = eElement
                            .getElementsByTagName("min")
                            .item(0)
                            .getTextContent();
                    
                    list.add(new  Column(name, max, min, numbers, relations, type));

                }
            }
            this.test();
        } catch (IOException | ParserConfigurationException | SAXException e){
        }
    }
    
    private void test(){
        list.forEach((c) -> {
            System.out.println(c.toString());
        });
        System.out.println("\n ilosc: " + this.quantity);
    }
    
}
