package cn.advu.workflow.repo.base.impl;


import cn.advu.workflow.dao.base.BaseDAO;
import cn.advu.workflow.dao.fcf_vu.SysLogMapper;
import cn.advu.workflow.domain.base.IEntity;
import cn.advu.workflow.repo.base.IRepo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 抽象仓库
 *
 * @param <T>
 */
public abstract class AbstractRepo<T extends IEntity> implements IRepo<T> {

    protected abstract BaseDAO<T> getSqlMapper();

    @Override
    public int add(T entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().insert(entity);
        }

        return count;
    }

    @Override
    public int addSelective(T entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().insertSelective(entity);
        }
        return count;
    }

    @Override
    public int update(T entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().updateByPrimaryKey(entity);
        }

        return count;
    }

    @Override
    public int updateSelective(T entity) {
        int count = 0;
        if (entity != null) {
            count = getSqlMapper().updateByPrimaryKeySelective(entity);
        }
        return count;
    }

    @Override
    public int remove(Integer id) {
        int count = 0;
        if (id != null) {
            count = getSqlMapper().deleteByPrimaryKey(id);
        }
        return count;
    }

    @Override
    public T findOne(Integer id) {
        T entity = null;
        if (id != null) {
            entity = getSqlMapper().selectByPrimaryKey(id);
        }
        return entity;
    }

    @Override
    public int logicRemove(T entity) {
        int count = 0;
        if (entity != null) {
            entity.setDelFlag("1");
            count = getSqlMapper().updateByPrimaryKeySelective(entity);
        }
        return count;
    }

}
