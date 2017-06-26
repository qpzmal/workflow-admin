package cn.advu.workflow.repo.fcf_vu.impl;

import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.dao.fcf_vu.BaseMonitorRequestMapper;
import cn.advu.workflow.domain.fcf_vu.BaseAdtype;
import cn.advu.workflow.domain.fcf_vu.BaseMonitorRequest;
import cn.advu.workflow.repo.base.impl.AbstractRepo;
import cn.advu.workflow.repo.fcf_vu.BaseMonitorRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 媒体
 *
 */
@Repository
public class BaseMonitorRequestRepoImpl extends AbstractRepo<BaseMonitorRequest> implements BaseMonitorRequestRepo {

    @Autowired
    BaseMonitorRequestMapper baseMonitorRequestMapper;

    @Override
    protected BaseDAO<BaseMonitorRequest> getSqlMapper() {
        return baseMonitorRequestMapper;
    }

    @Override
    public List<BaseMonitorRequest> findAll() {
        return baseMonitorRequestMapper.queryAll(null);
    }
}
