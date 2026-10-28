package com.novel.controller;

import com.novel.domain.po.SysUser;
import com.novel.result.Result;
import com.novel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class userController {
    private final UserService userService;

    @PostMapping("me/avatar")
    public Result<SysUser> avatar(@RequestBody MultipartFile file) {
        return userService.avatar(file);
    }
}
