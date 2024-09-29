package com.fivepointers.selenium.store;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // for .xlsx files
import org.apache.poi.EncryptedDocumentException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkbookHandler {

    private static final String DIR_PATH = "news-data";
    private static final String FILE_NAME = "data.xlsx";
    private static final String FILE_PATH = DIR_PATH + "/" + FILE_NAME;
    private static final String PATTERN = "MMM-dd";
    private Workbook workbook = null;

    public WorkbookHandler() {
        try {
            // Check if the file exists
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                // If file does not exist, create a new workbook
                workbook = new XSSFWorkbook(); // For .xlsx files
                System.out.println("File not found. Creating a new workbook.");
            } else {
                // If the file exists, open it
                FileInputStream fis = new FileInputStream(FILE_PATH);
                workbook = WorkbookFactory.create(fis);
                fis.close(); // Close input stream after use
                System.out.println("Workbook loaded successfully.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTodaySheetStr() {
        LocalDate date = LocalDate.now();
        return date.format(DateTimeFormatter.ofPattern(PATTERN));
    }

    private void addHeader(Sheet sheet, String[] headers) {
        Row headerRow = getRow(sheet, 0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = getCell(headerRow, i);
            cell.setCellValue(headers[i]);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
        }
    }
    
    public Sheet getOrCreateSheet() {
        final String todaySheetStr = getTodaySheetStr();
        Sheet sheet = workbook.getSheet(todaySheetStr);
        if (sheet == null) {
            sheet = workbook.createSheet(todaySheetStr);
            String[] headers = { "Date", "Title", "Description", "Author" };
            addHeader(sheet, headers);
        }
        return sheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Row getRow(Sheet sheet, int idx) {
        Row row = sheet.getRow(idx);
        if (row == null) {
            row = sheet.createRow(idx);
        }
        return row;
    }

    public Cell getCell(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) {
            cell = row.createCell(idx);
        }
        return cell;
    }

    public void addDate(Sheet sheet, int rowIdx, int colIdx, LocalDateTime val) {
        CellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd.MM.yyyy"));
        Row row = getRow(sheet, rowIdx);
        Cell cell = getCell(row, colIdx);
        cell.setCellValue(val);
        cell.setCellStyle(cellStyleDate);
        System.out.println(cell);
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
            workbook.close();
            System.out.println("Workbook saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save workbook.");
        }
    }
}
