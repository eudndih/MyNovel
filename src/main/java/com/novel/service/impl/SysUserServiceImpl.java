package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.domain.dto.SysUserDTO;
import com.novel.domain.po.SysUser;
import com.novel.domain.vo.codeVO;
import com.novel.domain.vo.LoginVO;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.result.Result;
import com.novel.service.SysUserService;
import com.novel.mapper.SysUserMapper;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.novel.utils.JWTUtils.createToken;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{
    private final Map<String,String> map = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String,Long> expires = new java.util.concurrent.ConcurrentHashMap<>();
    private final SysUserMapper sysUserMapper;
    @Override
    public synchronized Result<codeVO> sendCode(String phone) {
        checkPhone(phone);
        long now = System.currentTimeMillis();
        expires.entrySet().removeIf(entry -> {
            if (entry.getValue() <= now) { map.remove(entry.getKey()); return true; }
            return false;
        });
        if (expires.getOrDefault(phone, 0L) > now + 240_000)
            throw new BusinessException(429, "请60秒后再获取验证码");
        Random random = new java.security.SecureRandom();
        int code = random.nextInt(100000, 999999);
        map.put(phone, Integer.toString(code));
        expires.put(phone, now + 300_000);
        return Result.success(new codeVO(Integer.toString(code),300));
    }

    @Override
    public synchronized Result<Void> register(SysUserDTO dto) {
        if (dto == null) throw new BusinessException(400, "参数不能为空");
        String phone = dto.getPhone();
        checkPhone(phone);
        if (dto.getUsername() == null || dto.getUsername().isBlank() || dto.getUsername().length() > 50)
            throw new BusinessException(400, "用户名须为1至50个字符");
        if (dto.getPassword() == null || dto.getPassword().length() < 6 || dto.getPassword().length() > 72)
            throw new BusinessException(400, "密码须为6至72个字符");
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone));
        if(sysUser!=null)
            throw new BusinessException("该手机号已经注册");
        String code = dto.getCode();
        if (expires.getOrDefault(phone, 0L) <= System.currentTimeMillis())
            throw new BusinessException(400, "验证码未获取或已过期");
        if(!java.util.Objects.equals(map.get(phone), code))
            throw new BusinessException("验证码错误");
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto,user);
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())) > 0)
            throw new BusinessException(409, "用户名已被使用");
        user.setPassword(hash(dto.getPassword()));
        user.setRole("READER"); user.setStatus(1);
        sysUserMapper.insert(user);
        map.remove(phone); expires.remove(phone);
        return Result.success();
    }

    @Override
    public Result<SysUser> me() {
        Long userId = UserContext.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || Integer.valueOf(0).equals(sysUser.getStatus())) throw new BusinessException(401, "用户不存在或已禁用");
        return Result.success(sysUser);
    }

    @Override
    public Result<LoginVO> login(SysUserDTO dto) {
        if (dto == null || dto.getPassword() == null) throw new BusinessException(400, "请输入手机号和密码");
        checkPhone(dto.getPhone());
        String phone = dto.getPhone();
        String password = dto.getPassword();
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
        if(user==null)
            throw new BusinessException("用户不存在,请先注册");
        if (Integer.valueOf(0).equals(user.getStatus())) throw new BusinessException(403, "账号已禁用");
        String saved = user.getPassword();
        boolean encrypted = saved != null && saved.startsWith("pbkdf2$");
        if (!(encrypted ? matches(password, saved) : java.util.Objects.equals(saved, password))) {
            throw new BusinessException("密码错误,请重新输入");
        }
        // 已有明文账号首次成功登录后升级，保持课程测试账号可登录。
        if (!encrypted) { user.setPassword(hash(password)); sysUserMapper.updateById(user); }
        String token = createToken(user.getId().toString());
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo(
                user.getId(),
                user.getPhone(),
                user.getUsername(),
                user.getRole()
        );
        return Result.success(new LoginVO(token, userInfo));
    }

    private void checkPhone(String phone) {
        if (phone == null || !phone.matches("1[3-9]\\d{9}")) throw new BusinessException(400, "手机号格式不正确");
    }

    private String hash(String password) {
        byte[] salt = new byte[16]; new java.security.SecureRandom().nextBytes(salt);
        return "pbkdf2$" + java.util.Base64.getEncoder().encodeToString(salt) + "$"
                + java.util.Base64.getEncoder().encodeToString(derive(password, salt));
    }

    private byte[] derive(String password, byte[] salt) {
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 120000, 256);
        try { return javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (java.security.GeneralSecurityException e) { throw new IllegalStateException("密码加密失败", e); }
        finally { spec.clearPassword(); }
    }

    private boolean matches(String password, String encoded) {
        try {
            String[] parts = encoded.split("\\$");
            return parts.length == 3 && java.security.MessageDigest.isEqual(
                    derive(password, java.util.Base64.getDecoder().decode(parts[1])), java.util.Base64.getDecoder().decode(parts[2]));
        } catch (IllegalArgumentException e) { return false; }
    }
}




