package e5_MyXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;
import java.util.zip.DataFormatException;

public class myxml {
    static String getCollectionPath() { return "src/e1_xml/ex.xml"; }

    public static void main(String[] args) {
        Document collection = getXMLDocument(getCollectionPath());
        int answer = 0;

        do{
            switch(answer) {
                case 1: showCollectionMenu(collection); break;
                case 2: add2CollectionMenu(collection); break;
                case 3: searchInCollectionMenu(collection, "year"); break;
                case 4: searchInCollectionMenu(collection, "author"); break;
                case 5: deleteFromCollection(collection); break;
            }

            answer = mainMenu();
        } while(answer != 0);
    }

    static int mainMenu(){
        System.out.flush();
        System.out.println("-------------------------------------");
        System.out.println("Добро пожаловать в коллекцию книг");
        System.out.println("-------------------------------------");
        System.out.println("Выберите действие:");
        System.out.println("1 - Просмотр коллекции");
        System.out.println("2 - Добавить книгу в коллекцию");
        System.out.println("3 - Найти книги по году выхода");
        System.out.println("4 - Найти книги по автору");
        System.out.println("5 - Удалить книгу");
        System.out.println("\n0 - Выход");

        int answ = -1;
        boolean isFirst = true;

        Scanner in = new Scanner(System.in);
        do{
            if(isFirst) isFirst = false;
            else System.out.println("Такого пункта нет в меню");

            System.out.print("\n \nВаш ответ: ");

            try {
                String input = in.nextLine();

                if((input.length() - input.replace(",", "").length()) == 1 || (input.length() - input.replace(".", "").length()) == 1)
                    throw new ClassCastException("Необходимо целое число");

                answ = Integer.parseInt(input.trim());

            } catch (ClassCastException e) {
                System.out.println("Ошибка:" + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Не верный формат числа");
            }
        }while(answ < 0 || answ > 5);

        return answ;
    }

    static void showCollectionMenu(Document collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Список книг в коллекции");
        System.out.println("-------------------------------------");
        NodeList nodeList = collection.getElementsByTagName("books");
        for(int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                printELement(node);
            }
        }
        waitForEnter();
    }

    static void add2CollectionMenu(Document collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Добавление книги в коллекцию");
        System.out.println("-------------------------------------");

        Scanner in = new Scanner(System.in);

        String inputTitle = "";
        String inputAuthor = "";
        int inputYear = 0;

        boolean valid;
        do {
            valid = true;
            System.out.print("\nВведите название книги: ");
            try {
                inputTitle = in.nextLine();
                if(inputTitle.isEmpty()) throw new DataFormatException();
            }catch(DataFormatException e){
                System.out.println("Ошибка! Название не должно быть пустым!");
                valid = false;
            }

            if(valid){
                List<Element> books = findInCollection(collection, "title", inputTitle);
                if(books.size() > 0){
                    System.out.println("Ошибка! Книга с таким названием уже есть в коллекции!");
                    valid = false;
                }
            }
        }while(!valid);

        do {
            valid = true;
            System.out.print("\nВведите автора: ");
            try {
                inputAuthor =  in.nextLine().toUpperCase(Locale.ROOT);
                if(inputAuthor.isEmpty()) throw new DataFormatException();
            }catch(DataFormatException e){
                System.out.println("Ошибка! Поле автор не должено быть пустым!");
                valid = false;
            }
        }while(!valid);


        Date curdate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(curdate);
        int year = calendar.get(Calendar.YEAR);

        do {
            valid = true;
            System.out.print("\nВведите год издания книги: ");
            try {
                inputYear = in.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Ошибка! Введите год цифрами!");
                valid = false;
            }
            finally {
                in.nextLine();
            }

            if(valid && (inputYear < 1850 || inputYear > year)){
                System.out.println("Ошибка! Введите год в промежутке между 1850 и " + year + " годами!");
                valid = false;
            }
        }while(!valid);

        if(addInCollection(collection, inputTitle, inputAuthor, inputYear)) {
            System.out.println("\nСледующая книга была успешно добавлена:");
            List<Element> books = findInCollection(collection, "title", inputTitle);
            if(books.size() == 1) printELement(books.get(0));
        }else{
            System.out.println("\nОшибка. книга не добавлена.");
        }
        waitForEnter();
    }

    static void searchInCollectionMenu(Document collection, String searchParam) {
        Scanner in = new Scanner(System.in);
        String inputWord = "";

        System.out.println("\n-------------------------------------");
        System.out.print("Поиск книги ");

        switch (searchParam) {
            case "year":
                System.out.println("по году выпуска");
                inputWord = "год издания";
                break;
            case "author":
                System.out.println("по автору");
                inputWord = "наименование автора";
                break;
        }
        System.out.println("-------------------------------------");
        System.out.print("\nВведите " + inputWord + ": ");
        String searchValue = in.nextLine();

        // Ищем
        List<Element> foundElements = findInCollection(collection, searchParam, searchValue);

        if (foundElements.size() == 0)
        {
            System.out.println("\nВ коллекции ничего не найдено");
        }else{
            System.out.println("\nВ коллекции найдено книг - " + foundElements.size() + " :");
            for(int i=0;i<foundElements.size();i++){
                Node node = foundElements.get(i);
                printELement(node);
            }
        }
        waitForEnter();
    }

    static void deleteFromCollection(Document collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Удаление книги из коллекции");
        System.out.println("-------------------------------------");

        Scanner in = new Scanner(System.in);

        String inputTitle = "";

        List<Element> books = new ArrayList<>();
        boolean valid;
        do {
            valid = true;
            System.out.print("\nВведите название удаляемой книги (пустое значение - выход): ");
            inputTitle = in.nextLine();

            if(inputTitle.isEmpty()) return;

            books = findInCollection(collection, "title", inputTitle);
            if(books.size() == 0){
                System.out.println("Ошибка! книги с таким названием нет в коллекции!");
                valid = false;
            }
        }while(!valid);

        Node booksItem = books.get(0);
        System.out.println("\nНайдена следующая книга: ");
        printELement(booksItem);

        System.out.println("\nВы уверены, что хотите её удалить из коллекции?");
        System.out.println("1 - Да");
        System.out.println("0 - Отмена");

        System.out.print("\nВаш ответ: ");
        String answer = in.nextLine();

        if(answer.equals("1")){
            Node parentNode = booksItem.getParentNode();
            parentNode.removeChild(booksItem);
            saveFile(collection);

            System.out.println("\nКнига успешно удалена!");
            waitForEnter();
        }
    }

    static void printELement(Node node){
        Element element = (Element) node;
        String gtitle = element.getElementsByTagName("title").item(0).getTextContent();
        String gauthor = element.getElementsByTagName("author").item(0).getTextContent();
        int gyear = Integer.parseInt(element.getElementsByTagName("year").item(0).getTextContent());

        System.out.println("* " + gtitle + " (" + gauthor + ", " + gyear + ")");
    }

    static void waitForEnter() {
        System.out.println("\n\nНажмите [Enter] чтобы вернуться в меню...");
        Scanner in = new Scanner(System.in);
        in.nextLine();
    }

    static Document getXMLDocument(String filePath){
        Document ret = null;

        try{
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            ret = dBuilder.parse(inputFile);
            ret.getDocumentElement().normalize();
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    static List<Element> findInCollection(Document collection, String searchParam, String searchValue){
        List<Element> retList = new ArrayList();

        NodeList nodeList = collection.getElementsByTagName("books");
        for(int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String text = element.getElementsByTagName(searchParam).item(0).getTextContent();
                if(searchValue.equals(text)) retList.add(element);
            }
        }

        return retList;
    }

    static boolean addInCollection(Document collection, String title, String author, int year) {
        boolean ret = true;

        Element rootElement = collection.getDocumentElement();
        try {
            // Добавление книги
            Element books = collection.createElement("books");
            rootElement.appendChild(books);

            Element gameTitle = collection.createElement("title");
            gameTitle.appendChild(collection.createTextNode(title.trim()));
            books.appendChild(gameTitle);

            Element gamePlatform = collection.createElement("author");
            gamePlatform.appendChild(collection.createTextNode(author.trim()));
            books.appendChild(gamePlatform);

            Element gameYear = collection.createElement("year");
            gameYear.appendChild(collection.createTextNode(Integer.toString(year)));
            books.appendChild(gameYear);

            saveFile(collection);
        } catch (Exception e){
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public static void saveFile(Document collection){
        try {
            collection.normalizeDocument();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(collection);
            StreamResult result = new StreamResult(new File(getCollectionPath()));
            transformer.transform(source, result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
