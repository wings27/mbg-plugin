package com.github.wings27.mbgplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * Created by wenqiushi on 2017-07-03.
 *
 * @author wenqiushi
 */
public class MbgPluginMt extends org.mybatis.generator.api.PluginAdapter {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MbgPluginMt.class);

    private boolean generateDbComment;

    private String interfaceNameReplaceFrom;

    private String interfaceNameReplaceTo;

    private String mapperFileNameReplaceFrom;

    private String mapperFileNameReplaceTo;

    private String interfaceAnnotation = "@MyBatisRepository";

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        String propGenerateDbComment = properties.getProperty("generateDbComment", Boolean.TRUE.toString());
        this.generateDbComment = isTrue(propGenerateDbComment);

        this.interfaceNameReplaceFrom = properties.getProperty("interfaceNameReplaceFrom", "");
        this.interfaceNameReplaceTo = properties.getProperty("interfaceNameReplaceTo", "");
        this.mapperFileNameReplaceFrom = properties.getProperty("mapperFileNameReplaceFrom", "");
        this.mapperFileNameReplaceTo = properties.getProperty("mapperFileNameReplaceTo", "");
        this.interfaceAnnotation = properties.getProperty("interfaceAnnotation", null);
    }

    public boolean validate(java.util.List<java.lang.String> list) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        Pattern pattern = Pattern.compile(interfaceNameReplaceFrom);
        Matcher matcher = pattern.matcher(myBatis3JavaMapperType);
        String newMyBatis3JavaMapperType = matcher.replaceAll(this.interfaceNameReplaceTo);

        LOGGER.info("Replacing myBatis3JavaMapperType from [{}] to [{}]",
                myBatis3JavaMapperType, newMyBatis3JavaMapperType);
        introspectedTable.setMyBatis3JavaMapperType(newMyBatis3JavaMapperType);

        String myBatis3XmlMapperFileName = introspectedTable.getMyBatis3XmlMapperFileName();
        pattern = Pattern.compile(mapperFileNameReplaceFrom);
        matcher = pattern.matcher(myBatis3XmlMapperFileName);
        String newMyBatis3XmlMapperFileName = matcher.replaceAll(this.mapperFileNameReplaceTo);

        LOGGER.info("Replacing myBatis3XmlMapperFileName from [{}] to [{}]",
                myBatis3XmlMapperFileName, newMyBatis3XmlMapperFileName);
        introspectedTable.setMyBatis3XmlMapperFileName(newMyBatis3XmlMapperFileName);

        super.initialized(introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (generateDbComment) {
            field.addJavaDocLine(String.format("/** %s */", introspectedColumn.getRemarks()));
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (interfaze != null && this.interfaceAnnotation != null) {
            interfaze.addAnnotation(interfaceAnnotation);
        }

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

}
