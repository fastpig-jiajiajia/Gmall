package com.gmall.springdemo.importAnnotation.config;

import com.gmall.springdemo.importAnnotation.ImportDemo1;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-13 23:00:30
 * @description
 */
@Component
@Import(value = {ImportDemo1.class})
public class ImportConfiguration1 {
}
