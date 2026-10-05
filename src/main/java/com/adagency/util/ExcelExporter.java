package com.adagency.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adagency.model.Campaign;
import com.adagency.model.Client;

public final class ExcelExporter {

    private ExcelExporter() {}

    public static void exportCampaigns(List<Campaign> campaigns, String path) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Campaigns");
            String[] headers = {"ID", "Title", "Type", "Status",
                    "Start", "End", "Budget", "Client ID", "Description"};
            fillHeader(wb, sheet, headers);

            int r = 1;
            for (Campaign c : campaigns) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getTitle());
                row.createCell(2).setCellValue(c.getType().name());
                row.createCell(3).setCellValue(c.getStatus().name());
                row.createCell(4).setCellValue(c.getStartDate().toString());
                row.createCell(5).setCellValue(c.getEndDate().toString());
                row.createCell(6).setCellValue(c.getBudget().doubleValue());
                row.createCell(7).setCellValue(c.getClientId());
                row.createCell(8).setCellValue(c.getDescription() == null ? "" : c.getDescription());
            }
            autosize(sheet, headers.length);
            try (FileOutputStream out = new FileOutputStream(path)) { wb.write(out); }
        }
    }

    public static void exportClients(List<Client> clients, String path) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Clients");
            String[] headers = {"ID", "Full Name", "Email", "Phone", "Company", "Status", "Created At"};
            fillHeader(wb, sheet, headers);

            int r = 1;
            for (Client c : clients) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getFullName());
                row.createCell(2).setCellValue(c.getEmail());
                row.createCell(3).setCellValue(c.getPhone());
                row.createCell(4).setCellValue(c.getCompany() == null ? "" : c.getCompany());
                row.createCell(5).setCellValue(c.getStatus() == null ? "" : c.getStatus().name());
                row.createCell(6).setCellValue(
                        c.getCreatedAt() == null ? "" : c.getCreatedAt().toString());
            }
            autosize(sheet, headers.length);
            try (FileOutputStream out = new FileOutputStream(path)) { wb.write(out); }
        }
    }

    private static void fillHeader(Workbook wb, Sheet sheet, String[] headers) {
        Row header = sheet.createRow(0);
        CellStyle bold = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        bold.setFont(font);
        for (int i = 0; i < headers.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(bold);
        }
    }

    private static void autosize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) sheet.autoSizeColumn(i);
    }
}