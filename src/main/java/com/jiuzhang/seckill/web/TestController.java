package com.jiuzhang.seckill.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("hello")
    public String hello() {
        String result;
        try (Entry entry = SphU.entry("HelloResource")){
            // 被保护的业务逻辑
            result = "Hello Sentinel";
            return result;
        } catch (BlockException ex) {
            // 资源访问阻止，被限流或被降级
            // 此处进行相应的处理操作
            log.error(ex.toString());
            result = "系统繁忙稍后再试";
            return result;
        }
    }

//    /**
//     * 定义限流规则
//     * 1.创建存放限流规则的集合
//     * 2.创建限流规则
//     * 3.将限流规则放入集合
//     * 4.加载限流规则
//     */
//    @PostConstruct
//    public void initFlowRules() {
//        // 1. 创建存放限流规则的集合
//        List<FlowRule> rules = new ArrayList<>();
//        // 2. 创建限流规则
//        FlowRule rule = new FlowRule();
//        // 定义资源，表示sentinel会对那个资源生效
//        rule.setResource("HelloResource");
//        // 定义限流规则，QPS类型
//        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//        // 定义QPS每秒通过的请求数
//        rule.setCount(2);
//        // 3. 将限流规则放入集合中
//        rules.add(rule);
//        // 4. 加载限流规则
//        FlowRuleManager.loadRules(rules);
//    }

}
