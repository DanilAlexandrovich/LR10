package e7_MyHTML;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class Myhtml {
    public static void main(String[] args) {
        try{
            // Получаем HTML-код страницы
            Document doc = Jsoup.connect("http://fat.urfu.ru/index.html").get();

            // Извлекаем список новостей
            Elements newsParent = doc.select("body > table > tbody > tr > td > div > table > " +
                    "tbody > tr:nth-child(5) > td:nth-child(3) > table > tbody > " +
                    "tr > td:nth-child(1)");

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Создание корневого элемента
            org.w3c.dom.Document xmldoc = docBuilder.newDocument();
            org.w3c.dom.Element rootElement = xmldoc.createElement("urfunews");
            xmldoc.appendChild(rootElement);

            // Выводим последние 10 новостей в консоль
            for(int i=3;i<20;i++){
                if(!(i % 2 == 0)){
                    List<Node> nodes = newsParent.get(0).childNodes();
                    Node titleNode = ((Element) nodes.get(i)).getElementsByClass("blocktitle").get(0).childNodes().get(0);
                    Node dateNode = ((Element) nodes.get(i)).getElementsByClass("blockdate").get(0).childNodes().get(0);

                    System.out.println("Тема: " + titleNode);
                    System.out.println("Дата: " + dateNode);

                    // Добавление первой книги
                    org.w3c.dom.Element item = xmldoc.createElement("newsitem");
                    rootElement.appendChild(item);

                    org.w3c.dom.Element newsdate = xmldoc.createElement("date");
                    newsdate.appendChild(xmldoc.createTextNode(dateNode.toString()));
                    item.appendChild(newsdate);

                    org.w3c.dom.Element newstitle = xmldoc.createElement("title");

                    newstitle.appendChild(xmldoc.createTextNode(titleNode.toString()));
                    item.appendChild(newstitle);
                }
            }

            // Запись XML-файла
            xmldoc.setXmlStandalone(true);
            xmldoc.normalizeDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(xmldoc);
            StreamResult result = new StreamResult(new File("src/e7_MyHTML/urfu-news.xml"));
            transformer.transform(source, result);
        }catch (HttpStatusException e){
            System.out.println("Не удаётся открыть ссылку. Проверьте корректность ссылки и повторите попытку.");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
