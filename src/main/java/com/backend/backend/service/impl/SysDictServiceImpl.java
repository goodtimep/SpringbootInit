package com.backend.backend.service.impl;

import com.backend.backend.dao.SysDictMapper;
import com.backend.backend.enums.DelFlagEnum;
import com.backend.backend.model.entity.sys.SysDict;
import com.backend.backend.model.entity.sys.SysPermission;
import com.backend.backend.service.SysDictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/18 15:10
 * @description :
 */
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {
    final private SysDictMapper sysDictMapper;

    @Override
    public List<SysDict> getByParentId(Long parentId) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDict::getParentId, parentId).eq(SysDict::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .orderBy(true, true, SysDict::getOrderNumber);
        return sysDictMapper.selectList(queryWrapper);
    }

    @Override
    public SysDict getByDictId(Long id) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDict::getDictId, id).eq(SysDict::getDelFlag, DelFlagEnum.NORMAL.getCode());
        return sysDictMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveSysDict(SysDict sysDict) {
        sysDict.setCreate();
        save(sysDict);
    }

    @Override
    public void updateSysDict(SysDict sysDict) {
        sysDict.setUpdate();
        updateById(sysDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysDict(List<Long> ids) {
        List<SysDict> sysDictS = new ArrayList<>();
        for (Long item : ids) {
            SysDict temp = new SysDict();
            temp.delete();
            temp.setDictId(item);
            sysDictS.add(temp);
        }
        this.updateBatchById(sysDictS);
    }
}
