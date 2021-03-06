package cn.advu.workflow.web.controller.module;

import cn.advu.workflow.domain.fcf_vu.BaseIndustry;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.tool.DisplayTool;
import cn.advu.workflow.web.service.base.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 客户行业相关controller，用于管理客户行业
 *
 */
@Controller
@RequestMapping("/industry")
public class IndustryController {
    
    @Autowired
    private IndustryService industryService;

    /**
     * 跳转客户行业业务首页-客户行业列表页
     *
     * @param resultModel
     * @return
     */
    @RequestMapping("/index")
    public String toIndex(Model resultModel){
        ResultJson<List<BaseIndustry>> result = industryService.findAll();
        resultModel.addAttribute("dataList",result.getData());
        DisplayTool.buttonDisplay(resultModel, "add", "10201");
        DisplayTool.buttonDisplay(resultModel, "modify", "10202");
        DisplayTool.buttonDisplay(resultModel, "delete", "10203");
        return "modules/industry/list";
    }

    /**
     * 新增客户行业
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/add", method = RequestMethod.POST)
    public ResultJson<Integer> addIndustry(BaseIndustry baseIndustry, HttpServletRequest request){
        return industryService.addIndustry(baseIndustry);
    }

    /**
     * 更新客户行业
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/update", method = RequestMethod.POST)
    public ResultJson<Integer> updateIndustry(BaseIndustry baseIndustry, HttpServletRequest request){
        return industryService.updateIndustry(baseIndustry);
    }

    /**
     * 跳转新增客户行业页面
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "modules/industry/add";
    }

    /**
     * 跳转到更新客户行业页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id, Model model){

        BaseIndustry baseIndustry = industryService.findById(id).getData();

        model.addAttribute("baseIndustry", baseIndustry);
        DisplayTool.buttonDisplay(model, "modify", "10202");

        return "modules/industry/update";
    }

}
