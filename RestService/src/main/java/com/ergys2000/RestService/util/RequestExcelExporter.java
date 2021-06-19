package com.ergys2000.RestService.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ergys2000.RestService.models.Request;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
/** The object used to export request to an excel sheet */
public class RequestExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private Iterable<Request> listRequests;
     
    public RequestExcelExporter(Iterable<Request> listRequests) {
        this.listRequests = listRequests;
        workbook = new XSSFWorkbook();
    }
 
 
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Requests");
         
        Row row = sheet.createRow(0);
         
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
         
        createCell(row, 0, "Request ID", style);      
        createCell(row, 1, "User name", style);
        createCell(row, 2, "Created at", style);       
        createCell(row, 3, "Start date", style);    
        createCell(row, 4, "End date", style);
        createCell(row, 5, "Approved", style);
    }
     
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
     
    private void writeDataLines() {
        int rowCount = 1;
 
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
                 
        for (Request request : listRequests) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
			String username = String.format("%s %s", request.getUser().getFirstname(), request.getUser().getLastname());
             
            createCell(row, columnCount++, request.getId(), style);
            createCell(row, columnCount++, username, style);
            createCell(row, columnCount++, request.getCreatedOn().toString(), style);
            createCell(row, columnCount++, request.getStartDate().toString(), style);
            createCell(row, columnCount++, request.getEndDate().toString(), style);
            createCell(row, columnCount++, request.getApproved(), style);
        }
    }
     
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
         
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
         
        outputStream.close();
    }
}
