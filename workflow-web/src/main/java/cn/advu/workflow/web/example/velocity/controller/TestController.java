package cn.advu.workflow.web.example.velocity.controller;

import cn.advu.workflow.domain.fcf_vu.SysUser;
import cn.advu.workflow.web.common.util.echarts.*;
import cn.advu.workflow.web.example.velocity.service.TestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Controller
public class TestController {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private TestService testService;


    //@RequestMapping("index")
    public ModelAndView index() {
        logger.debug("method : index() ");
        ModelAndView mav = new ModelAndView("index");

//        User user  = testService.findUser(1l);
        SysUser user = testService.findUser("hello");
        mav.addObject("user", user );

        return mav;
    }
    
    @RequestMapping("chart")
    @ResponseBody
    public Echarts<Integer> testEcharts(){
        Echarts<Integer> chart = new Echarts<>();
        chart.setTitle(new Title("父标题", "子标题"));
        chart.setTooltip(new Tooltip("item"));
        Legend legend = new Legend();
        List<String> data = new ArrayList<>();
        data.add("最高温度");        
        data.add("最低温度");        
        legend.setData(data);
        chart.setLegend(legend);
        
        List<String> xdata = new ArrayList<>();
        xdata.add("周一");
        xdata.add("周二");
        xdata.add("周三");
        XAxis xAxis = new XAxis(xdata);
        chart.setxAxis(xAxis);
        YAxis yAxis = new YAxis("℃");
        chart.setyAxis(yAxis);
        
        List<Series<Integer>> series = new ArrayList<>();
        List<Integer> result1 = new ArrayList<>();
        result1.add(11);
        result1.add(11);
        result1.add(15);
        List<Integer> result2 = new ArrayList<>();
        result2.add(1);
        result2.add(-2);
        result2.add(2);
        Series<Integer> s1 = new Series<>("最高温度", "line", result1);
        Series<Integer> s2 = new Series<>("最低温度", "line", result2);
        series.add(s1);
        series.add(s2);
        chart.setSeries(series);
        return chart;
    }
}
