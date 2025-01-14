package com.egzosn.pay.spring.boot.core.provider.merchant.platform;

import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform;
import com.egzosn.pay.spring.boot.core.merchant.bean.CommonPaymentPlatformMerchantDetails;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 支付宝支付平台
 *
 * @author egan
 *         <pre>
 *                 email egzosn@gmail.com
 *                 date  2019/4/4 14:35.
 *                 </pre>
 */
@Configuration(AliPaymentPlatform.platformName)
@ConditionalOnMissingBean(AliPaymentPlatform.class)
@ConditionalOnClass(name = {"com.egzosn.pay.ali.api.AliPayConfigStorage"})
public class AliPaymentPlatform implements PaymentPlatform {

    public static final String platformName = "aliPay";



    /**
     * 获取商户平台
     *
     * @return 商户平台
     */
    @Override
    public String getPlatform() {
        return platformName;
    }

    /**
     * 获取支付平台对应的支付服务
     *
     * @param payConfigStorage 支付配置
     * @return 支付服务
     */
    @Override
    public PayService getPayService(PayConfigStorage payConfigStorage) {
        if (payConfigStorage instanceof AliPayConfigStorage) {
            return new AliPayService((AliPayConfigStorage) payConfigStorage);
        }
        AliPayConfigStorage configStorage = new AliPayConfigStorage();
        configStorage.setInputCharset(payConfigStorage.getInputCharset());
        configStorage.setAppId(payConfigStorage.getAppId());
        configStorage.setPid(payConfigStorage.getPid());
        configStorage.setAttach(payConfigStorage.getAttach());
        configStorage.setSeller(payConfigStorage.getSeller());
        configStorage.setKeyPrivate(payConfigStorage.getKeyPrivate());
        configStorage.setKeyPublic(payConfigStorage.getKeyPublic());
        configStorage.setNotifyUrl(payConfigStorage.getNotifyUrl());
        configStorage.setReturnUrl(payConfigStorage.getReturnUrl());
        configStorage.setPayType(payConfigStorage.getPayType());
        configStorage.setTest(payConfigStorage.isTest());
        configStorage.setSignType(payConfigStorage.getSignType());
        if (payConfigStorage instanceof CommonPaymentPlatformMerchantDetails){
            CommonPaymentPlatformMerchantDetails platformMerchantDetails = (CommonPaymentPlatformMerchantDetails)payConfigStorage;
            configStorage.setAppAuthToken(platformMerchantDetails.getSubAppId());

            //证书方式加密
            configStorage.setCertSign(true);

            //设置证书存储方式，这里为路径
            configStorage.setCertStoreType(platformMerchantDetails.getCertStoreType()); //证书存储方式
            configStorage.setMerchantCert(platformMerchantDetails.getMerchantCert());   //应用公钥证书文件路径，例如：d:/appCertPublicKey_2019051064521003.crt
            configStorage.setAliPayCert(platformMerchantDetails.getKeyPublic());        //支付宝公钥证书文件路径，例如：d:/alipayCertPublicKey_RSA2.crt
            configStorage.setAliPayRootCert(platformMerchantDetails.getKeyCert());      //支付宝根证书文件路径，例如：d:/alipayRootCert.crt
        }

        return new AliPayService(configStorage);
    }

    /**
     * 获取支付平台对应的支付服务
     *
     * @param payConfigStorage  支付配置
     * @param httpConfigStorage 网络配置
     * @return 支付服务
     */
    @Override
    public PayService getPayService(PayConfigStorage payConfigStorage, HttpConfigStorage httpConfigStorage) {
        PayService payService = getPayService(payConfigStorage);
        payService.setRequestTemplateConfigStorage(httpConfigStorage);
        return payService;
    }

    @Override
    public TransactionType getTransactionType(String name) {
        return AliTransactionType.valueOf(name);
    }


}
