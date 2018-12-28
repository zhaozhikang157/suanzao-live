package com.huaxin.util;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
/**
 * Created by U on 2016/7/2.
 */
public class ExportExcelWhaUtil {
    private Workbook workbook = null;
    private Sheet sheet=null;
    private List<String> title = null;
    private List<List<Object>> content = null;
    private String sheetName=null;
    private HttpServletRequest request;
    private List<ExcelTop> ets;
    private String titleStr;
    /**要添加的内容
     * @param content
     * @param req 请求域
     */
    public ExportExcelWhaUtil(List<List<Object>> content,HttpServletRequest req)
    {
        this.content = content;
        this.request=req;
    }
    /**
     * @param content 要添加的内容
     * @param sheetName 工作表的名称
     * @param req 请求域
     */
    public ExportExcelWhaUtil(List<List<Object>> content,String sheetName,HttpServletRequest req)
    {
        this.content = content;
        this.sheetName=sheetName;
        this.request=req;
    }
    /**
     * @param title 列头
     * @param content 要添加的内容
     * @param req 请求域
     */
    public ExportExcelWhaUtil(List<String> title,List<List<Object>> content,HttpServletRequest req)
    {
        this.title=title;
        this.content=content;
        this.request=req;
    }

    /**
     * @param title 列头
     * @param content 要添加的内容
     * @param sheetName 工作表名称
     * @param req 请求域
     */
    public ExportExcelWhaUtil(List<String> title,List<List<Object>> content,String sheetName,HttpServletRequest req)
    {
        this.title=title;
        this.content=content;
        this.sheetName=sheetName;
        this.request=req;
    }

    /**
     *
     * @param title 列头
     * @param content 要添加的内容
     * @param sheetName 工作表名称
     * @param req 请求域
     * @param ets 表头
     */
    public ExportExcelWhaUtil(List<String> title,List<List<Object>> content,String sheetName,HttpServletRequest req,List<ExcelTop> ets)
    {
        this.title=title;
        this.content=content;
        this.sheetName=sheetName;
        this.request=req;
        this.ets = ets;
    }
    /**
     * 创建文件路径
     * @return
     */
    private String getUnique()
    {
        UUID uuid=UUID.randomUUID();
        String real=request.getSession().getServletContext().getRealPath("/");
        String uniqueCode=real+File.separator+uuid.toString().replaceAll("-", "");
        File path = new File(uniqueCode);
        if (!path.exists())
        {
            path.mkdirs();
        }
        return path.getPath()+File.separator+"excel.xls";
    }
    /**
     * 获得创建完成的Excel的路径
     * @return 生成的Excel的路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String getExcel() throws FileNotFoundException, IOException
    {
        this.workbook=new HSSFWorkbook();
        if (null==this.sheetName)
        {
            this.sheet = workbook.createSheet();
        }
        else
        {
            this.sheet = workbook.createSheet(this.sheetName);
        }
        int h=0;
        if(ets!=null&&ets.size()>0){
            for(ExcelTop e :ets){
                ++h;
                Row row = sheet.createRow((short) e.getRowIndex());
                Cell cell2 = row.createCell((short) e.getRowText());
                cell2.setCellValue(e.getData());
                if(e.isSl()){
                    sheet.addMergedRegion(new CellRangeAddress(e.getRowIndex(), e.getRowNum(), e.getStart(), e.getEnd()));
                }
                if(e.getEts()!=null&&e.getEts().size()>0){

                    for(ExcelTop e1: e.getEts()){
                        Cell cell = row.createCell((short) e1.getRowText());
                        cell.setCellValue(e1.getData());
                        if(e1.isSl()){
                            sheet.addMergedRegion(new CellRangeAddress(e1.getRowIndex(), e1.getRowNum(),e1.getStart(),e1.getEnd()));
                        }
                    }

                }}
        }
        if (this.title!=null)
        {

            Row row = sheet.createRow(h);
            h++;
            for (int i=0;i<title.size();i++)
            {
                Cell cell = row.createCell(i);
                cell.setCellValue(this.title.get(i));
            }
            title=null;
        }
        if (this.content!=null)
        {
            int rowCount=this.content.size();
            for (int i = 0; i < rowCount; i++)
            {
                Row row = sheet.createRow(i+h);
                List<Object> colValues=this.content.get(i);

                int colCount=colValues.size();
                for (int j = 0; j < colCount; j++)
                {
                    Cell cell=row.createCell(j);
                    getValue(cell,colValues.get(j));
                }
            }
        }

        String path=this.getUnique();
        FileOutputStream os=new FileOutputStream(path);
        //ServletUtils.setFileDownloadHeader(response, "excel.xls");
        //ServletOutputStream sos=response.getOutputStream();
        workbook.write(os);
        //sos.close();
        os.close();
        return path;
    }
    /**
     * 给单元格赋值
     * @param cell
     * @param object
     */
    private void getValue(Cell cell,Object object)
    {
        if (object==null)
        {
            cell.setCellValue("");
        }else if(object instanceof Double){
            cell.setCellValue(Double.parseDouble(object.toString()));
        }else if(object instanceof Date){
            Date date = (Date) object;
            cell.setCellValue(date.toString());
        }else{
            cell.setCellValue(object.toString());
        }
    }
    public static List<String> getTitleList(String titleStr){
        List<String> title =null;
        if(titleStr!=null&&!titleStr.isEmpty()){
            title = new ArrayList<String>();
            String[] split = titleStr.split(",");
            for(String str : split){
                title.add(str);
            }
        }
        return title;
    }
    public static List<List<Object>> getContent(List<Map> objs,String keys){
        List<List<Object>> content=new ArrayList<List<Object>>();
        int i = 1;
        List<String> titleList = getTitleList(keys);
        if(objs!=null&&objs.size()>0){
            for(Map m : objs){
                List<Object> data=new ArrayList<Object>();
                data.add(i);
                for(String key:titleList){
                    data.add(m.get(key));
                }
                content.add(data);
                i++;
            }
        }
        return content;
    }
}
