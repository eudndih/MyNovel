package com.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUserDTO {
    private String phone;
    private String username;
    private String code;
    private String password;
}
