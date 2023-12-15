//package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;
//
//import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
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
//@Component("paramFlowRuleNacosPublisher")
//public class ParamFlowRuleNacosPublisher implements DynamicRulePublisher<List<ParamFlowRuleEntity>> {
//
//    @Autowired
//    private ConfigService configService;
//
//    @Autowired
//    private Converter<List<ParamFlowRuleEntity>, String> converter;
//
//    @Override
//    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
//        AssertUtil.notEmpty(app, "app name cannot be empty");
//        if (rules == null) {
//            return;
//        }
//        configService.publishConfig(app + NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX,
//                NacosConfigUtil.GROUP_ID, converter.convert(rules), ConfigType.JSON.getType());
//    }
//}

package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
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
 * @date 2021/2/4 10:04
 */
@Component("paramFlowRuleNacosPublisher")
public class ParamFlowRuleNacosPublisher implements DynamicRulePublisher<java.util.List<ParamFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private NacosConfig nacosConfig;
    @Autowired
    private Converter<java.util.List<ParamFlowRuleCorrectEntity>, String> converter;

    @Override
    public void publish(String app, java.util.List<ParamFlowRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        for (ParamFlowRuleEntity rule : rules) {
            if (rule.getApp() == null) {
                rule.setApp(app);
            }
        }
        //  转换
        List<ParamFlowRuleCorrectEntity> list = rules.stream().map(rule -> {
            ParamFlowRuleCorrectEntity entity = new ParamFlowRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());

        //lbj增加格式化、美化json内容 - start
        String content = converter.convert(list);
        JSONArray jsonArray = JSONArray.parseArray(content);
        String prettyContent = JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        //lbj增加格式化、美化json内容 - end

//        final String group = StringUtils.isNotEmpty(nacosConfig.getGroup()) ?
//                nacosConfig.getGroup() :
//                NacosConfigUtil.GROUP_ID;
        configService.publishConfig(app + NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, prettyContent);
    }
}