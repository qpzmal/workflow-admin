package cn.advu.workflow.web.controller.module;

import cn.advu.workflow.domain.fcf_vu.BaseMonitor;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.tool.DisplayTool;
import cn.advu.workflow.web.service.base.MonitorRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by weiqz on 2017/6/25.
 */
@Controller
@RequestMapping("monitor")
public class MonitorController {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private MonitorRequestService monitorRequestService;

    @RequestMapping("/index")
    public String toIndex(Model resultModel){
        resultModel.addAttribute("dataList",monitorRequestService.findAll().getData());
        DisplayTool.buttonDisplay(resultModel, "add", "10301");
        DisplayTool.buttonDisplay(resultModel, "modify", "10302");
        DisplayTool.buttonDisplay(resultModel, "delete", "10303");
        return "modules/monitor/list";
    }


    /**
     * 新增监测机构
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/add", method = RequestMethod.POST)
    public ResultJson<Integer> addMonitor(BaseMonitor baseMonitorRequest, HttpServletRequest request){
        return monitorRequestService.addMonitorRequest(baseMonitorRequest);
    }


    /**
     * 新增监测机构
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/update", method = RequestMethod.POST)
    public ResultJson<Integer> updateMonitor(BaseMonitor baseMonitorRequest, HttpServletRequest request){
        return monitorRequestService.updateMonitorRequest(baseMonitorRequest);
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "modules/monitor/add";
    }

    /**
     * 跳转至监测机构更新页面
     *
     * @param id
     * @param resultModel
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id, Model resultModel){
        BaseMonitor baseMonitorRequest = monitorRequestService.findById(id).getData();
        resultModel.addAttribute("baseMonitorRequest", baseMonitorRequest);
        DisplayTool.buttonDisplay(resultModel, "modify", "10302");
        return "modules/monitor/update";
    }

}
