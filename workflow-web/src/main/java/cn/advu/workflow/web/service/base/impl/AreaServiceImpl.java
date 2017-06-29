package cn.advu.workflow.web.service.base.impl;

import cn.advu.workflow.dao.fcf_vu.BaseAreaMapper;
import cn.advu.workflow.domain.fcf_vu.BaseArea;
import cn.advu.workflow.domain.fcf_vu.BaseRegion;
import cn.advu.workflow.repo.fcf_vu.BaseAreaRepo;
import cn.advu.workflow.web.common.ResultJson;
import cn.advu.workflow.web.common.constant.WebConstants;
import cn.advu.workflow.web.service.base.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by weiqz on 2017/6/25.
 */
@Service
public class AreaServiceImpl implements AreaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AreaServiceImpl.class);

    @Autowired
    private BaseAreaRepo baseAreaRepo;

    @Override
    public ResultJson<List<BaseArea>> findAll() {
        ResultJson<List<BaseArea>> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseAreaRepo.findAll());
        return result;
    }

    @Override
    public ResultJson<Integer> addArea(BaseArea baseArea) {
        Integer insertCount = baseAreaRepo.addSelective(baseArea);
        if(insertCount != 1){
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "创建区域失败!");
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<Integer> updateArea(BaseArea baseArea) {
        if (baseArea.getId() == null) {
        return new ResultJson<>(WebConstants.OPERATION_FAILURE, "ID没有设置!");
        }
        Integer insertCount = baseAreaRepo.updateSelective(baseArea);
        if(insertCount != 1){
            return new ResultJson<>(WebConstants.OPERATION_FAILURE, "更新区域失败!");
        }
        return new ResultJson<>(WebConstants.OPERATION_SUCCESS);
    }

    @Override
    public ResultJson<BaseArea> findById(Integer id) {
        ResultJson<BaseArea> result = new ResultJson<>(WebConstants.OPERATION_SUCCESS);
        result.setData(baseAreaRepo.findOne(id));
        return result;
    }
}
