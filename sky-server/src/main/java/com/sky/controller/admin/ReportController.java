package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计接口")
    public Result<TurnoverReportVO> turnOverReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计接口");
        TurnoverReportVO turnoverReportVO = reportService.turnOverReport(begin,end);

        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate end) {
        log.info("用户统计接口");
        UserReportVO userReportVO =reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate end){
        log.info("订单统计接口");
        OrderReportVO orderReportVO =reportService.ordersStatistics(begin,end);

        return Result.success(orderReportVO);

    }

    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> top10Report(@DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDate end) {
        log.info("查询销量排名top10接口");

        try {
            // 调用 reportService.top10Report
            SalesTop10ReportVO salesTop10ReportVO = reportService.top10Report(begin, end);
            return Result.success(salesTop10ReportVO);
        } catch (Exception e) {
            log.error("查询销量排名top10失败", e); // 打印异常日志
            // 抛出自定义异常或者返回失败的响应
            throw new RuntimeException("查询销量排名top10失败", e); // 你也可以自定义异常类
        }
    }


    @GetMapping("/export")
    @ApiOperation("导出Excel报表接口")
    public void export(HttpServletResponse response){
        log.info("导出Excel报表接口");
        reportService.exportBussionessData(response);
    }

}
