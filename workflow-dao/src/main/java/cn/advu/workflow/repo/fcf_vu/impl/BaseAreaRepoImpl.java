package cn.advu.workflow.repo.fcf_vu.impl;

import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.dao.base.BaseTreeDAO;
import cn.advu.workflow.dao.fcf_vu.BaseAreaMapper;
import cn.advu.workflow.dao.fcf_vu.BaseRegionMapper;
import cn.advu.workflow.domain.fcf_vu.AreaVO;
import cn.advu.workflow.domain.fcf_vu.BaseArea;
import cn.advu.workflow.domain.fcf_vu.BaseRegion;
import cn.advu.workflow.repo.base.impl.AbstractRepo;
import cn.advu.workflow.repo.base.impl.AbstractTreeRepo;
import cn.advu.workflow.repo.fcf_vu.BaseAreaRepo;
import cn.advu.workflow.repo.fcf_vu.BaseRegionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户
 *
 */
@Repository
public class BaseAreaRepoImpl extends AbstractTreeRepo<BaseArea> implements BaseAreaRepo {

    @Autowired
    BaseAreaMapper baseAreaMapper;

    @Override
    protected BaseTreeDAO<BaseArea> getTreeSqlMapper() {
        return baseAreaMapper;
    }

    @Override
    public List<BaseArea> findAll() {
        return baseAreaMapper.queryAll(null);
    }

    @Override
    public List<BaseArea> findByParent(Integer parentId) {
        return baseAreaMapper.queryByParent(parentId);
    }

    @Override
    public List<AreaVO> findByParentAreaVO(Integer parentId) {
        return baseAreaMapper.queryByParentAreaVO(parentId);
    }

    @Override
    public BaseArea findByIdAndName(Integer id, String name) {
        return baseAreaMapper.queryByIdAndName(id, name);
    }

    @Override
    public BaseArea findByIdAndCode(Integer id, String code) {
        return baseAreaMapper.queryByIdAndCode(id, code);
    }

}
