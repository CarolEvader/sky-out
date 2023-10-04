package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import com.sky.entity.Setmeal;

import java.util.List;

public interface SetmealService {

    void save(SetmealDTO setmealDTO);

    SetmealVO getById(Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void update(SetmealDTO setmealDTO);

    void deleteBydIds(List<Long> ids);

    void setStatus(Long id, Integer status);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);

}
