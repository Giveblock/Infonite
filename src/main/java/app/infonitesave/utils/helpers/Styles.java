package app.infonitesave.utils.helpers;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Styles {
    public static final XSSFColor redColor = new XSSFColor(new java.awt.Color(255, 150, 150), new DefaultIndexedColorMap());
    public static final XSSFColor greenColor = new XSSFColor(new java.awt.Color(120, 255, 120), new DefaultIndexedColorMap());

}
