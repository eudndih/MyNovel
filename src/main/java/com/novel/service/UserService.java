package com.novel.service;

import com.novel.domain.po.SysUser;
import com.novel.result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Result<SysUser> avatar(MultipartFile file);
}
