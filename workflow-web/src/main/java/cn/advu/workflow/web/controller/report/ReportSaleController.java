package cn.advu.workflow.web.controller.report;

import cn.advu.workflow.domain.fcf_vu.datareport.VuDataReport;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.service.datareport.DataReportService;
import cn.advu.workflow.web.third.echarts.NormalExtend;

import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisLine;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;
import com.github.abel533.echarts.style.ItemStyle;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

/**
 * Created by weiqz on 2017/7/13.
 */
@Controller
@RequestMapping("/report/sale")
public class ReportSaleController {

    private static Logger LOGGER = LoggerFactory.getLogger(ReportSaleController.class);


    @Autowired
    private DataReportService dataReportService;

    /**
     * 历史订单
     *
     * @return
     */
    @RequestMapping("/history")
    public String history(){
        return "report/sale_history";
    }

    /**
     * 未来订单
     *
     * @return
     */
    @RequestMapping("/feature")
    public String feature(){
        return "report/sale_feature";
    }



    @RequestMapping(value = "/history/query", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String queryHistory(
            String lid, String startDate, String endDate, String type,
            HttpServletRequest request, HttpServletResponse response) {
        ResultJson result = new ResultJson<>(WebConstants.OPERATION_FAILURE);

        lid = StringUtils.isEmpty(lid)?"1":lid;
        startDate = StringUtils.isEmpty(startDate)?("1980-01-01"):startDate;
        endDate = StringUtils.isEmpty(endDate)?("2100-01-01"):endDate;
        type = StringUtils.isEmpty(type)?"1":type;

        LOGGER.info("lid:{},options:{}", lid, type);
        LOGGER.info("startDate:{},endDate:{}", startDate, endDate);

        List<VuDataReport> list = new ArrayList<>();
        List<VuDataReport> top3 = new ArrayList<>();
        Option option = new Option();
        Option option1 = new Option();
        if ("1".equals(lid)) {
            list = dataReportService.querySaleHistoryByDate(startDate, endDate, type);
            option = this.createChart4Date(list);
        } else if ("2".equals(lid)) {
            list = dataReportService.querySaleHistoryByArea();
            option = this.createChart4Area(list);
        } else if ("3".equals(lid)) {
            list = dataReportService.querySaleHistoryBySaler(startDate, endDate, type);
            top3 = dataReportService.querySaleHistoryBySalerTOP3(startDate, endDate);
            option = this.createChart4Saler(list); // 柱状图
            option1 = this.createChartWithTypeTop3(top3);

        } else if ("4".equals(lid)) {
            list = dataReportService.querySaleHistoryByCustomType(startDate, endDate, type);
            option = this.createChart4CustomType(list);
        } else {
            LOGGER.error("lid 参数错误。");
        }



        result.setData(option);
        result.setLine(option1);
        result.setCode(WebConstants.OPERATION_SUCCESS).setInfo("成功");
        LOGGER.info("result:{}", JSON.toJSONString(result));
        return JSON.toJSONString(result);
    }



    @RequestMapping(value = "/feature/query", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String queryFeature(
            String lid, String startDate, String endDate, String options, String queryType,
            HttpServletRequest request, HttpServletResponse response) {
        ResultJson result = new ResultJson<>(WebConstants.OPERATION_FAILURE);
        lid = StringUtils.isEmpty(lid)?"1":lid;
        startDate = StringUtils.isEmpty(startDate)?(new DateTime().withDayOfYear(1).withHourOfDay(1).withMinuteOfHour(1).toString("yyyy/MM/dd HH:mm:ss")):startDate;
        endDate = StringUtils.isEmpty(endDate)?(new DateTime().toString("yyyy/MM/dd HH:mm:ss")):endDate;
        queryType = StringUtils.isEmpty(queryType)?"0":queryType; //0地域；0以外时间
        Option option = new Option();

        List<VuDataReport> list = new ArrayList<>();
        list = dataReportService.querySaleFeature(startDate, endDate, queryType);

        if ("0".equals(queryType)) {
            option = this.createChart4Area(list);
        } else if ("1".equals(lid)) {
            option = this.createChart4Date(list);
        } else {
            LOGGER.error("queryType 参数错误。");
        }

        result.setData(option);
        result.setCode(WebConstants.OPERATION_SUCCESS).setInfo("成功");
        LOGGER.info("result:{}", JSON.toJSONString(result));
        return JSON.toJSONString(result);
    }

    private Option createChart4Date(List<VuDataReport> list) {
        return this.createChartWithType(list, 1);
    }
    private Option createChart4Area(List<VuDataReport> list) {
        return this.createChartWithType(list, 2);
    }
    private Option createChart4Saler(List<VuDataReport> list) {
        return this.createChartWithType(list, 2);
    }
    private Option createChart4CustomType(List<VuDataReport> list) {
        return this.createChartWithType(list, 3);
    }


    private Option createChartWithTypeTop3(List<VuDataReport> list) {
        //创建Option
        Option option = new Option();
//        option.title("销售汇总").tooltip(Trigger.axis).legend("金额（元）");
        option.tooltip(Trigger.axis).title().text("销售额TOP3").x(X.center);
        //横轴为值轴
        option.yAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //柱状数据
        Bar bar = new Bar("金额（元）");
        option.legend().data("金额（元）").x(X.center).y(Y.bottom);
        bar.itemStyle().normal().color("rgb(215, 38, 59)");
        //循环数据
        for (VuDataReport vuDataReport : list) {
            
                category.data(vuDataReport.getName());
                //类目对应的柱状图
                bar.data(vuDataReport.getTaxAmount());
        }
        //设置类目轴
        option.xAxis(category);
        // 设置柱状图参数
        ItemStyle itemStyle = new ItemStyle();
        NormalExtend normal = new NormalExtend();
        normal.setPosition("top");
        normal.setShow(true);
        itemStyle.setNormal(normal);
        bar.setLabel(itemStyle);

        //设置数据
        option.series(bar);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180).width("50%");
        //返回Option

        return option;
    }
    
    private Option createChartWithType(List<VuDataReport> list, int type) {
        //创建Option
        Option option = new Option();
//        option.title("销售汇总").tooltip(Trigger.axis).legend("金额（元）");
        option.tooltip(Trigger.axis).title().text("销售汇总").x(X.center);
        //横轴为值轴
        option.yAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //柱状数据
        Bar bar = new Bar("金额（元）");
        option.legend().data("金额（元）").x(X.center).y(Y.bottom);
        bar.itemStyle().normal().color("rgb(34, 137, 196)");
        //饼图数据
        Pie pie = new Pie("金额（元）");
        //循环数据
        for (VuDataReport vuDataReport : list) {
            if (1 == type) { // orderDate
                //设置类目
                category.data(vuDataReport.getOrderDate());
                //类目对应的柱状图
                bar.data(vuDataReport.getTaxAmount());
                //饼图数据
                pie.data(new PieData(vuDataReport.getOrderDate(), vuDataReport.getTaxAmount()));

            } else if (2 == type) { // name
                //设置类目
                category.data(vuDataReport.getName());
                //类目对应的柱状图
                bar.data(vuDataReport.getTaxAmount());
                //饼图数据
                pie.data(new PieData(vuDataReport.getName(), vuDataReport.getTaxAmount()));

            } else if (3 == type){ // customType
                //设置类目
                category.data(vuDataReport.getName());
                //类目对应的柱状图
                bar.data(vuDataReport.getTaxAmount());
                //饼图数据
                pie.data(new PieData(vuDataReport.getName(), vuDataReport.getTaxAmount()));

            }
        }
        //设置类目轴
        option.xAxis(category);
        //饼图的圆心和半径
        pie.center("80%","50%").radius(100);
        // 设置柱状图参数
        ItemStyle itemStyle = new ItemStyle();
        NormalExtend normal = new NormalExtend();
        normal.setPosition("top");
        normal.setShow(true);
        itemStyle.setNormal(normal);
        bar.setLabel(itemStyle);

        //设置数据
        option.series(bar);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180).width("50%");
        //返回Option

        return option;
    }


    private Option createChart(List<VuDataReport> list) {

        //创建Option对象
        Option option = new Option();

        //设置图表标题，并且居中显示
        option.tooltip(Trigger.axis).title().text("销售汇总").x(X.left);

        //设置y轴为值轴
        option.yAxis(new ValueAxis());

        //创建类目轴，并且不显示竖着的分割线，onZero=false
        CategoryAxis categoryAxis = new CategoryAxis()
                .splitLine(new SplitLine().show(false))
                .axisLine(new AxisLine().onZero(false));

        //不显示表格边框，就是围着图标的方框
        option.grid().borderWidth(0);


        Map<String, List<VuDataReport>> dataMap = new HashMap<>();
        List<String> dateList = new ArrayList();
        for (VuDataReport data : list) {
            if (dataMap.containsKey(data.getName())) {
                dataMap.get(data.getName()).add(data);
            } else {
                List<VuDataReport> dataList = new ArrayList();
                dataList.add(data);
                dataMap.put(data.getName(), dataList);
            }
        }

        Iterator iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<VuDataReport>> entry = (Map.Entry<String, List<VuDataReport>>) iterator.next();

            //设置图例,居中底部显示，显示边框
//            option.legend().data("人名").x(X.center).y(Y.bottom).borderWidth(1);
            option.legend().data(entry.getKey());

            //创建Line数据
            Line line = new Line(entry.getKey()).smooth(true);

            //根据获取的数据赋值
            for (VuDataReport data : entry.getValue()) {

                if (!dateList.contains(data.getOrderDate())) {
                    dateList.add(data.getOrderDate());
                    //增加类目，值为日期
                    categoryAxis.data(data.getOrderDate());
                }

                //日期对应的数据
                line.data(data.getTaxAmount());
            }

            //设置x轴为类目轴
            option.xAxis(categoryAxis);

            //设置数据
            option.series(line);
        }

        return option;
    }

}
