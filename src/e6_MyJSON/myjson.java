package e6_MyJSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.zip.DataFormatException;

public class myjson {
    static String getCollectionPath() { return "src/e2_json/example-json.json"; }

    public static void main(String[] args) {
        Object collection = getJSONObject(getCollectionPath());
        int answer = 0;

        do{
            switch(answer) {
                case 1: showCollectionMenu(collection); break;
                case 2: add2CollectionMenu(collection); break;
                case 3: searchInCollectionMenu(collection); break;
                case 4: deleteFromCollection(collection); break;
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
        System.out.println("3 - Найти книгу по автору");
        System.out.println("4 - Удалить книгу");
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
        }while(answ < 0 || answ > 4);

        return answ;
    }

    static void add2CollectionMenu(Object collection){
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
                List<Object> books = findInCollection(collection, "title", inputTitle);
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
                inputAuthor = in.nextLine();
                if(inputAuthor.isEmpty()) throw new DataFormatException();
            }catch(DataFormatException e){
                System.out.println("Ошибка! Поле автор не должно быть пустым!!");
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
            List<Object> books = findInCollection(collection, "title", inputTitle);
            if(books.size() == 1){
                JSONObject g = (JSONObject) books.get(0);
                printELement(g);
            }
        }else{
            System.out.println("\nОшибка. Книга не добавлена.");
        }
        waitForEnter();
    }

    static void showCollectionMenu(Object collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Список книг в коллекции");
        System.out.println("-------------------------------------");
        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

        if (jsonArray != null) {
            for(Object o:jsonArray){
                JSONObject books = (JSONObject) o;
                printELement(books);
            }
        } else {
            System.out.println("Коллекция пуста");
        }
        waitForEnter();
    }
    static void searchInCollectionMenu(Object collection) {
        Scanner in = new Scanner(System.in);

        System.out.println("\n-------------------------------------");
        System.out.println("Поиск книги по автору");
        System.out.println("-------------------------------------");
        System.out.print("\nВведите название автора: ");
        String searchValue = in.nextLine();


        List<Object> foundElements = findInCollection(collection, "Author", searchValue);

        if (foundElements.size() == 0)
        {
            System.out.println("\nВ коллекции ничего не найдено");
        }else{
            System.out.println("\nВ коллекции найдено книг - " + foundElements.size() + " :");
            for(int i=0;i<foundElements.size();i++){
                JSONObject books = (JSONObject) foundElements.get(i);
                printELement(books);
            }
        }
        waitForEnter();
    }

    static void deleteFromCollection(Object collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Удаление книги из коллекции");
        System.out.println("-------------------------------------");

        Scanner in = new Scanner(System.in);

        String inputTitle = "";

        List<Object> books = new ArrayList<>();
        boolean valid;
        do {
            valid = true;
            System.out.print("\nВведите название удаляемой книги (пустое значение - выход): ");
            inputTitle = in.nextLine();

            if(inputTitle.isEmpty()) return;

            books = findInCollection(collection, "title", inputTitle);
            if(books.size() == 0){
                System.out.println("Ошибка! Книги с таким названием нет в коллекции!");
                valid = false;
            }
        }while(!valid);

        JSONObject booksItem = (JSONObject) books.get(0);
        System.out.println("\nНайдена следующая книга: ");
        printELement(booksItem);

        System.out.println("\nВы уверены, что хотите её удалить из коллекции?");
        System.out.println("1 - Да");
        System.out.println("0 - Отмена");

        System.out.print("\nВаш ответ: ");
        String answer = in.nextLine();

        if(answer.equals("1")){
            JSONObject jsonObject = (JSONObject) collection;
            JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

            Iterator iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject book = (JSONObject) iterator.next();
                if (inputTitle.equals(book.get("title"))) {
                    iterator.remove();
                }}

            saveFile(collection);

            System.out.println("\nКнига успешно удалена!");
            waitForEnter();
        }
    }

    static void printELement(JSONObject books){
        String gtitle = books.get("title").toString();
        String gauthor = books.get("author").toString();
        int gyear = Integer.parseInt(books.get("year").toString());

        System.out.println("* " + gtitle + " (" + gauthor + ", " + gyear + ")");
    }

    static void waitForEnter() {
        System.out.println("\n\nНажмите [Enter] чтобы вернуться в меню...");
        Scanner in = new Scanner(System.in);
        in.nextLine();
    }

    static Object getJSONObject(String filePath){
        Object ret = null;

        try{
            JSONParser parser = new JSONParser();
            ret = parser.parse(new FileReader(filePath));
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    static List<Object> findInCollection(Object collection, String searchParam, String searchValue){
        List<Object> retList = new ArrayList();

        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

        for(Object o:jsonArray){
            JSONObject books = (JSONObject) o;

            String text = books.get(searchParam).toString();
            if(searchValue.equals(text)) retList.add(books);
        }

        return retList;
    }

    static boolean addInCollection(Object collection, String title, String author, int year) {
        boolean ret = true;

        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");
        try {

            JSONObject newBooks = new JSONObject();
            newBooks.put("title", title);
            newBooks.put("author", author);
            newBooks.put("year", year);
            jsonArray.add(newBooks);

            saveFile(collection);
        } catch (Exception e){
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public static void saveFile(Object collection){
        try {
            JSONObject jsonObject = (JSONObject) collection;
            FileWriter file = new FileWriter(getCollectionPath());
            file.write(jsonObject.toJSONString());
            file.flush();
            file.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
