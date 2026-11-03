package com.ruoyi.common.opc.ua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.UserTokenType;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.UserTokenPolicy;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ClientTest {


    /***
     * 匿名连接测试
     */
    public void OpcUaAnonymous(){
        try {
            // OPC UA 服务器地址，例如 "opc.tcp://localhost:49320"
            String url = "opc.tcp://127.0.0.1:49320";

            // 创建 client
            OpcUaClient client = OpcUaClient.create(url,
                    endpoints ->
                            endpoints.stream()
                                    .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                    .findFirst(),
                    configBuilder ->
                            configBuilder
                                    //访问方式
                                    .setIdentityProvider(new AnonymousProvider())
                                    .setRequestTimeout(UInteger.valueOf(5000))
                                    .build());


            // 连接到服务器
            client.connect().get();

            System.out.println("匿名连接成功！");

            // 在此处可以进行读写操作，例如读取节点的值
            // client.readValue(0, TimestampsToReturn.Both, new ReadValueId(NodeId, ...));
            // ...

            // 最后断开连接
            client.disconnect().get();
            System.out.println("客户端断开连接。");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 简单选择一个安全策略为 None 的端点（匿名方式一般使用安全策略None，具体看服务器配置）
    private static EndpointDescription chooseSecureEndpoint(List<EndpointDescription> endpoints) {
        EndpointDescription result = null;
        for (EndpointDescription e : endpoints) {
            if (e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri())) {
                result = e;
                break;
            }
        }
        return result;
    }

    /***
     * 账户密码登录连接测试
     */
    public void OpcUaLogin(String url,String user,String password){
        try {
            String endpointUrl = url;
//            String endpointUrl = "opc.tcp://127.0.0.1:4840";

            List<EndpointDescription> endpoints = DiscoveryClient
                    .getEndpoints(endpointUrl).get();

            EndpointDescription endpoint = chooseUserNameEndpoint(endpoints);

            OpcUaClientConfigBuilder configBuilder = new OpcUaClientConfigBuilder();
            configBuilder.setEndpoint(endpoint);

            // 假设用户名为 "user", 密码为 "password"
            configBuilder.setIdentityProvider(new UsernameProvider(user, password));

            OpcUaClient client = OpcUaClient.create(configBuilder.build());

            client.connect().get();

            System.out.println("用户名密码方式连接成功！");

            // 进行后续读写操作
            // ...

            client.disconnect().get();
            System.out.println("客户端断开连接。");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static EndpointDescription chooseUserNameEndpoint(List<EndpointDescription> endpoints) {
        // 通常 OPC UA 服务器也支持 SecurityPolicy.None + UserName 方式
        // 也可能是 Basic128Rsa15, Basic256, etc. 具体看服务端配置
        for (EndpointDescription e : endpoints) {
            if (e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri())) {
                // 确保端点支持 UserName 类型的认证
                for (UserTokenPolicy tokenPolicy : e.getUserIdentityTokens()) {
                    if (tokenPolicy.getTokenType() == UserTokenType.UserName) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    //证书连接测试
    public static OpcUaClient initClient(String url,SecurityPolicy securityPolicy) {
        try {
            if (securityPolicy.equals(SecurityPolicy.None)){
                return OpcUaClient.create(url,
                        endpoints ->
                                endpoints.stream()
                                        .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
                                        .findFirst(),
                        configBuilder ->
                                configBuilder
                                        //访问方式
                                        .setIdentityProvider(new AnonymousProvider())
                                        .setRequestTimeout(UInteger.valueOf(5000))
                                        .build());
            }
            Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
            Files.createDirectories(securityTempDir);
            if (!Files.exists(securityTempDir)) {
                throw new Exception("unable to create security dir: " + securityTempDir);
            }
            KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);
            File pkiDir = securityTempDir.resolve("pki").toFile();

            DefaultTrustListManager trustListManager = new DefaultTrustListManager(pkiDir);

            DefaultClientCertificateValidator certificateValidator =
                    new DefaultClientCertificateValidator(trustListManager);
            String hostName = InetAddress.getLocalHost().getHostName();
            return OpcUaClient.create(url,
                    endpoints ->
                            endpoints.stream()
                                    .map(endpoint -> {
                                        // 构建一个新的 EndpointDescription（可选修改某些字段）
                                        return new EndpointDescription(
                                                url,
                                                endpoint.getServer(),
                                                endpoint.getServerCertificate(),
                                                endpoint.getSecurityMode(), // 或者强制改为某种模式
                                                endpoint.getSecurityPolicyUri(),
                                                endpoint.getUserIdentityTokens(),
                                                endpoint.getTransportProfileUri(),
                                                endpoint.getSecurityLevel()
                                        );
                                    })
                                    .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
                                    .findFirst(),
                    configBuilder ->
                            configBuilder
                                    //访问方式
                                    .setApplicationName(LocalizedText.english("datacollector-driver"))
                                    .setApplicationUri(String.format("urn:%s:opcua-client", hostName))  // 必须与证书中的URI一致
                                    .setKeyPair(loader.getClientKeyPair())
                                    .setCertificate(loader.getClientCertificate())
                                    .setCertificateChain(loader.getClientCertificateChain())
                                    .setCertificateValidator(certificateValidator)
                                    .setIdentityProvider(new UsernameProvider("admin", "123456"))
                                    .setRequestTimeout(UInteger.valueOf(5000))
                                    .build());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
