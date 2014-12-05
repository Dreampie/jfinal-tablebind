jfinal-tablebind
============

jfinal  tablebind plugin，查看其他插件-> [Maven](http://search.maven.org/#search%7Cga%7C1%7Ccn.dreampie)

maven 引用  ${jfinal-tablebind.version}替换为相应的版本如:0.1

```xml
<dependency>
<groupId>cn.dreampie</groupId>
<artifactId>jfinal-tablebind</artifactId>
<version>${jfinal-tablebind.version}</version>
</dependency>
```

使用非常简单的，自动注册所有Model的Table绑定工具

```java

/**
 * 配置扫描model
 */
//Model自动绑定表插件
TableBindPlugin tableBindDefault = new TableBindPlugin(druidDefault, SimpleNameStyles.LOWER);
tableBindDefault.setContainerFactory(new CaseInsensitiveContainerFactory(true)); //忽略字段大小写
排除或者引入包
//tableBindDefault.addExcludePaths("cn.dreampie.function.shop");
//tableBindDefault.addIncludePaths("cn.dreampie.function.default");
tableBindDefault.setShowSql(getPropertyToBoolean("devMode", false));
//非mysql的数据库方言
//tableBindDefault.setDialect(new AnsiSqlDialect());
plugins.add(tableBindDefault);


//Model里必须加上 表名注解
@TableBind(tableName = "com_area")
```

