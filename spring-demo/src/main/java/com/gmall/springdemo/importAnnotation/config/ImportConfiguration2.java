package com.gmall.springdemo.importAnnotation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-13 23:00:30
 * @description
 */
@Component
@Import(value = {ImportConfiguration2.class})
public class ImportConfiguration2 implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        String className = "com.gmall.springdemo.importAnnotation.ImportDemo2";
        return new String[]{className};
    }
}
