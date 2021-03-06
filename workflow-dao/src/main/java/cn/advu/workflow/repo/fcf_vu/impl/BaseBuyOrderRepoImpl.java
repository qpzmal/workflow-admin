package cn.advu.workflow.repo.fcf_vu.impl;

import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.dao.fcf_vu.BaseBuyOrderMapper;
import cn.advu.workflow.dao.fcf_vu.BaseOrderCpmMapper;
import cn.advu.workflow.domain.enums.CpmTypeEnum;
import cn.advu.workflow.domain.fcf_vu.BaseBuyOrder;
import cn.advu.workflow.domain.fcf_vu.BaseOrderCpm;
import cn.advu.workflow.domain.fcf_vu.BaseOrderCpmVO;
import cn.advu.workflow.repo.base.impl.AbstractRepo;
import cn.advu.workflow.repo.fcf_vu.BaseBuyOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 需求订单
 *
 */
@Repository
public class BaseBuyOrderRepoImpl extends AbstractRepo<BaseBuyOrder> implements BaseBuyOrderRepo {

    @Autowired
    BaseBuyOrderMapper baseBuyOrderMapper;
    @Autowired
    BaseOrderCpmMapper baseOrderCpmMapper;

    @Override
    protected BaseDAO<BaseBuyOrder> getSqlMapper() {
        return baseBuyOrderMapper;
    }

    @Override
    public List<BaseBuyOrder> findAll(BaseBuyOrder baseBuyOrder) {
        return baseBuyOrderMapper.queryAll(baseBuyOrder);
    }

    @Override
    public int addSelective(BaseBuyOrder entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().insertSelective(entity);
            List<BaseOrderCpmVO> baseOrderCpmList = entity.getBaseOrderCpmList();
            if (baseOrderCpmList != null && !baseOrderCpmList.isEmpty()) {
                for (BaseOrderCpm baseOrderCpm : baseOrderCpmList) {
                    baseOrderCpm.setOrderId(entity.getId());
                    baseOrderCpmMapper.insertSelective(baseOrderCpm);
                }
            }
        }

        return count;
    }


    @Override
    public int update(BaseBuyOrder entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().updateByPrimaryKey(entity);

            baseOrderCpmMapper.deleteByOrderAndType(entity.getId(), Integer.valueOf(CpmTypeEnum.BUY.getValue()));

            List<BaseOrderCpmVO> baseOrderCpmList = entity.getBaseOrderCpmList();
            if (baseOrderCpmList != null && !baseOrderCpmList.isEmpty()) {
                for (BaseOrderCpm baseOrderCpm : baseOrderCpmList) {
                    baseOrderCpm.setId(null);
                    baseOrderCpm.setOrderId(entity.getId());
                    baseOrderCpmMapper.insertSelective(baseOrderCpm);
                }
            }
        }

        return count;
    }

    @Override
    public int logicRemove(BaseBuyOrder entity) {
        int count = 0;
        if (entity != null) {
            entity.setDelFlag("1");
            count = getSqlMapper().updateByPrimaryKeySelective(entity);

            List<BaseOrderCpmVO> baseOrderCpmList = entity.getBaseOrderCpmList();
            if (baseOrderCpmList != null && !baseOrderCpmList.isEmpty()) {
                for (BaseOrderCpm baseOrderCpm : baseOrderCpmList) {
                    baseOrderCpm.setDelFlag("1");
                    baseOrderCpmMapper.updateByPrimaryKeySelective(baseOrderCpm);
                }
            }

        }
        return count;
    }
}
