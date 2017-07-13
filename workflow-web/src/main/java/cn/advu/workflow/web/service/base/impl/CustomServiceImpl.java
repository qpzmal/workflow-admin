package cn.advu.workflow.web.service.base.impl;

import cn.advu.workflow.domain.enums.CustomTypeEnum;
import cn.advu.workflow.domain.fcf_vu.BaseCustom;
import cn.advu.workflow.repo.fcf_vu.BaseCustomRepo;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.constants.MessageConstants;
import cn.advu.workflow.web.exception.ServiceException;
import cn.advu.workflow.web.manager.CustomMananger;
import cn.advu.workflow.web.service.base.CustomService;
import cn.advu.workflow.web.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by weiqz on 2017/6/25.
 */
@Service
public class CustomServiceImpl implements CustomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomServiceImpl.class);

    @Autowired
    private BaseCustomRepo baseCustomRepo;

    @Autowired
    private CustomMananger customMananger;

    @Override
    public ResultJson<List<BaseCustom>> findAll() {
        ResultJson<List<BaseCustom>> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseCustomRepo.findAll());
        return result;
    }

    @Override
    public ResultJson<Integer> add(BaseCustom baseCustom) {

        AssertUtil.assertNotNull(baseCustom);
        AssertUtil.assertNotNullOrEmpty(baseCustom.getName(), "名称");

        // 名称重复检查
        Boolean isNameDuplicated = customMananger.isNameDuplicated(baseCustom.getId(), baseCustom.getName());
        if (isNameDuplicated) {
            throw new ServiceException(MessageConstants.NAME_IS_DUPLICATED);
        }

        // 新增客户
        Integer insertCount = baseCustomRepo.add(baseCustom);
        if(insertCount != 1){
            throw new ServiceException("创建客户失败!");
        }

        ResultJson<Integer> resultJson = new ResultJson<>();
        resultJson.setData(baseCustom.getId());
        return resultJson;
    }

    @Override
    public ResultJson<Integer> update(BaseCustom baseCustom) {

        AssertUtil.assertNotNull(baseCustom);
        Integer id = baseCustom.getId();
        AssertUtil.assertNotNull(id);

        // 名称重复检查
        Boolean isNameDuplicated = customMananger.isNameDuplicated(id, baseCustom.getName());
        if (isNameDuplicated) {
            throw new ServiceException(MessageConstants.NAME_IS_DUPLICATED);
        }

        // 4A -> 直客，广告主
        BaseCustom oldBaseCustom = baseCustomRepo.findOne(id);
        AssertUtil.assertNotNull(oldBaseCustom, MessageConstants.CUSTOM_IS_NOT_EXISTS);
        String oldCustomType = oldBaseCustom.getCustomType();
        String customType = oldBaseCustom.getCustomType();
        AssertUtil.assertNotNull(oldCustomType);
        AssertUtil.assertNotNull(customType);
        if (CustomTypeEnum.FA.getValue().equals(oldCustomType) && !oldCustomType.equals(customType)) {
            List<BaseCustom> faChildCustomList = baseCustomRepo.find4AChildCustom(id);
            if (faChildCustomList != null && faChildCustomList.size() > 0) {
                throw new ServiceException(MessageConstants.FA_CUSTOM_HAS_CHILD);
            }
        }

        // 更新客户
        Integer updateCount = baseCustomRepo.update(baseCustom);
        if(updateCount != 1) {
            throw new ServiceException(MessageConstants.CUSTOM_IS_NOT_EXISTS);
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<Void> remove(Integer id) {
        AssertUtil.assertNotNull(id);
        // 4A -> 直客，广告主
        BaseCustom oldBaseCustom = baseCustomRepo.findOne(id);
        AssertUtil.assertNotNull(oldBaseCustom, MessageConstants.CUSTOM_IS_NOT_EXISTS);
        String oldCustomType = oldBaseCustom.getCustomType();
        String customType = oldBaseCustom.getCustomType();
        AssertUtil.assertNotNull(oldCustomType);
        AssertUtil.assertNotNull(customType);
        if (CustomTypeEnum.FA.getValue().equals(oldCustomType)) {
            List<BaseCustom> faChildCustomList = baseCustomRepo.find4AChildCustom(id);
            if (faChildCustomList != null && faChildCustomList.size() > 0) {
                throw new ServiceException(MessageConstants.FA_CUSTOM_HAS_CHILD_FOR_REMOVE);
            }
        }
        int updateCount = baseCustomRepo.logicRemove(oldBaseCustom);
        if(updateCount != 1) {
            throw new ServiceException(MessageConstants.CUSTOM_IS_NOT_EXISTS);
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<BaseCustom> findById(Integer id) {
        ResultJson<BaseCustom> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseCustomRepo.findOne(id));
        return result;
    }

    @Override
    public ResultJson<List<BaseCustom>> findParentCustom() {
        ResultJson<List<BaseCustom>> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseCustomRepo.findParentCustom());
        return result;
    }
}
