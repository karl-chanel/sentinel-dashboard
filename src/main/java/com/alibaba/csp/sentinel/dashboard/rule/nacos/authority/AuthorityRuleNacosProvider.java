//package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;
//
//import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
//import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
//import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
//import com.alibaba.csp.sentinel.datasource.Converter;
//import com.alibaba.csp.sentinel.util.StringUtil;
//import com.alibaba.nacos.api.config.ConfigService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author 星空流年
// */
//@Component("authorityRuleNacosProvider")
//public class AuthorityRuleNacosProvider implements DynamicRuleProvider<List<AuthorityRuleEntity>> {
//
//    @Autowired
//    private ConfigService configService;
//
//    @Autowired
//    private Converter<String, List<AuthorityRuleEntity>> converter;
//
//    @Override
//    public List<AuthorityRuleEntity> getRules(String appName) throws Exception {
//        String rules = configService.getConfig(appName + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
//                NacosConfigUtil.GROUP_ID, 3000);
//        if (StringUtil.isEmpty(rules)) {
//            return new ArrayList<>();
//        }
//        return converter.convert(rules);
//    }
//}

package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chengmeng
 * @description
 * @date 2021/2/4 14:08
 */
@Component("authorityRuleNacosProvider")
public class AuthorityRuleNacosProvider implements DynamicRuleProvider<java.util.List<AuthorityRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private NacosConfig nacosConfig;
    @Autowired
    private Converter<String, java.util.List<AuthorityRuleCorrectEntity>> converter;
    @Override
    public java.util.List<AuthorityRuleEntity> getRules(String appName) throws Exception {
//        final String group = StringUtils.isNotEmpty(nacosConfig.getGroup()) ?
//                nacosConfig.getGroup() :
//                NacosConfigUtil.GROUP_ID;
        String rules = configService.getConfig(appName + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        java.util.List<AuthorityRuleCorrectEntity> entityList = converter.convert(rules);
        entityList.forEach(e -> e.setApp(appName));
        return entityList.stream().map(rule -> {
            AuthorityRule authorityRule = new AuthorityRule();
            BeanUtils.copyProperties(rule, authorityRule);
            AuthorityRuleEntity entity = AuthorityRuleEntity.fromAuthorityRule(rule.getApp(), rule.getIp(), rule.getPort(), authorityRule);
            entity.setId(rule.getId());
            entity.setGmtCreate(rule.getGmtCreate());
            return entity;
        }).collect(Collectors.toList());
    }

}