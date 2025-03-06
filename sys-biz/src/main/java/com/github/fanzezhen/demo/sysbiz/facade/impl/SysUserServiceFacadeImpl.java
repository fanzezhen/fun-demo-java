package com.github.fanzezhen.demo.sysbiz.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.fanzezhen.demo.sysbiz.facade.SysUserServiceFacade;
import com.github.fanzezhen.demo.sysbiz.foundation.entity.SysUser;
import com.github.fanzezhen.demo.sysbiz.foundation.entity.SysUserRole;
import com.github.fanzezhen.demo.sysbiz.foundation.service.ISysUserRoleService;
import com.github.fanzezhen.demo.sysbiz.foundation.service.ISysUserService;
import com.github.fanzezhen.demo.sysbiz.model.dto.SysUserDto;
import com.github.fanzezhen.demo.sysbiz.model.vo.SysUserVo;
import com.github.fanzezhen.fun.framework.mp.model.dto.PageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
@Slf4j
@Service
public class SysUserServiceFacadeImpl implements SysUserServiceFacade {
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ISysUserRoleService sysUserRoleService;

    @Override
    public IPage<SysUserVo> page(PageDto<SysUserDto, SysUser> pageDto) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>(pageDto.getParam());
        if (!StringUtils.isEmpty(pageDto.getParam().getUsername())) {
            queryWrapper.like(SysUser::getUsername, pageDto.getParam().getUsername());
        }
        pageDto.getParam().setUsername(null);
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        return sysUserService.page(pageDto, queryWrapper).convert(this::toVo);
    }

    @Override
    public SysUserVo getById(String id) {
        return toVo(sysUserService.getById(id));
    }

    @Override
    public SysUserVo getByUserName(String username) {
        return toVo(sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username)));
    }

    @Override
    public SysUserVo getByUserName(String username, String appCode) {
        return toVo(sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public SysUserVo save(SysUserDto sysUserDto) {
        if (sysUserService.saveOrUpdate(sysUserDto) && !org.springframework.util.StringUtils.isEmpty(sysUserDto.getId()) &&
                sysUserDto.getRoleIds() != null && sysUserDto.getRoleIds().size() > 0) {
            sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, sysUserDto.getId()));
            List<SysUserRole> sysUserRoleList = new ArrayList<>();
            for (String roleId : sysUserDto.getRoleIds()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(sysUserDto.getId());
                sysUserRole.setRoleId(roleId);
                sysUserRoleList.add(sysUserRole);
            }
            sysUserRoleService.saveBatch(sysUserRoleList);
        }
        return toVo(sysUserDto);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<SysUserVo> saveBatch(List<SysUserDto> sysUserDtoList) {
        List<SysUser> entityList = sysUserDtoList.stream().map(sysUserDto ->
                BeanUtil.copyProperties(sysUserDto,  SysUser.class)).collect(Collectors.toList());
        if (!sysUserService.saveOrUpdateBatch(entityList)) {
            ExceptionUtil.throwException("批量保存用户失败！");
        }
        List<SysUserRole> sysUserRoleList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            SysUser sysUser = entityList.get(i);
            SysUserDto sysUserDto = sysUserDtoList.get(i);
            BeanUtil.copyProperties(sysUser, sysUserDto);
            if (StrUtil.isBlank(sysUserDto.getId())) {
                ExceptionUtil.throwException("批量保存用户失败，未能生成主键：" + sysUserDto.getId() + sysUserDto.getUsername());
            }
            for (String roleId : sysUserDto.getRoleIds()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(sysUserDto.getId());
                sysUserRole.setRoleId(roleId);
                sysUserRoleList.add(sysUserRole);
            }
        }
        if (!sysUserRoleService.saveBatch(sysUserRoleList)) {
            ExceptionUtil.throwException("批量保存用户权限失败！");
        }
        return BeanUtil.copyToList(sysUserDtoList, SysUserVo.class, CopyOptions.create());
    }

    private SysUserVo toVo(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }
        return BeanUtil.copyProperties(sysUser,  SysUserVo.class);
    }
}
