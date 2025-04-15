package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page =setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void save(SetmealDTO setmealDTO) {
        log.info("save setmeal{}",setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.save(setmeal);
        List<SetmealDish> list = setmealDTO.getSetmealDishes();
        list.forEach(setmealDish -> {setmealDish.setSetmealId(setmeal.getId());});
        setmealDishMapper.insert(list);
    }

    @Override
    public SetmealVO getById(Long id) {
        log.info("get setmeal{}",id);
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDish = setmealDishMapper.getSetmealDishId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDish);
//
        return setmealVO;
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishMapper.delete(setmealId);
        if(setmealDishes.size()>0  && setmealDishes != null){
            setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});
            setmealDishMapper.insert(setmealDishes);
        }
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
    for (Long id : ids) {
       Setmeal setmeal = setmealDishMapper.getById(id);
       if(setmeal.getStatus()==StatusConstant.ENABLE){
           throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
       }
    }
    setmealMapper.deleteByIds(ids);
    setmealDishMapper.deleteByIds(ids);

    }
}
