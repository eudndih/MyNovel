package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.domain.po.SysUser;
import com.novel.mapper.SysUserMapper;
import com.novel.result.Result;
import com.novel.service.UserService;
import com.novel.utils.MinIoUtils;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MinIoUtils  minIoUtils;
    private final SysUserMapper  sysUserMapper;
    @Override
    public Result<SysUser> avatar(MultipartFile file) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, UserContext.getUserId()));
        try {
            String s = minIoUtils.uploadAvatar(file);
            user.setAvatarUrl("http://192.168.150.161:9000/novel-bucket/"+s);
            sysUserMapper.updateById(user);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
        return Result.success(user);
    }
}
