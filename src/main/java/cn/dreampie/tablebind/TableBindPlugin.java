package cn.dreampie.tablebind;

import cn.dreampie.ClassSearchKit;
import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by wangrenhui on 14-4-11.
 */
public class TableBindPlugin extends ActiveRecordPlugin {

  protected final Logger log = Logger.getLogger(getClass());

  @SuppressWarnings("rawtypes")
  private List<Class<? extends Model>> excludeClasses = Lists.newArrayList();
  private List<Class<? extends Model>> includeClasses = Lists.newArrayList();
  private List<String> includeClassPaths = Lists.newArrayList();
  private List<String> excludeClassPaths = Lists.newArrayList();
  //  private List<String> includeJars = Lists.newArrayList();
  private boolean autoScan = false;
  //  private boolean includeAllJarsInLib;
  private INameStyle nameStyle;

  public TableBindPlugin(DataSource dataSource) {
    this(dataSource, SimpleNameStyles.DEFAULT);
  }

  public TableBindPlugin(DataSource dataSource, INameStyle nameStyle) {
    super(dataSource);
    this.nameStyle = nameStyle;
  }

  public TableBindPlugin(IDataSourceProvider dataSourceProvider) {
    this(dataSourceProvider, SimpleNameStyles.DEFAULT);
  }

  public TableBindPlugin(IDataSourceProvider dataSourceProvider, INameStyle nameStyle) {
    super(dataSourceProvider);
    this.nameStyle = nameStyle;
  }

  public TableBindPlugin(String dataSourceName, IDataSourceProvider dataSourceProvider, INameStyle nameStyle) {
    super(dataSourceName, dataSourceProvider);
    this.nameStyle = nameStyle;
  }


  @SuppressWarnings({"rawtypes", "unchecked"})
  public TableBindPlugin addExcludeClasses(Class<? extends Model>... clazzes) {
    for (Class<? extends Model> clazz : clazzes) {
      excludeClasses.add(clazz);
    }
    return this;
  }

  @SuppressWarnings("rawtypes")
  public TableBindPlugin addExcludeClasses(List<Class<? extends Model>> clazzes) {
    if (clazzes != null) {
      excludeClasses.addAll(clazzes);
    }
    return this;
  }

  public TableBindPlugin addExcludePaths(String... paths) {
    for (String path : paths) {
      excludeClassPaths.add(path);
    }
    return this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public TableBindPlugin addIncludeClasses(Class<? extends Model>... clazzes) {
    for (Class<? extends Model> clazz : clazzes) {
      includeClasses.add(clazz);
    }
    return this;
  }

  @SuppressWarnings("rawtypes")
  public TableBindPlugin addIncludeClasses(List<Class<? extends Model>> clazzes) {
    if (clazzes != null) {
      includeClasses.addAll(clazzes);
    }
    return this;
  }

  public TableBindPlugin addIncludePaths(String... paths) {
    for (String path : paths) {
      includeClassPaths.add(path);
    }
    return this;
  }

//  public AutoMultiSourceTableBindPlugin addJars(List<String> jars) {
//    if (jars != null) {
//      includeJars.addAll(jars);
//    }
//    return this;
//  }
//
//  public AutoMultiSourceTableBindPlugin addJars(String... jars) {
//    if (jars != null) {
//      for (String jar : jars) {
//        includeJars.add(jar);
//      }
//    }
//    return this;
//  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public boolean start() {
    if (includeClasses.size() <= 0) {
//      includeClasses = ClassSearcherExt.of(Model.class).includepaths(includeClassPaths).injars(includeJars).includeAllJarsInLib(includeAllJarsInLib).search();
      includeClasses = ClassSearchKit.of(Model.class).includepaths(includeClassPaths).search();
    }
    TableBind tb = null;
    for (Class modelClass : includeClasses) {
      boolean isexclude = false;
      if (excludeClassPaths.size() > 0) {
        for (String excludepath : excludeClassPaths) {
          if (modelClass.getName().startsWith(excludepath)) {
            log.debug("exclude model:" + modelClass.getName());
            isexclude = true;
            break;
          }
        }
      }
      if (isexclude || excludeClasses.contains(modelClass)) {
        continue;
      }
      tb = (TableBind) modelClass.getAnnotation(TableBind.class);
      String tableName;
      if (tb == null) {
        if (!autoScan) {
          continue;
        }
        tableName = nameStyle.name(modelClass.getSimpleName());
        this.addMapping(tableName, modelClass);
        log.debug("addMapping(" + tableName + ", " + modelClass.getName() + ")");
      } else {
        tableName = tb.tableName();
        if (StrKit.notBlank(tb.pkName())) {
          this.addMapping(tableName, tb.pkName(), modelClass);
          log.debug("addMapping(" + tableName + ", " + tb.pkName() + "," + modelClass.getName() + ")");
        } else {
          this.addMapping(tableName, modelClass);
          log.debug("addMapping(" + tableName + ", " + modelClass.getName() + ")");
        }
      }
    }
    return super.start();
  }

  @Override
  public boolean stop() {
    return super.stop();
  }

  public TableBindPlugin autoScan(boolean autoScan) {
    this.autoScan = autoScan;
    return this;
  }

//  public AutoMultiSourceTableBindPlugin includeAllJarsInLib(boolean includeAllJarsInLib) {
//    this.includeAllJarsInLib = includeAllJarsInLib;
//    return this;
//  }
}
