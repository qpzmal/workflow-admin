package cn.advu.workflow.web.service.base.impl;

import cn.advu.workflow.domain.fcf_vu.BaseIndustry;
import cn.advu.workflow.repo.fcf_vu.BaseIndustryRepo;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.service.base.IndustryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by weiqz on 2017/6/25.
 */
@Service
public class IndustryServiceImpl implements IndustryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndustryServiceImpl.class);

    @Autowired
    private BaseIndustryRepo baseIndustryRepo;


    @Override
    public ResultJson<Integer> addIndustry(BaseIndustry baseIndustry) {
        Integer insertCount = baseIndustryRepo.addSelective(baseIndustry);
        if(insertCount != 1){
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "创建客户行业失败!");
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<Integer> updateIndustry(BaseIndustry baseIndustry) {
        if (baseIndustry.getId() == null) {
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "客户行业ID没有设置!");
        }
        Integer updateCount = baseIndustryRepo.updateSelective(baseIndustry);
        if(updateCount != 1){
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "更新客户行业失败!");
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<List<BaseIndustry>> findAll() {
        ResultJson<List<BaseIndustry>> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseIndustryRepo.findAll());
        return result;
    }

    @Override
    public ResultJson<BaseIndustry> findById(Integer id) {
        ResultJson<BaseIndustry> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseIndustryRepo.findOne(id));
        return result;
    }
}
