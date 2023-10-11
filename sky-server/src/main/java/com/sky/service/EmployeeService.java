package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工状态修改
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getEmployeeById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void setEmployee(EmployeeDTO employeeDTO);

    /**
     * 退出登录
     */
    void logout();
}
