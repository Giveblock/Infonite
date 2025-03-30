package app.infonitesave.data.drivers;

import app.infonitesave.utils.helpers.Styles;
import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DriverSheet {

    public static void createSheet(JsonObject drivers) throws IOException, InvalidFormatException {
        File template = new File("Archive/Templates/Driver-Template.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(template);
        XSSFSheet sheet = workbook.getSheet("Drivers");
        XSSFRow row;

        HashMap<String, String[]> map = driverMap(drivers);
        String[] keys = sortNames(map.keySet());

        int rowid = 1;
        for (String key : keys) {
            row = sheet.createRow(rowid++);
            String[] strings = map.get(key);
            int cellid = 0;
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            for (String s : strings) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue(s);
                cell.setCellStyle(rowStyle(rowid, workbook));
                if (s.equalsIgnoreCase("Yes")) {
                    cell.getCellStyle().setFillForegroundColor(Styles.greenColor);
                }
                if (s.equalsIgnoreCase("No")) {
                    cell.getCellStyle().setFillForegroundColor(Styles.redColor);
                }

            }


        }


        File output = new File("Archive/Drivers/DriverSheet.xlsx");
        if (output.exists()) {
            output.delete();
        }

        output.createNewFile();
        FileOutputStream out = new FileOutputStream(output);
        workbook.write(out);
        out.close();

    }

    private static HashMap<String, String[]> driverMap(JsonObject drivers) {
        HashMap<String, String[]> map = new HashMap<>();
        for (String id : drivers.keySet()) {
            JsonObject o = drivers.getAsJsonObject(id);
            //Name
            String name = o.get("full-name").getAsString();
            String nickname = o.get("nick-name").getAsString();
            if (!nickname.equalsIgnoreCase("")) {
                name = nickname;
            }
            //Number
            String number = o.get("phone-number").getAsString();
            if (number.length() == 10) {
                String area = number.substring(0, 3);
                String mid = number.substring(3, 6);
                String end = number.substring(6);

                number = "(" + area + ") " + mid + "-" + end;
            }



            //WhatsApp
            String whatsapp = null;
            boolean hasWhatsApp = o.get("whatsapp").getAsBoolean();
            if (hasWhatsApp) {
                whatsapp = "Yes";
            } else {
                whatsapp = "No";
            }

            String[] strings = {
                    name,
                    number
            };

            map.put(name, strings);
        }

        return map;
    }
    private static String[] sortNames(Set<String> names) {
        String[] nameArray = names.toArray(new String[0]);
        Arrays.sort(nameArray);

        return nameArray;
    }
    private static CellStyle rowStyle(int row, XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        if (row % 2 == 0) {
            cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(205, 230, 255), new DefaultIndexedColorMap()));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else {
            cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 255), new DefaultIndexedColorMap()));
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeight(14);
        cellStyle.setFont(font);
        return cellStyle;
    }


}
