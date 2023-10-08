package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> localDateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();

        localDateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        localDateList.forEach(x -> {
            LocalDateTime st = LocalDateTime.of(x, LocalTime.MIN);
            LocalDateTime ed = LocalDateTime.of(x, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", st);
            map.put("end", ed);
            map.put("status", Orders.COMPLETED);

            Double turnover = reportMapper.getTurnoverByTimeAndStatus(map);
            turnover = (turnover == null) ? 0.0 : turnover;

            turnoverList.add(turnover);
        });



        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> localDateList = new ArrayList<>();
        List<Integer> totalList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        localDateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        localDateList.forEach(x -> {
            LocalDateTime st = LocalDateTime.of(x, LocalTime.MIN);
            LocalDateTime ed = LocalDateTime.of(x, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", ed);

            Integer userTotal = reportMapper.getUserCount(map);

            map.put("begin", st);
            Integer newUserCount = reportMapper.getUserCount(map);

            newUserList.add(newUserCount);
            totalList.add(userTotal);

        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {


        List<LocalDate> localDateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Double orderCompletionRate = 0.0;

        localDateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        for(LocalDate x : localDateList) {
            LocalDateTime st = LocalDateTime.of(x, LocalTime.MIN);
            LocalDateTime ed = LocalDateTime.of(x, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", st);
            map.put("end", ed);

            Integer orderCount = reportMapper.getOrderCount(map);

            map.put("status", Orders.COMPLETED);

            Integer usefulOrderCount = reportMapper.getOrderCount(map);

            orderCountList.add(orderCount);
            validOrderCountList.add(usefulOrderCount);
            totalOrderCount += orderCount;
            validOrderCount += usefulOrderCount;

        }

        if(totalOrderCount != 0) {
            orderCompletionRate = validOrderCount * 1. / totalOrderCount;
        }


        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

        LocalDateTime st = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime ed = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> top10List = reportMapper.getTop10(st, ed);

        List<String> nameList = top10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = top10List.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 导出Excel报表
     *
     * @param response
     */
    public void export(HttpServletResponse response) {

        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
