package app.infonitesave.output.PerformerTiers;

import app.infonitesave.data.drivers.Drivers;
import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.helpers.Styles;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PerformerTiers {
    private static final LocalDate date = LocalDate.now().minusDays(7);
    private static final File scorecard = new File(FileSystem.getReportsFolder(date), "scorecard.pdf");
    private static final ArrayList<String> highPerformers = new ArrayList<>();
    private static final ArrayList<String> lowPerformers= new ArrayList<>();
    private static final ArrayList<String> noStatus = new ArrayList<>();
    public static XSSFWorkbook workbook;
    static {
        try {
            File template = new File("Archive/Templates/Performer-Tiers-Template.xlsx");
            workbook = new XSSFWorkbook(template);
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public static void create() {
        File output = new File(FileSystem.getWeekFolder(date), "Performer Tiers.xlsx");
        if (output.exists() || !scorecard.exists()) {
            return;
        }
        createSheet();

        try {
            if (output.createNewFile()) {
                FileOutputStream out = new FileOutputStream(output);
                workbook.write(out);
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static void createSheet() {
        XSSFSheet sheet = workbook.getSheet("Tiers");
        HashMap<String, Object[]> map = driverMap();
        String[] keys = sortNames(map.keySet());

        XSSFRow row;
        int rowid = 1;
        for (String key : keys) {
            row = sheet.createRow(rowid++);
            Object[] objects = map.get(key);
            int cellid = 0;
            XSSFCell cell;
            for (Object o : objects) {
                cell = row.createCell(cellid++);
                if (NumberUtils.isCreatable(o.toString())) {
                    int num = Integer.parseInt(o.toString());
                    cell.setCellValue(num);
                } else {
                    cell.setCellValue(o.toString());
                }
                cell.setCellStyle(style());
                if (lowPerformers.contains(key)) {
                    cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.getCellStyle().setFillForegroundColor(Styles.redColor);
                }
                if (highPerformers.contains(key)) {
                    cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.getCellStyle().setFillForegroundColor(Styles.greenColor);
                }

            }


        }

    }


    private static XSSFCellStyle style() {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(16);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);


        return cellStyle;
    }

    private static HashMap<String, Object[]> driverMap() {
        HashMap<String, Object[]> map = new HashMap<>();
        try (PDDocument pdf = PDDocument.load(scorecard)) {
            ArrayList<String> trailing = getTrailing(pdf);
            for (String line : trailing) {
                String[] x = line.split(" ");
                String name = StringUtils.capitalize(x[1].toLowerCase()) + " " + StringUtils.capitalize(x[2].substring(0,2).toLowerCase());
                int fantastic = Integer.parseInt(x[x.length-4]);
                int great = Integer.parseInt(x[x.length-3]);
                int fair = Integer.parseInt(x[x.length-2]);
                int poor = Integer.parseInt(x[x.length-1]);
                String tier = "";
                if (line.contains("High Performer")) {
                    highPerformers.add(name);
                    tier = "High";
                }
                if (line.contains("Normal Performer") || line.contains("Low Performer")) {
                    lowPerformers.add(name);
                    tier = "Low";
                }
                if (line.contains("No Status")) {
                    noStatus.add(name);
                    tier = "<4 Wks";
                }

                map.put(name, new Object[]{name, tier, fantastic, great, fair, poor});
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    private static ArrayList<String> getTrailing(PDDocument pdf) throws IOException {
        int pages = pdf.getNumberOfPages();
        ArrayList<String> trailing = new ArrayList<>();
        for (int i = 1; i <= pages; i++) {
            PageExtractor extractor = new PageExtractor(pdf, i, i);
            PDFTextStripper stripper= new PDFTextStripper();
            String page = stripper.getText(extractor.extract());
            if (page.contains("DA Trailing 6-Week Performance")) {
                BufferedReader reader = new BufferedReader(new StringReader(page));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (NumberUtils.isCreatable(line.substring(0,1))) {
                        trailing.add(line);
                    }
                }


            }

        }
        return trailing;
    }
    private static String[] sortNames(Set<String> names) {
        String[] nameArray = names.toArray(new String[0]);
        Arrays.sort(nameArray);

        return nameArray;
    }
}
