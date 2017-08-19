package cn.advu.workflow.web.controller.demand;

import cn.advu.workflow.common.utils.StringUtil;
import cn.advu.workflow.domain.enums.RoleEnum;
import cn.advu.workflow.domain.fcf_vu.*;
import cn.advu.workflow.web.common.RequestUtil;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.loginContext.UserThreadLocalContext;
import cn.advu.workflow.web.constants.MessageConstants;
import cn.advu.workflow.web.manager.*;
import cn.advu.workflow.web.service.base.*;
import cn.advu.workflow.web.util.AssertUtil;
import cn.advu.workflow.web.util.BigDecimalUtil;
import cn.advu.workflow.web.util.DateUtil;
import cn.advu.workflow.web.util.StringListUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 需求单相关controller，用于管理需求单
 *
 */
@Controller
@RequestMapping("/saleOrder")
public class ExecuteOrderController {

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteOrderController.class);

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ExecuteOrderService executeOrderService;

    @Autowired
    AreaService areaService;

    @Autowired
    MonitorRequestService monitorRequestService;

    @Autowired
    TreeMananger treeMananger;

    @Autowired
    PersonMananger personMananger;

    @Autowired
    CustomMananger customMananger;
    @Autowired
    UserMananger userMananger;

    @Autowired
    IndustryManager industryManager;

    @Autowired
    RegionManager regionManager;

    @Autowired
    MediaMananger mediaMananger;

    @Autowired
    AdtypeMananger adtypeMananger;

    @Autowired
    SaleFrameService saleFrameService;

    @Autowired
    RoleManager roleManager;

    @Autowired
    FileUploadService fileUploadService;

    @Value("${upload.img.base}")
    private String uploadImgBase;
    @Value("${upload.img.contract}")
    private String uploadImgContract;
    @Value("${upload.img.execute_order}")
    private String uploadImgExeOrder;

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping("/leaderSelect")
    public String leaderSelect(Integer areaId, Model model){
        List<BasePerson> leaderList = personMananger.findPersonListByArea(areaId);
        model.addAttribute("leaderList", leaderList);
        return "demand/saleOrder/leaderSelect";
    }

    @RequestMapping("/signCompanySelect")
    public String signCompanySelect(Integer signType, Model model){
        List<BaseCustom> signCompanyList = null;
        if (signType != null) {
            signCompanyList = customMananger.findCustomListByCustomType(signType);
        }
        model.addAttribute("signCompanyList", signCompanyList);
        return "demand/saleOrder/signCompanySelect";
    }

    @RequestMapping("/customSelect")
    public String customSelect(String signCustomName, Model model){
        BaseCustom baseCustom = customMananger.findByName(signCustomName);
        if (baseCustom != null) {
            List<BaseCustom> customList = customMananger.findChildCustom(baseCustom.getId());
            model.addAttribute("customList", customList);
        }

        return "demand/saleOrder/customSelect";
    }

    /**
     * 跳转需求单首页-需求单列表页
     *
     * @param resultModel
     * @return
     */
    @RequestMapping("/index")
    public String toIndex(Model resultModel){
        String strStatus = httpServletRequest.getParameter("status");
        byte pStatus = -1;
        if (StringUtils.isNotEmpty(strStatus)) {
            pStatus = Byte.parseByte(strStatus);
        }
        BaseExecuteOrder param = new BaseExecuteOrder();
        param.setStatus(pStatus);
        if ("1".equals(strStatus)) { // 排期执行单
            param.setContractStatus("0");
            param.setContractImgStatus("0");
            param.setOriginalExecuteOrderStatus("0");
        }

        ResultJson<List<BaseExecuteOrder>> result = executeOrderService.findAll(param);
        resultModel.addAttribute("dataList", result.getData());
        return "demand/saleOrder/list";
    }

    /**
     * 跳转合同与单据首页-合同与单据列表页
     *
     * @param resultModel
     * @return
     */
    @RequestMapping("/contractList")
    public String toContractList(Model resultModel){

        String conditionStartDate = RequestUtil.getStringParamDef(httpServletRequest, "startDate", DateUtil.getYearFirstDay());
        String conditionEndDate = RequestUtil.getStringParamDef(httpServletRequest, "endDate", DateUtil.getToday());

        BaseExecuteOrder param = new BaseExecuteOrder();
        param.setStatusArray("(1, 2)");
        param.setPayPercent(new BigDecimal(100));
        param.setConditionStartDate(conditionStartDate);
        param.setConditionEndDate(conditionEndDate);

        ResultJson<List<BaseExecuteOrder>> result = executeOrderService.queryAllForContract(param);
        List<BaseExecuteOrder> dataList = result.getData();
        for (BaseExecuteOrder data:dataList) {

            if ("-1".equals(data.getContractStatus())) {
                data.setStrTodoStatus("待签署合同");
                data.setIntTodoStatus("1");

            } else if ("-1".equals(data.getContractImgStatus())) {
                data.setStrTodoStatus("待上传扫描版合同");
                data.setIntTodoStatus("6");
                if (data.getContractImgCount() > 0 ) {
                    data.setIntTodoStatus("7");
                }

            } else if ("-1".equals(data.getOriginalContractStatus())) {
                data.setStrTodoStatus("待获取原章合同");
                data.setIntTodoStatus("11");

            } else if ("-1".equals(data.getExecuteOrderImgStatus())) {
                data.setStrTodoStatus("待上传扫描版排期单");
                data.setIntTodoStatus("16");
                if (data.getExecuteOrderImgCount() > 0 ) {
                    data.setIntTodoStatus("17");
                }

            } else if ("-1".equals(data.getOriginalContractStatus())) {
                data.setStrTodoStatus("待获取原章排期单");
                data.setIntTodoStatus("21");


            } else if ("-1".equals(data.getReminderPaymentStatus())) {
                    data.setStrTodoStatus("待催款");
                    data.setIntTodoStatus("26");

            } else {
                data.setStrTodoStatus("未知状态");
                LOGGER.warn("合同与单据列表中，出现未知状态");
            }

        }
        resultModel.addAttribute("dataList", result.getData());
        return "demand/saleOrder/contractList";
    }

    /**
     * 新增需求单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/add", method = RequestMethod.POST)
    public ResultJson<Integer> add(BaseExecuteOrder baseExecuteOrder, HttpServletRequest request){
        return executeOrderService.add(baseExecuteOrder);
    }

    /**
     * 更新需求单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/update", method = RequestMethod.POST)
    public ResultJson<Integer> update(BaseExecuteOrder baseExecuteOrder, HttpServletRequest request){
        return executeOrderService.update(baseExecuteOrder);
    }

    /**
     * 更新需求单状态
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/updateStatus")
    public ResultJson<Integer> updateStatus(BaseExecuteOrder baseExecuteOrder, HttpServletRequest request){
        return executeOrderService.updateSelective(baseExecuteOrder);
    }

    /**
     * 删除需求单
     *
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/remove", method = RequestMethod.POST)
    public ResultJson<Void> remove(Integer id, HttpServletRequest request){
        return executeOrderService.remove(id);
    }

    /**
     * 跳转新增需求单页面
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(Model resultModel){

        Integer userId = Integer.valueOf(UserThreadLocalContext.getCurrentUser().getUserId());


        SysUser sysUser = userMananger.findById(userId);
        BasePerson basePerson = personMananger.findPersonByName(sysUser.getUserName());
        AssertUtil.assertNotNull(basePerson, MessageConstants.PERSON_IS_NOT_EXISTS);

        Integer areaId = basePerson.getAreaId();

        String areaTreeJson = treeMananger.converToTreeJsonStr(areaService.findAreaNodeList(null).getData());
        List<BasePerson> leaderList = personMananger.findPersonListByArea(areaId);
        List<BaseMonitor> baseMonitorRequestList = monitorRequestService.findAll().getData();
        List<BaseIndustry> industryList = industryManager.findAllEnabledIndustryList();
        List<BaseRegion> regionList = regionManager.findAllActiveRegionList();
        List<BaseMedia> mediaList = mediaMananger.findAllActiveMedia();
        List<BaseAdtype> adtypeList = adtypeMananger.findAllActive();

        Boolean hasSaleAuth = false;
        Boolean hasMediaAuth = false;
        Boolean hasFinancialAuth = false;

        List<Integer> userRoleIdList = roleManager.findUserAllRoleId(userId);
        if (userRoleIdList.contains(RoleEnum.SALER.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALERDM.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALEGM.getIntegerValue())) {
            hasSaleAuth = true;
        }
        if (userRoleIdList.contains(RoleEnum.MEDIA.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.MEDIAGM.getIntegerValue())) {
            hasMediaAuth = true;
        }

        if (userRoleIdList.contains(RoleEnum.FINANCIALGM.getIntegerValue())) {
            hasFinancialAuth = true;
        }


        resultModel.addAttribute("hasSaleAuth", hasSaleAuth);
        resultModel.addAttribute("hasMediaAuth", hasMediaAuth);
        resultModel.addAttribute("hasFinancialAuth", hasFinancialAuth);

        resultModel.addAttribute("areaTreeJson", areaTreeJson);
        resultModel.addAttribute("monitorRequestList", baseMonitorRequestList);
        resultModel.addAttribute("leaderList", leaderList);
        resultModel.addAttribute("industryList", industryList);
        resultModel.addAttribute("regionList", regionList);
        resultModel.addAttribute("salePersonId", basePerson.getId());
        resultModel.addAttribute("salePersonName", basePerson.getName());
        resultModel.addAttribute("mediaListJson", JSONArray.toJSONString(mediaList));
        resultModel.addAttribute("adtypeListJson", JSONArray.toJSONString(adtypeList));
        resultModel.addAttribute("std", BigDecimalUtil.HUNDRED);

        return "demand/saleOrder/add";
    }

    @RequestMapping("/toAddBrach")
    public String toAddBrach( Model model){

        // 复制框架信息
        BaseExecuteOrderFrame param = new BaseExecuteOrderFrame();
        param.setStatus((byte) 1);
        List<BaseExecuteOrderFrame> executeFrameList = saleFrameService.findAll(param).getData();
        model.addAttribute("executeFrameList", executeFrameList);

        return "demand/saleOrder/addBrach";

    }

    /**
     * 跳转新增需求单页面
     *
     * @return
     */
    @RequestMapping("/toAddCopy")
    public String toAddCopy(Integer frameId, Model model){

        Integer userId = Integer.valueOf(UserThreadLocalContext.getCurrentUser().getUserId());
        SysUser sysUser = userMananger.findById(userId);

        BasePerson basePerson = personMananger.findPersonByName(sysUser.getUserName());
        AssertUtil.assertNotNull(basePerson, MessageConstants.PERSON_IS_NOT_EXISTS);

        BaseExecuteOrderFrame baseExecuteOrderFrame = saleFrameService.findById(frameId).getData();
        String areaTreeJson = treeMananger.converToTreeJsonStr(areaService.findAreaNodeList(null).getData());
        BaseArea baseArea = areaService.findById(baseExecuteOrderFrame.getAreaId()).getData();
        List<BasePerson> leaderList = personMananger.findPersonListByArea(baseExecuteOrderFrame.getAreaId());
        List<BaseIndustry> industryList = industryManager.findAllEnabledIndustryList();
        List<BaseRegion> regionList = regionManager.findAllActiveRegionList();
        List<BaseMonitor> baseMonitorRequestList = monitorRequestService.findAll().getData();
        List<BaseMedia> mediaList = mediaMananger.findAllActiveMedia();
        List<BaseAdtype> adtypeList = adtypeMananger.findAllActive();

        List<BaseCustom> signCompanyList = null;
        String signType = baseExecuteOrderFrame.getSignType();
        if (signType != null) {
            signCompanyList = customMananger.findCustomListByCustomType(Integer.valueOf(signType));
        }

        List<BaseCustom> customList = null;
        Integer signCustomId = baseExecuteOrderFrame.getCustomSignId();
        if (signCustomId != null) {
            customList = customMananger.findChildCustom(signCustomId);
        }
        model.addAttribute("customList", customList);

        Boolean hasSaleAuth = false;
        Boolean hasMediaAuth = false;
        Boolean hasFinancialAuth = false;

        List<Integer> userRoleIdList = roleManager.findUserAllRoleId(userId);
        if (userRoleIdList.contains(RoleEnum.SALER.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALERDM.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALEGM.getIntegerValue())) {
            hasSaleAuth = true;
        }
        if (userRoleIdList.contains(RoleEnum.MEDIA.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.MEDIAGM.getIntegerValue())) {
            hasMediaAuth = true;
        }

        if (userRoleIdList.contains(RoleEnum.FINANCIALGM.getIntegerValue())) {
            hasFinancialAuth = true;
        }


        model.addAttribute("hasSaleAuth", hasSaleAuth);
        model.addAttribute("hasMediaAuth", hasMediaAuth);
        model.addAttribute("hasFinancialAuth", hasFinancialAuth);

        List<BaseOrderCpmVO> cpmList = baseExecuteOrderFrame.getBaseOrderCpmList();
        int index = 1;
        JSONArray cpmArrList = new JSONArray();
        for (BaseOrderCpm cpmTemp : cpmList) {
            JSONObject cpmVo = new JSONObject();
            cpmVo.put("state", false);
            cpmVo.put("num", index++);
            cpmVo.put("id", cpmTemp.getId());
            cpmVo.put("mediaId", cpmTemp.getMediaId());
            cpmVo.put("mediaPrice", cpmTemp.getMediaPrice());
            cpmVo.put("firstPrice", cpmTemp.getFirstPrice());
            cpmVo.put("adTypeId", cpmTemp.getAdTypeId());
            cpmVo.put("cpm", cpmTemp.getCpm());
            cpmVo.put("remark", cpmTemp.getRemark());
            cpmArrList.add(cpmVo);
        }
        baseExecuteOrderFrame.setCpmJsonStr(cpmArrList.toJSONString());

        String areaName = (baseArea==null)?"":baseArea.getName();

        model.addAttribute("selectedReginList", StringListUtil.toList(baseExecuteOrderFrame.getDeliveryAreaIds()));
        model.addAttribute("selectMonitorList", StringListUtil.toList(baseExecuteOrderFrame.getMonitorIds()));
        model.addAttribute("selectOurMonitorNameList", StringListUtil.toList(baseExecuteOrderFrame.getOurMonitorName()));
        model.addAttribute("selectReportList", StringListUtil.toList(baseExecuteOrderFrame.getReportTypeId()));

        model.addAttribute("customList", customList);
        model.addAttribute("signCompanyList", signCompanyList);
        model.addAttribute("monitorRequestList", baseMonitorRequestList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("industryList", industryList);
        model.addAttribute("areaName", areaName);
        model.addAttribute("leaderList", leaderList);
        model.addAttribute("areaTreeJson", areaTreeJson);
        model.addAttribute("baseExecuteOrder", baseExecuteOrderFrame);
        model.addAttribute("mediaListJson", JSONArray.toJSONString(mediaList));
        model.addAttribute("adtypeListJson", JSONArray.toJSONString(adtypeList));
        model.addAttribute("format", format);
        model.addAttribute("salePersonId", basePerson.getId());
        model.addAttribute("salePersonName", basePerson.getName());
        model.addAttribute("std", BigDecimalUtil.HUNDRED);

        return "demand/saleOrder/addCopy";
    }

    /**
     * 跳转修改页
     *
     * @param model
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id, Model model){

        Integer userId = Integer.valueOf(UserThreadLocalContext.getCurrentUser().getUserId());
        SysUser sysUser = userMananger.findById(userId);

        BaseExecuteOrder baseExecuteOrder = executeOrderService.findById(id).getData();

        BasePerson basePerson = personMananger.findById(baseExecuteOrder.getPersonSalesId());
        AssertUtil.assertNotNull(basePerson, MessageConstants.SALE_PERSON_IS_NOT_EXISTS);

        String areaTreeJson = treeMananger.converToTreeJsonStr(areaService.findAreaNodeList(null).getData());
        BaseArea baseArea = areaService.findById(baseExecuteOrder.getAreaId()).getData();
        List<BasePerson> leaderList = personMananger.findPersonListByArea(baseExecuteOrder.getAreaId());
        List<BaseIndustry> industryList = industryManager.findAllEnabledIndustryList();
        List<BaseRegion> regionList = regionManager.findAllActiveRegionList();
        List<BaseMonitor> baseMonitorRequestList = monitorRequestService.findAll().getData();
        List<BaseMedia> mediaList = mediaMananger.findAllActiveMedia();
        List<BaseAdtype> adtypeList = adtypeMananger.findAllActive();

        List<BaseCustom> signCompanyList = null;
        String signType = baseExecuteOrder.getSignType();
        if (signType != null) {
            signCompanyList = customMananger.findCustomListByCustomType(Integer.valueOf(signType));
        }

        List<BaseCustom> customList = null;
        Integer signCustomId = baseExecuteOrder.getCustomSignId();
        if (signCustomId != null) {
            customList = customMananger.findChildCustom(signCustomId);
        }
        model.addAttribute("customList", customList);

        Boolean hasSaleAuth = false;
        Boolean hasMediaAuth = false;
        Boolean hasFinancialAuth = false;

        List<Integer> userRoleIdList = roleManager.findUserAllRoleId(userId);
        if (userRoleIdList.contains(RoleEnum.SALER.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALERDM.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.SALEGM.getIntegerValue())) {
            hasSaleAuth = true;
        }
        if (userRoleIdList.contains(RoleEnum.MEDIA.getIntegerValue())
                || userRoleIdList.contains(RoleEnum.MEDIAGM.getIntegerValue())) {
            hasMediaAuth = true;
        }

        if (userRoleIdList.contains(RoleEnum.FINANCIALGM.getIntegerValue())) {
            hasFinancialAuth = true;
        }


        model.addAttribute("hasSaleAuth", hasSaleAuth);
        model.addAttribute("hasMediaAuth", hasMediaAuth);
        model.addAttribute("hasFinancialAuth", hasFinancialAuth);

        List<BaseOrderCpmVO> cpmList = baseExecuteOrder.getBaseOrderCpmList();
        int index = 1;
        JSONArray cpmArrList = new JSONArray();
        for (BaseOrderCpm cpmTemp : cpmList) {
            JSONObject cpmVo = new JSONObject();
            cpmVo.put("state", false);
            cpmVo.put("num", index++);
            cpmVo.put("id", cpmTemp.getId());
            cpmVo.put("mediaId", cpmTemp.getMediaId());
            cpmVo.put("mediaPrice", cpmTemp.getMediaPrice());
            cpmVo.put("firstPrice", cpmTemp.getFirstPrice());
            cpmVo.put("adTypeId", cpmTemp.getAdTypeId());
            cpmVo.put("cpm", cpmTemp.getCpm());
            cpmVo.put("remark", cpmTemp.getRemark());
            cpmArrList.add(cpmVo);
        }
        baseExecuteOrder.setCpmJsonStr(cpmArrList.toJSONString());

        String areaName = (baseArea==null)?"":baseArea.getName();

        model.addAttribute("selectedReginList", StringListUtil.toList(baseExecuteOrder.getDeliveryAreaIds()));
        model.addAttribute("selectMonitorList", StringListUtil.toList(baseExecuteOrder.getMonitorIds()));
        model.addAttribute("selectOurMonitorNameList", StringListUtil.toList(baseExecuteOrder.getOurMonitorName()));
        model.addAttribute("selectReportList", StringListUtil.toList(baseExecuteOrder.getReportTypeId()));

        model.addAttribute("customList", customList);
        model.addAttribute("signCompanyList", signCompanyList);
        model.addAttribute("monitorRequestList", baseMonitorRequestList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("industryList", industryList);
        model.addAttribute("areaName", areaName);
        model.addAttribute("leaderList", leaderList);
        model.addAttribute("areaTreeJson", areaTreeJson);
        model.addAttribute("baseExecuteOrder", baseExecuteOrder);
        model.addAttribute("mediaListJson", JSONArray.toJSONString(mediaList));
        model.addAttribute("adtypeListJson", JSONArray.toJSONString(adtypeList));
        model.addAttribute("format", format);

        model.addAttribute("salePersonId", basePerson.getId());
        model.addAttribute("salePersonName", basePerson.getName());
        model.addAttribute("std", BigDecimalUtil.HUNDRED);

        return "demand/saleOrder/update";
    }



    /**
     * 跳转修改页
     *
     * @param model
     * @return
     */
    @RequestMapping("/refer")
    public String toRefer(Integer id, Model model){

        Integer userId = Integer.valueOf(UserThreadLocalContext.getCurrentUser().getUserId());
        SysUser sysUser = userMananger.findById(userId);
//        BasePerson basePerson = personMananger.findPersonByName(sysUser.getUserName());

        BaseExecuteOrder baseExecuteOrder = executeOrderService.findById(id).getData();

        BasePerson basePerson = personMananger.findById(baseExecuteOrder.getPersonSalesId());
        AssertUtil.assertNotNull(basePerson, MessageConstants.PERSON_IS_NOT_EXISTS);

        String areaTreeJson = treeMananger.converToTreeJsonStr(areaService.findAreaNodeList(null).getData());
        BaseArea baseArea = areaService.findById(baseExecuteOrder.getAreaId()).getData();
        List<BasePerson> leaderList = personMananger.findPersonListByArea(baseExecuteOrder.getAreaId());
        List<BaseIndustry> industryList = industryManager.findAllEnabledIndustryList();
        List<BaseRegion> regionList = regionManager.findAllActiveRegionList();
        List<BaseMonitor> baseMonitorRequestList = monitorRequestService.findAll().getData();
        List<BaseMedia> mediaList = mediaMananger.findAllActiveMedia();
        List<BaseAdtype> adtypeList = adtypeMananger.findAllActive();

        List<BaseCustom> signCompanyList = null;
        String signType = baseExecuteOrder.getSignType();
        if (signType != null) {
            signCompanyList = customMananger.findCustomListByCustomType(Integer.valueOf(signType));
        }

        List<BaseCustom> customList = null;
        Integer signCustomId = baseExecuteOrder.getCustomSignId();
        if (signCustomId != null) {
            customList = customMananger.findChildCustom(signCustomId);
        }
        model.addAttribute("customList", customList);

        List<BaseOrderCpmVO> cpmList = baseExecuteOrder.getBaseOrderCpmList();
        int index = 1;
        JSONArray cpmArrList = new JSONArray();
        for (BaseOrderCpmVO cpmTemp : cpmList) {
            JSONObject cpmVo = new JSONObject();
            cpmVo.put("state", false);
            cpmVo.put("num", index++);
            cpmVo.put("id", cpmTemp.getId());
            cpmVo.put("mediaName", cpmTemp.getMediaName());
            cpmVo.put("mediaPrice", cpmTemp.getMediaPrice());
            cpmVo.put("firstPrice", cpmTemp.getFirstPrice());
            cpmVo.put("adTypeName", cpmTemp.getAdTypeName());
            cpmVo.put("cpm", cpmTemp.getCpm());
            cpmVo.put("remark", cpmTemp.getRemark());
            cpmArrList.add(cpmVo);
        }
        baseExecuteOrder.setCpmJsonStr(cpmArrList.toJSONString());

        String areaName = (baseArea==null)?"":baseArea.getName();


        model.addAttribute("selectedReginList", StringListUtil.toList(baseExecuteOrder.getDeliveryAreaIds()));
        model.addAttribute("selectMonitorList", StringListUtil.toList(baseExecuteOrder.getMonitorIds()));
        model.addAttribute("selectOurMonitorNameList", StringListUtil.toList(baseExecuteOrder.getOurMonitorName()));
        model.addAttribute("selectReportList", StringListUtil.toList(baseExecuteOrder.getReportTypeId()));

        model.addAttribute("customList", customList);
        model.addAttribute("signCompanyList", signCompanyList);
        model.addAttribute("monitorRequestList", baseMonitorRequestList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("industryList", industryList);
        model.addAttribute("areaName", areaName);
        model.addAttribute("leaderList", leaderList);
        model.addAttribute("areaTreeJson", areaTreeJson);
        model.addAttribute("baseExecuteOrder", baseExecuteOrder);
        model.addAttribute("mediaListJson", JSONArray.toJSONString(mediaList));
        model.addAttribute("adtypeListJson", JSONArray.toJSONString(adtypeList));
        model.addAttribute("format", format);
        model.addAttribute("salePersonId", basePerson.getId());
        model.addAttribute("salePersonName", basePerson.getName());
        model.addAttribute("std", BigDecimalUtil.HUNDRED);

        return "demand/saleOrder/refer";
    }

    @RequestMapping(value="fileUpload",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String fileUpload(Integer bizId, String picType, String uploadType, Model model
//                             @RequestParam("file") CommonsMultipartFile[] imgFile,
                             ,HttpServletRequest request
    ){
//        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> imgFileList = multipartRequest.getFiles("file");
        LOGGER.info("共上传【{}】个文件", imgFileList.size());

        BaseExecuteOrder baseExecuteOrder = executeOrderService.findById(bizId).getData();
        String orderNum = baseExecuteOrder.getOrderNum();

        String path = uploadImgBase;
        if ("1".equals(picType)) { // 上传扫描版合同
            path = path + (new DateTime().getYear()) + uploadImgContract;
        } else if ("2".equals(picType)) { // 上传扫描版排期单
            path = path + (new DateTime().getYear()) + uploadImgExeOrder;
        }
        LOGGER.info("文件上传路径：{}", path);
        if(!new File(path).exists())   {
            new File(path).mkdirs();
        }

        if ("reupload".equals(uploadType)) { // 续传
            fileUploadService.removeByName(baseExecuteOrder.getOrderNum(), picType);
        }

        DateTime dateTime=new DateTime();
        for (MultipartFile file:imgFileList) {
            String fileName = file.getOriginalFilename();
            if (fileName.length() > 30) { // 文件名过长时，改名
                fileName = System.currentTimeMillis() + ".jpg";
            }
            fileName = orderNum + "_" + dateTime.toString("yyyyMMddhhmmss") + "_" + StringUtil.getRandom6Str() + "_" + fileName;
            String filePath = path + fileName;
            LOGGER.debug("file path:{}", filePath);
            LOGGER.debug("file path:{}", filePath.substring(filePath.indexOf("/workflow-admin"), filePath.length()));

			try {
	            FileOutputStream fout = new FileOutputStream(filePath);
	            IOUtils.write(file.getBytes(), fout);
	            fout.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
            BaseFileupload obj = new BaseFileupload();
            obj.setBizName(baseExecuteOrder.getOrderNum());
            obj.setFileName(filePath.substring(filePath.indexOf("/workflow-admin"), filePath.length()));
            obj.setCreatorId(UserThreadLocalContext.getCurrentUser().getUserId());
            fileUploadService.add(obj);
        }

        return "aaaaaa";
    }


}
