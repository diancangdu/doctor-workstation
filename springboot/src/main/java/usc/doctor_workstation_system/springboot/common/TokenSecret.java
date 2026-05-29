package usc.doctor_workstation_system.springboot.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 每次类加载时生成一个随机密钥，用于 JWT 签名。
 * 使用静态字段内联初始化，避免 CGLIB 代理导致重复调用构造器。
 * 服务重启后密钥失效，所有旧 token 自动作废，强制重新登录。
 */
@Component
public class TokenSecret {

    private static final String SECRET = UUID.randomUUID().toString();

    public static String get() {
        return SECRET;
    }
}
