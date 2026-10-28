package com.novel.controller;

import com.novel.domain.dto.SysUserDTO;
import com.novel.domain.po.SysUser;
import com.novel.domain.vo.codeVO;
import com.novel.domain.vo.LoginVO;
import com.novel.result.Result;
import com.novel.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证接口")
public class authController {
    private final SysUserService sysUserService;
    @PostMapping("send-code")
    @Operation(summary = "生成验证码")
    public Result<codeVO> sendCode(@RequestBody String phone) {
        return sysUserService.sendCode(phone);
    }

    @PostMapping("register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@RequestBody SysUserDTO dto){
        return sysUserService.register(dto);
    }

    @GetMapping("me")
    @Operation(summary = "用户展示")
    public Result<SysUser> me(){
        return sysUserService.me();
    }

    @PostMapping("login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@RequestBody SysUserDTO dto){
        return sysUserService.login(dto);
    }
}
