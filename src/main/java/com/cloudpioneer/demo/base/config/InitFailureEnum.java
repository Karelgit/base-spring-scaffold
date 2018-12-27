package com.cloudpioneer.demo.base.config;

import com.cloudpioneer.demo.base.entity.EnumUtil;
import com.cloudpioneer.demo.base.entity.FailureResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局失败结果枚举初始化类
 * @author jiangyunjun
 */
@Component
public class InitFailureEnum implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CommandLineRunner.class);

    @Override
    public void run(String... args) throws Exception {
        initEnum();
    }

    private static void initEnum() throws Exception{

        LOG.info("############ 枚举加载开始 ############");
        Resource resource = new ClassPathResource("FailureResult.properties");
        InputStream is = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String tmp;
        Map<String,Object[]> enumValues = new HashMap<>(20);
        while ((tmp = reader.readLine()) != null) {
            if(!StringUtils.isEmpty(tmp)&&tmp.trim().indexOf("#")!=0) {
                String[] nameAndCodeMsg = tmp.split("=");
                String name = nameAndCodeMsg[0].trim();
                String cav = nameAndCodeMsg[1].trim();
                String code = cav.substring(0, cav.indexOf(","));
                String msg = cav.substring(cav.indexOf(",") + 1);
                Object[] value = new Object[2];
                value[0] = Integer.parseInt(code.trim());
                value[1] = msg.trim();
                enumValues.put(name, value);
            }
        }
        EnumUtil.addEnum(FailureResult.class,new Class[]{int.class,String.class}, enumValues);
        LOG.info("############ 枚举加载完毕 ############");
    }
}
