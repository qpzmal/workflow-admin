package cn.advu.workflow.web.controller.report;

import cn.advu.workflow.domain.fcf_vu.datareport.VuDataReport;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.service.datareport.DataReportService;
import cn.advu.workflow.web.third.echarts.NormalExtend;
import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.series.Bar;
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
import java.util.ArrayList;
import java.util.List;

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
        startDate = StringUtils.isEmpty(startDate)?(new DateTime().withDayOfYear(1).withHourOfDay(1).withMinuteOfHour(1).toString("yyyy/MM/dd HH:mm:ss")):startDate;
        endDate = StringUtils.isEmpty(endDate)?(new DateTime().toString("yyyy/MM/dd HH:mm:ss")):endDate;
        type = StringUtils.isEmpty(type)?"1":type;

        LOGGER.info("lid:{},options:{}", lid, type);
        LOGGER.info("startDate:{},endDate:{}", startDate, endDate);

        List<VuDataReport> list = new ArrayList<>();
        if ("1".equals(lid)) {
            list = dataReportService.querySaleHistoryByDate(startDate, endDate, type);
        } else if ("2".equals(lid)) {
            list = dataReportService.querySaleHistoryByArea();
        } else if ("3".equals(lid)) {
            list = dataReportService.querySaleHistoryBySaler(startDate, endDate, type);
        } else if ("4".equals(lid)) {
            list = dataReportService.querySaleHistoryByCustomType(startDate, endDate, type);
        } else {
            LOGGER.error("lid 参数错误。");
        }

        //创建Option
        Option option = new Option();
//        option.title("销售汇总").tooltip(Trigger.axis).legend("金额（元）");
        option.title("销售汇总").legend("金额（元）").tooltip().trigger();
        //横轴为值轴
        option.xAxis(new ValueAxis().boundaryGap(0d, 0.01));
        //创建类目轴
        CategoryAxis category = new CategoryAxis();
        //柱状数据
        Bar bar = new Bar("金额（元）");
        //饼图数据
        Pie pie = new Pie("金额（元）");
        //循环数据
        for (VuDataReport vuDataReport : list) {
            //设置类目
            category.data(vuDataReport.getOrderDate());
            //类目对应的柱状图
            bar.data(vuDataReport.getTaxAmount());
            //饼图数据
            pie.data(new PieData(vuDataReport.getOrderDate(), vuDataReport.getTaxAmount()));
        }
        //设置类目轴
        option.yAxis(category);
        //饼图的圆心和半径
        pie.center("80%","50%").radius(100);
        // 设置柱状图参数
        ItemStyle itemStyle = new ItemStyle();
        NormalExtend normal = new NormalExtend();
        normal.setPosition("right");
        normal.setShow(true);
        itemStyle.setNormal(normal);
        bar.setLabel(itemStyle);

        //设置数据
        option.series(bar, pie);
        //由于药品名字过长，图表距离左侧距离设置180，关于grid可以看ECharts的官方文档
        option.grid().x(180).width("50%");
        //返回Option


        result.setData(option);
        result.setCode(WebConstants.OPERATION_SUCCESS).setInfo("成功");
        LOGGER.info("result:{}", JSON.toJSONString(result));
        return JSON.toJSONString(result);
    }



    @RequestMapping("/feature/query")
    @ResponseBody
    public ResultJson<Object> queryFeature(
            String lid, String startDate, String endDate, String options,
            HttpServletRequest request, HttpServletResponse response) {
        ResultJson result = new ResultJson<>(WebConstants.OPERATION_FAILURE);
        lid = StringUtils.isEmpty(lid)?"1":lid;
        startDate = StringUtils.isEmpty(startDate)?(new DateTime().withDayOfYear(1).withHourOfDay(1).withMinuteOfHour(1).toString("yyyy/MM/dd HH:mm:ss")):startDate;
        endDate = StringUtils.isEmpty(endDate)?(new DateTime().toString("yyyy/MM/dd HH:mm:ss")):endDate;
        options = StringUtils.isEmpty(options)?"1":options;


        List<VuDataReport> list = new ArrayList<>();
        list = dataReportService.querySaleFeature(startDate, endDate, options);
        result.setData(list);

        return result;
    }

}
