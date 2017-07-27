package cn.advu.workflow.web.service.base.impl;

import cn.advu.workflow.domain.enums.CustomTypeEnum;
import cn.advu.workflow.domain.fcf_vu.BaseArea;
import cn.advu.workflow.domain.fcf_vu.BaseCustom;
import cn.advu.workflow.domain.fcf_vu.BaseExecuteOrder;
import cn.advu.workflow.domain.fcf_vu.BaseOrderCpmVO;
import cn.advu.workflow.repo.fcf_vu.BaseExecuteOrderRepo;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.common.loginContext.UserThreadLocalContext;
import cn.advu.workflow.web.constants.MessageConstants;
import cn.advu.workflow.web.exception.ServiceException;
import cn.advu.workflow.web.manager.AreaManager;
import cn.advu.workflow.web.manager.CpmManager;
import cn.advu.workflow.web.manager.CustomMananger;
import cn.advu.workflow.web.service.base.ExecuteOrderService;
import cn.advu.workflow.web.util.AssertUtil;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by weiqz on 2017/6/25.
 */
@Service
public class ExecuteOrderServiceImpl extends  AbstractOrderService implements ExecuteOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteOrderServiceImpl.class);

    @Autowired
    private BaseExecuteOrderRepo baseExecuteOrderRepo;

    @Autowired
    CustomMananger customMananger;



    @Autowired
    AreaManager areaManager;

    @Autowired
    CpmManager cpmManager;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;




    @Override
    public ResultJson<List<BaseExecuteOrder>> findAll(BaseExecuteOrder param) {
        ResultJson<List<BaseExecuteOrder>> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseExecuteOrderRepo.findAll(param));
        return result;
    }

    @Override
    public ResultJson<Integer> add(BaseExecuteOrder baseExecuteOrder) {


        AssertUtil.assertNotNullOrEmpty(baseExecuteOrder.getSignType(), "签约类型");

        String customSignName = baseExecuteOrder.getCustomSignName();
        String customAdverserName = baseExecuteOrder.getCustomAdverserName();

        AssertUtil.assertNotNullOrEmpty(customSignName, "签约公司");
        AssertUtil.assertNotNullOrEmpty(customAdverserName, "广告主");

        customSignName = customSignName.trim();
        customAdverserName = customAdverserName.trim();

        AssertUtil.assertNotNullOrEmpty(customSignName, "签约公司");
        AssertUtil.assertNotNullOrEmpty(customAdverserName, "广告主");


        BaseCustom customSign = customMananger.findByName(customSignName);
        if (customSign == null) {
            customSign = new BaseCustom();
            customSign.setName(customSignName);
            customSign.setCustomType(baseExecuteOrder.getSignType());
            customMananger.add(customSign);
        }

        baseExecuteOrder.setCustomSignId(customSign.getId());

        Integer customAdverserId = customSign.getId();

        if (customSignName.equals(customAdverserName) && CustomTypeEnum.FA.getValue().equalsIgnoreCase(baseExecuteOrder.getSignType())) {
            throw new ServiceException(MessageConstants.ADVERSER_IS_4A);
        }
        if ( CustomTypeEnum.FA.getValue().equalsIgnoreCase(baseExecuteOrder.getSignType())) {
            BaseCustom customAdverser = customMananger.findByName(customAdverserName);
            baseExecuteOrder.setCustomSignId(customSign.getId());
            if (customAdverser == null) {
                customAdverser = new BaseCustom();
                customAdverser.setName(customAdverserName);
                customAdverser.setCustomType(CustomTypeEnum.MA.getValue());
                customAdverser.setParentId(customSign.getId());
                customMananger.add(customAdverser);
            }
            customAdverserId = customAdverser.getId();
        }

        baseExecuteOrder.setCustomAdverserId(customAdverserId);

        AssertUtil.assertNotNull(baseExecuteOrder.getCustomSignId(), MessageConstants.SIGN_CUSTOM_TYPE_IS_NULL);
        // 编码
        String orderNumSeqStr = this.buildOrderNumSeqStr();
        BaseArea baseArea = areaManager.findById(baseExecuteOrder.getAreaId());
        AssertUtil.assertNotNull(baseArea, MessageConstants.AREA_IS_NOT_EXISTS);

        String areaCode = baseArea.getCode();
        String orderNum = "S" + findCustomType(baseExecuteOrder.getSignType()) + areaCode + orderNumSeqStr;
        baseExecuteOrder.setOrderNum(orderNum);

        // 补充编码
        if (StringUtils.isEmpty(baseExecuteOrder.getSecOrderNum())) {
            baseExecuteOrder.setSecOrderNum(orderNum);
        }
        String userId = UserThreadLocalContext.getCurrentUser().getUserId();
        baseExecuteOrder.setUserId(Integer.valueOf(userId));

        // CPM
        buildExecuteCpm(baseExecuteOrder);

        if (baseExecuteOrder.getFrameId() != null && baseExecuteOrder.getFrameId() != 0) {
            baseExecuteOrder.setType("1"); // 框架
        } else {
            baseExecuteOrder.setType("2"); // 单采
        }
        Integer insertCount = baseExecuteOrderRepo.addSelective(baseExecuteOrder);
        if(insertCount != 1){
            throw new ServiceException("创建需求单失败!");
        }

        // 发起工作流
        return this.startWorkFlow(baseExecuteOrder);
    }

    @Override
    public ResultJson<Integer> update(BaseExecuteOrder baseExecuteOrder) {

        AssertUtil.assertNotNullOrEmpty(baseExecuteOrder.getSignType(), "签约类型");
        String customSignName = baseExecuteOrder.getCustomSignName();
        String customAdverserName = baseExecuteOrder.getCustomAdverserName();

        AssertUtil.assertNotNullOrEmpty(customSignName, "签约公司");
        AssertUtil.assertNotNullOrEmpty(customAdverserName, "广告主");

        customSignName = customSignName.trim();
        customAdverserName = customAdverserName.trim();

        AssertUtil.assertNotNullOrEmpty(customSignName, "签约公司");
        AssertUtil.assertNotNullOrEmpty(customAdverserName, "广告主");


        BaseCustom customSign = customMananger.findByName(customSignName);
        if (customSign == null) {
            customSign = new BaseCustom();
            customSign.setName(customSignName);
            customSign.setCustomType(baseExecuteOrder.getSignType());
            customMananger.add(customSign);
        }

        baseExecuteOrder.setCustomSignId(customSign.getId());

        Integer customAdverserId = customSign.getId();

        if (customSignName.equals(customAdverserName) && CustomTypeEnum.FA.getValue().equalsIgnoreCase(baseExecuteOrder.getSignType())) {
            throw new ServiceException(MessageConstants.ADVERSER_IS_4A);
        }
        if ( CustomTypeEnum.FA.getValue().equalsIgnoreCase(baseExecuteOrder.getSignType())) {
            BaseCustom customAdverser = customMananger.findByName(customAdverserName);
            baseExecuteOrder.setCustomSignId(customSign.getId());
            if (customAdverser == null) {
                customAdverser = new BaseCustom();
                customAdverser.setName(customAdverserName);
                customAdverser.setCustomType(CustomTypeEnum.MA.getValue());
                customAdverser.setParentId(customSign.getId());
                customMananger.add(customAdverser);
            }
            customAdverserId = customAdverser.getId();
        }
        baseExecuteOrder.setCustomAdverserId(customAdverserId);
        AssertUtil.assertNotNull(baseExecuteOrder.getCustomSignId(), MessageConstants.SIGN_CUSTOM_TYPE_IS_NULL);
        // 补充编码
        if (StringUtils.isEmpty(baseExecuteOrder.getSecOrderNum())) {
            baseExecuteOrder.setSecOrderNum(baseExecuteOrder.getOrderNum());
        }

        // CPM
        buildExecuteCpm(baseExecuteOrder);

        if (baseExecuteOrder.getId() == null) {
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "ID没有设置!");
        }
        Integer insertCount = baseExecuteOrderRepo.update(baseExecuteOrder);
        if(insertCount != 1){
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "更新需求单失败!");
        }

        // 发起工作流
        return this.startWorkFlow(baseExecuteOrder);
    }

    @Override
    public ResultJson<Void> remove(Integer id) {

        BaseExecuteOrder baseExecuteOrder = baseExecuteOrderRepo.findOne(id);
        List<BaseOrderCpmVO> cpmList = cpmManager.findExecuteOrderFrameCpm(id);
        baseExecuteOrder.setBaseOrderCpmList(cpmList);

        Integer count = baseExecuteOrderRepo.logicRemove(baseExecuteOrder);
        if (count == 0) {
            throw new ServiceException("需求单不存在！");
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<BaseExecuteOrder> findById(Integer id) {
        ResultJson<BaseExecuteOrder> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        BaseExecuteOrder baseExecuteOrder = baseExecuteOrderRepo.findOne(id);
        result.setData(baseExecuteOrder);

        List<BaseOrderCpmVO> cpmList = cpmManager.findOrderCustomCpm(id);
        baseExecuteOrder.setBaseOrderCpmList(cpmList);

        return result;
    }

    private ResultJson<Integer> startWorkFlow(BaseExecuteOrder baseExecuteOrder) {
        LOGGER.info("startWorkFlow-flowType:{}", baseExecuteOrder.getFlowType());
        if (WebConstants.WorkFlow.START.equals(baseExecuteOrder.getFlowType())) {
            String userName = UserThreadLocalContext.getCurrentUser().getUserName();
            String processKey = WebConstants.WORKFLOW_SALE_ORDER;
            LOGGER.debug("userName:{}, processKey:{}", userName, processKey);

            try {
                // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
                identityService.setAuthenticatedUserId(userName);

                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, baseExecuteOrder.getId() + "", new HashMap<String, Object>());
                LOGGER.info(" processInstanceId:{}", processInstance.getId());

                baseExecuteOrder.setProcessInstanceId(processInstance.getId());
                baseExecuteOrder.setStatus(WebConstants.WorkFlow.STATUS_0);
                baseExecuteOrderRepo.updateSelective(baseExecuteOrder);

            } catch (ActivitiException e) {
                if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                    LOGGER.warn("没有部署[ " + processKey + " ]流程!", e);
                    return new ResultJson<>(WebConstants.OPERATION_FAILURE, "没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>");
                } else {
                    LOGGER.error("启动[ " + processKey + " ]流程失败：", e);
                    return new ResultJson<>(WebConstants.OPERATION_FAILURE, "系统内部错误！");
                }
            } catch (Exception e) {
                LOGGER.error("启动[ " + processKey + " ]流程失败：", e);
                return new ResultJson<>(WebConstants.OPERATION_FAILURE, "系统内部错误！");
            }
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }
}
