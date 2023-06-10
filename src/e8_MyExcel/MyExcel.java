package e8_MyExcel;

import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class MyExcel {
    public static void main(String[] args) throws IOException {
        // Открываем файл Excel для чтения
        String filePath = "src/e4_excel/example.xlsx";
        FileInputStream inputStream = new FileInputStream(filePath);

        try {
            // Создаём экземпляр книги Excel из файла
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // Получаем лист из книги по его имени
            XSSFSheet sheet = workbook.getSheet("Товары");

            // Перебираем строки и ячейки листа
            for (Row row : sheet) {
                for (Cell cell : row) {
                    // Выводим значение ячейки на экран
                    System.out.print(cell.toString() + "\t");
                }
                System.out.println();
            }

            // Закрываем файл и освобождаем ресурсы
            workbook.close();
            inputStream.close();
        }catch (NotOfficeXmlFileException e){
            System.out.println("Указан не Excel-файл. Укажите файл example.xlsx и запустите программу снова.");
        }catch (EmptyFileException e){
            System.out.println("Указан пустой файл.");
        }catch (NullPointerException e){
            System.out.println("Не найдена указанная книга \"Товары\"");
        }
    }
}
