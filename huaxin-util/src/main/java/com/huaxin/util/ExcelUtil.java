package com.huaxin.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ExcelUtil {

    //判别 excel 什么版本
    public static Workbook getWorkbook(String filename, FileInputStream in) throws IOException {
        HSSFWorkbook workbook = null;
        Workbook wb;
        if (filename.endsWith(".xls")) {
            //Excel2003
            wb = new HSSFWorkbook(new BufferedInputStream(in));
        } else {
            //Excel 2007
            wb = new XSSFWorkbook(new BufferedInputStream(in));
        }
        return wb;
    }

    //excel  读取 数据 存入List
    public static ActResult importEmployeeInfo(Workbook workbook) {
        ActResult act = new ActResult();
        List<Map<Object, Object>> excelParesResult = new ArrayList<Map<Object, Object>>();
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            act.setMsg("上传的表格没有可导入的信息");
            act.setSuccess(false);
            return act;
        }
        /*循环行Row*/
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            /*循环列Cell*/
            Map<Object, Object> cellValue = new HashMap<Object, Object>();
            for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell == null) {
                    cellValue.put(cellNum, "");
                    continue;
                }
                if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()){
                    DecimalFormat decimalFormat = new DecimalFormat("0");//格式化
                    cellValue.put(cellNum,decimalFormat.format(cell.getNumericCellValue()));
                }else {
                    cellValue.put(cellNum,cell.getStringCellValue());
                }
            }
            excelParesResult.add(cellValue);
        }
        act.setSuccess(true);
        act.setMsg("批量导入员工信息成功");
        act.setData(excelParesResult);
        return act;
    }

    public static String createExcel(String fileName,List<Map<Object,Object>> listMap) {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("未导入信息");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        //设置表头
        List<String> excelHead = getExcelHead();

        HSSFCell cell = null;
        // excel头
        for (int i = 0; i < excelHead.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(excelHead.get(i));
            cell.setCellStyle(style);
        }
        for (int i = 0; i < listMap.size(); i++) {
            row = sheet.createRow((int) i + 1);
            Map map = listMap.get(i);
            // 创建单元格，并设置值
            int j = 0;
            insertCell(row, j++, map.get("cardNum"));
            insertCell(row, j++, map.get("name"));
            insertCell(row, j++, map.get("gender"));
            insertCell(row, j++, map.get("mobile"));
            insertCell(row, j++, map.get("integral"));
            insertCell(row, j++, map.get("balance"));
            insertCell(row, j++, map.get("comment"));
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(fileName));
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            System.err.println("获取不到位置");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 设置表头
     *
     * @return
     */
    private static List<String> getExcelHead() {

        List<String> strings = new ArrayList<String>();
        strings.add("会员编号");
        strings.add("姓名");
        strings.add("性别");
        strings.add("手机号码");
        strings.add("积分");
        strings.add("余额");
        strings.add("备注");
        return strings;
    }

    /**
     * 创建 单元值
     * @param row
     * @param i
     * @param object
     */
    private static void insertCell(HSSFRow row, int i, Object object) {
        if (object == null) {
            row.createCell(i).setCellValue("");
        } else {
            row.createCell(i).setCellValue(object.toString());
        }
    }

    public static List<ExcelTop> getExceltitel(List<String> strs) {
        //++++++++++++++++++++++++++++++++++++++++表头合并拼接++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        List<ExcelTop> ets = new ArrayList<ExcelTop>();//行集合
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                List<String> titleList = ExportExcelWhaUtil.getTitleList(strs.get(i));
                ExcelTop et = new ExcelTop();//每行第一个位置
                et.setRowIndex(i);
                et.setRowText(0);
                et.setSl(false);
                et.setData(titleList.get(0));
                List<ExcelTop> etscell = new ArrayList<ExcelTop>();//每行内的 每列集合
                if (titleList != null && titleList.size() > 1) {
                    for (int j = 1; j < titleList.size(); j++) {
                        ExcelTop etj = new ExcelTop();
                        etj.setRowIndex(i);
                        etj.setRowText(j);
                        etj.setSl(false);
                        etj.setData(titleList.get(j));
                        etscell.add(etj);
                    }
                }
                et.setEts(etscell);
                ets.add(et);
            }
        }
        return ets;
    }
}
