//package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;
//
//import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
//import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
//import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
//import com.alibaba.csp.sentinel.datasource.Converter;
//import com.alibaba.csp.sentinel.util.AssertUtil;
//import com.alibaba.nacos.api.config.ConfigService;
//import com.alibaba.nacos.api.config.ConfigType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @author 星空流年
// */
//@Component("authorityRuleNacosPublisher")
//public class AuthorityRuleNacosPublisher implements DynamicRulePublisher<List<AuthorityRuleEntity>> {
//
//    @Autowired
//    private ConfigService configService;
//
//    @Autowired
//    private Converter<List<AuthorityRuleEntity>, String> converter;
//
//    @Override
//    public void publish(String app, List<AuthorityRuleEntity> rules) throws Exception {
//        AssertUtil.notEmpty(app, "app name cannot be empty");
//        if (rules == null) {
//            return;
//        }
//        configService.publishConfig(app + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
//                NacosConfigUtil.GROUP_ID, converter.convert(rules), ConfigType.JSON.getType());
//    }
//}

package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.ConfigService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chengmeng
 * @description
 * @date 2021/2/4 14:08
 */
@Component("authorityRuleNacosPublisher")
public class AuthorityRuleNacosPublisher implements DynamicRulePublisher<java.util.List<AuthorityRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private NacosConfig nacosConfig;
    @Autowired
    private Converter<java.util.List<AuthorityRuleCorrectEntity>, String> converter;

    @Override
    public void publish(String app, java.util.List<AuthorityRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
//        final String group = StringUtils.isNotEmpty(nacosConfig.getGroup()) ?
//                nacosConfig.getGroup() :
//                NacosConfigUtil.GROUP_ID;
        if (rules == null) {
            return;
        }


        for (AuthorityRuleEntity rule : rules) {
            if (rule.getApp() == null) {
                rule.setApp(app);
            }
        }

        //  转换
        List<AuthorityRuleCorrectEntity> list = rules.stream().map(rule -> {
            AuthorityRuleCorrectEntity entity = new AuthorityRuleCorrectEntity();
            BeanUtils.copyProperties(rule,entity);
            return entity;
        }).collect(Collectors.toList());


        //lbj增加格式化、美化json内容 - start
        String content = converter.convert(list);
        JSONArray jsonArray = JSONArray.parseArray(content);
        String prettyContent = JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        //lbj增加格式化、美化json内容 - end

        configService.publishConfig(app + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, prettyContent);
    }
}