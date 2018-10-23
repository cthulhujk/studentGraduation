package com.university.graduation.dao;


import com.university.graduation.domain.bo.AuthAccountLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import java.util.List;

/* *
 * @Author tomsun28
 * @Description 
 * @Date 8:27 2018/4/22
 */
@Mapper
public interface AuthAccountLogMapper {

    List<AuthAccountLog> selectAccountLogList();

    int insertSelective(AuthAccountLog authAccountLog) throws DataAccessException;

}
