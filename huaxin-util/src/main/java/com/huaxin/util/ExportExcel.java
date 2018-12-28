package com.huaxin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 导出Excel
 *
 */
public class ExportExcel
{
    private Workbook workbook = null;
    private Sheet sheet=null;
    private List<String> title = null;
    private List<List<Object>> content = null;
    private String sheetName=null;
    private HttpServletRequest request;


    /**要添加的内容
     * @param content
     * @param req 请求域
     */
    public ExportExcel(List<List<Object>> content,HttpServletRequest req)
    {
        this.content = content;
        this.request=req;
    }
    /**
     * @param content 要添加的内容
     * @param sheetName 工作表的名称
     * @param req 请求域
     */
    public ExportExcel(List<List<Object>> content,String sheetName,HttpServletRequest req)
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
    public ExportExcel(List<String> title,List<List<Object>> content,HttpServletRequest req)
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
    public ExportExcel(List<String> title,List<List<Object>> content,String sheetName,HttpServletRequest req)
    {
        this.title=title;
        this.content=content;
        this.sheetName=sheetName;
        this.request=req;
    }
    /**
     * 创建文件路径
     * @return
     */
    private String getUnique()
    {
        UUID uuid=UUID.randomUUID();
        String real=request.getSession().getServletContext().getRealPath("excel");
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
        if (this.title!=null)
        {
            h=1;
            Row row = sheet.createRow(0);
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


}
