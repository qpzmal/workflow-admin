package cn.advu.workflow.dao.fcf_vu;

import cn.advu.workflow.dao.base.ISqlMapper;
import cn.advu.workflow.domain.fcf_vu.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysUserMapper extends ISqlMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);


    // 以下为自定义SQL
    List<SysUser> queryAll(@Param("item_status") int status);

    SysUser queryUserByNameAndId(@Param("username") String userName, @Param("userid") String userid);

    SysUser queryUserByNameAndPassword(@Param("username") String userName, @Param("password") String password);


}