package com.ruoyi.common.opc.ua;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.opc.OpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

@Slf4j
@Component
public class ClientGen {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
    public static OpcUaClient opcUaClient;

    private String endpointUrl;
    private String username;
    private String password;

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener(ApplicationReadyEvent.class)
    public void createClient() {
        try {
            endpointUrl = StrUtil.format("opc.tcp://{}:{}", OpcProperties.ip,OpcProperties.port);
            Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
            Files.createDirectories(securityTempDir);
            if (!Files.exists(securityTempDir)) {
                throw new Exception("没有创建安全目录: " + securityTempDir);
            }
            log.info("安全目录: {}", securityTempDir.toAbsolutePath());

            //加载秘钥
            KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);

            //安全策略 None、Basic256、Basic128Rsa15、Basic256Sha256
            SecurityPolicy securityPolicy = SecurityPolicy.None; //安全策略选择
            List<EndpointDescription> endpoints;
            try {
                endpoints = DiscoveryClient.getEndpoints(endpointUrl).get();
            } catch (Throwable ex) {
                // 发现服务
                String discoveryUrl =endpointUrl;
                if (!discoveryUrl.endsWith("/")) {
                    discoveryUrl += "/";
                }
                discoveryUrl += "discovery";

                log.info("开始连接 URL: {}", discoveryUrl);
                endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
            }
            EndpointDescription endpoint = endpoints.stream()
                    .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
                    .findFirst()
                    .orElseThrow(() -> new Exception("没有连接上端点"));

            log.info("使用端点: {} [{}/{}]", endpoint.getEndpointUrl(), securityPolicy, endpoint.getSecurityMode());
            OpcUaClientConfig config = OpcUaClientConfig.builder()
                    .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                    .setApplicationUri("urn:eclipse:milo:examples:client")
                    .setCertificate(loader.getClientCertificate())
                    .setKeyPair(loader.getClientKeyPair())
                    .setEndpoint(endpoint)
                    //根据匿名验证和第三个用户名验证方式设置传入对象 AnonymousProvider（匿名方式）UsernameProvider（账户密码）
                    //new UsernameProvider("admin","123456")
                    .setIdentityProvider(new AnonymousProvider())
                    .setRequestTimeout(uint(5000))
                    .build();
            opcUaClient = OpcUaClient.create(config);

            opcUaClient.connect().get();
            log.info("OPC UA 客户端注册完成: {}", endpointUrl);
            future.complete(opcUaClient);

        } catch (Exception e) {
            log.error("创建客户端失败", e);
            // 连接失败也要结束 Future，否则下游订阅会永久等待
            future.completeExceptionally(e);
        }
    }

    /**
     * 客户端注册完成信号：createClient() 中连接成功后 complete。
     * 下游订阅逻辑基于此 Future 触发，保证订阅一定发生在连接就绪之后。
     */
    public CompletableFuture<OpcUaClient> whenReady() {
        return future;
    }

}
