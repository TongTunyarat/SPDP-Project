package com.example.project.service.ManageSchedule;

import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.DTO.ManageSchedule.Preview.StudentDataDTO;
import com.example.project.service.ManageSchedule.DefenseSchedule.ManageDefenseService;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.poi.ss.util.CellUtil.createCell;

@Service
public class ExportScheduleService {

    @Autowired
    ManageProposalScheduleService manageProposalScheduleService;
    @Autowired
    ManageDefenseService manageDefenseService;

    public List<PreviewProposalDTO> getDataExport(String program, String semesterYear) {

        return manageProposalScheduleService.getDataPreviewSchedule(semesterYear).stream()
                .filter(p -> program.equalsIgnoreCase(p.getProgram())).collect(Collectors.toList());

    }

    public List<PreviewProposalDTO> getDefenseDataExport(String semesterYear) {

        return manageDefenseService.getDataDefensePreviewSchedule(semesterYear);

    }

    //https://denitiawan.medium.com/create-rest-api-for-export-data-to-excel-and-pdf-using-springboot-38a2ee6c73a0

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    public void newReportExcel() {
        workbook = new XSSFWorkbook();
    }

    // กำหนดรูปเเบบไฟล์
    public HttpServletResponse initResponseForExportExcel(HttpServletResponse response, String fileName) {

        // mime type มาตรฐานการสื่อสารของไฟล์เอกสาร เพื่อให้เบราว์เซอร์รู้ว่าไฟล์ที่กำลังติดต่อกับ server นั้นคือไฟล์รูปแบบใด (https://kb.hostatom.com/content/20612/)
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new Date());

        // ทำให้ browser download ไฟล์แทนที่จะเปิดใน browser
        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=" + fileName + "_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName + "_" + currentDateTime + ".xlsx";

        response.setHeader(headerKey, headerValue);

        return response;
    }

    //
    public void writeTableHeaderExcel(String sheetName, String titleName, String[] headers) {

        // sheet
        sheet = workbook.createSheet(sheetName);
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        // title
        // สร้าง row เเละใส่ชื่อ
        createCell(row, 0, titleName, style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
        font.setFontHeightInPoints((short) 10);

        // header
        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        for(int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }

    }

    public void  createCell(org.apache.poi.ss.usermodel.Row row, int columnCount, Object value, CellStyle style) {

        sheet.autoSizeColumn(columnCount);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnCount);

        if(value instanceof  Integer) {
            cell.setCellValue((Integer) value);

        } else if (value instanceof  Double) {
            cell.setCellValue((Double) value);

        } else if (value instanceof  Boolean) {
            cell.setCellValue((Boolean) value);

        } else if (value instanceof  Long) {
            cell.setCellValue((Long) value);

        }else if (value instanceof  Byte) {
            cell.setCellValue((Byte) value);

        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);

    }

    public CellStyle getFontContentExcel() {

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontHeight(14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    public void writeTableData(List<PreviewProposalDTO> data) {

        int rowCount = 2;
        CellStyle style = getFontContentExcel();

        CellStyle advisorStyle = workbook.createCellStyle();
        advisorStyle.cloneStyleFrom(style);

        Font blueFont = workbook.createFont();
        blueFont.setFontHeightInPoints((short) 14);
        // https://www.linkedin.com/pulse/color-palette-poi-indexedcolors-aniruddha-duttachowdhury/
        blueFont.setColor(IndexedColors.LIGHT_BLUE.getIndex());
        advisorStyle.setFont(blueFont);

        CellStyle emptyStyle = workbook.createCellStyle();
        emptyStyle.cloneStyleFrom(style);
        emptyStyle.setFillForegroundColor(IndexedColors.MAROON.getIndex());
        emptyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(PreviewProposalDTO proposalDTO : data) {

            List<StudentDataDTO> students = proposalDTO.getStudents();

            List<String> allInstructors = new ArrayList<>();
            List<Boolean> isAdvisor = new ArrayList<>();

            if(proposalDTO.getInstructorNames() != null) {
                Map<String, List<String>> instructors = proposalDTO.getInstructorNames();

                if(instructors.containsKey("Advisor")) {
                    List<String> advisors = instructors.get("Advisor");
                    for(String advisor : advisors) {
                        allInstructors.add(advisor);
                        isAdvisor.add(true);
                    }
                }

                if(instructors.containsKey("Committee")) {
                    List<String> committees = instructors.get("Committee");
                    for(String committee : committees) {
                        allInstructors.add(committee);
                        isAdvisor.add(false);
                    }
                }
            }

            int startRow = rowCount;
            int maxRow = Math.max(students.size(), allInstructors.size());

            for(int i = 0; i < maxRow; i++) {

                Row row = sheet.createRow(rowCount ++);
                int columnCount = 0;

                // คนเเรก
                if(i == 0) {
                    createCell(row, columnCount++, proposalDTO.getDate(), (proposalDTO.getDate() != null && !proposalDTO.getDate().isEmpty()) ? style : emptyStyle);
                    createCell(row, columnCount++, proposalDTO.getTime(), ( proposalDTO.getTime() != null && ! proposalDTO.getTime().isEmpty()) ? style : emptyStyle);
                    createCell(row, columnCount++, proposalDTO.getRoom(), (proposalDTO.getRoom() != null && !proposalDTO.getRoom().isEmpty()) ? style : emptyStyle);
                    createCell(row, columnCount++, proposalDTO.getProgram(), style);
                    createCell(row, columnCount++, proposalDTO.getProjectId(), style);
                    createCell(row, columnCount++, proposalDTO.getProjectTitle(), style);

                } else {

                    columnCount = 6;
                }

                // loop first st
                if(i < students.size()) {

                    StudentDataDTO student = students.get(i);

                    createCell(row, columnCount++, student.getStudentId(), style);
                    createCell(row, columnCount++, student.getStudentName(), style);
                    createCell(row, columnCount++, student.getSection() != null ? student.getSection() : "0", style);
                    createCell(row, columnCount++, (student.getTrack() != null && !student.getTrack().isEmpty()) ? student.getTrack() : "0", style);
                } else {

                    columnCount += 4;

                }

                if(i < allInstructors.size()) {

                    CellStyle instructorStyle = isAdvisor.get(i) ? advisorStyle : style;
                    createCell(row, columnCount++, allInstructors.get(i), instructorStyle);

                }

            }

            if(maxRow > 1 ){
                // ข้อมูลโปรเจค
                for(int col = 0; col < 6; col ++) {

                    // firstRow, lastRow, firstCol, lastCol
                    sheet.addMergedRegion(new CellRangeAddress(
                            startRow,
                            startRow + maxRow -1,
                            col,
                            col
                    ));

                }

            }


        }

    }


    public void exportToExcel(HttpServletResponse response, List<PreviewProposalDTO> data, String program, String semesterYear) throws IOException {

        newReportExcel();

        // response  writer to excel
        response = initResponseForExportExcel(response, "Proposal_Schedule_" + program + "_" + semesterYear);
        ServletOutputStream outputStream = response.getOutputStream();

        // write sheet, title & header
        String[] headers = new String[] {"Date", "Time", "Room", "Program", "Project ID", "Project Title", "Student ID", "First - Last Name", "Sec.", "Track", "Advisor and Committees"};
        writeTableHeaderExcel("Schedule_Presentation", "Proposal Schedule Presentation", headers);

        // write content row
        writeTableData(data);

        workbook.write(outputStream);
        outputStream.close();

    }

    public void exportDefenseToExcel(HttpServletResponse response, List<PreviewProposalDTO> data, String semesterYear) throws IOException {

        newReportExcel();

        // response  writer to excel
        response = initResponseForExportExcel(response, "Defense_Schedule"+ "_" + semesterYear);
        ServletOutputStream outputStream = response.getOutputStream();

        // write sheet, title & header
        String[] headers = new String[] {"Date", "Time", "Room", "Program", "Project ID", "Project Title", "Student ID", "First - Last Name", "Sec.", "Track", "Advisor and Committees"};
        writeTableHeaderExcel("Schedule_Presentation", "Defense Schedule Presentation", headers);

        // write content row
        writeTableData(data);

        workbook.write(outputStream);
        outputStream.close();

    }


}