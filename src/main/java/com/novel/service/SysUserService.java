package com.novel.service;

import com.novel.domain.dto.SysUserDTO;
import com.novel.domain.po.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.domain.vo.codeVO;
import com.novel.domain.vo.LoginVO;
import com.novel.result.Result;

/**
* @author 钱力川
* @description 针对表【sys_user(系统用户表)】的数据库操作Service
* @createDate 2026-10-12 08:44:12
*/
public interface SysUserService extends IService<SysUser> {

    Result<codeVO> sendCode(String phone);

    Result<Void> register(SysUserDTO dto);

    Result<SysUser> me();

    Result<LoginVO> login(SysUserDTO dto);
}
