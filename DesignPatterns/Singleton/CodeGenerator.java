
package com.taursys.tools;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.IOException;
import java.util.Properties;

public class CodeGenerator {
  private static CodeGenerator singleton;
  private boolean initialized = false;
  private VelocityEngine velocity = null;
  private Properties properties = new Properties();
  public static final String TEMPLATES_PATH = "templatesPath";


  private CodeGenerator() {
    properties.setProperty(TEMPLATES_PATH, "./");
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public static CodeGenerator getInstance() {
    if (singleton == null) {
      // create a new instance
      singleton = new CodeGenerator();
    }
    return singleton;
  }

  public void initialize() throws Exception {
    if (initialized)
      return;
    velocity = new VelocityEngine();
    velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
        "com.taursys.debug.VelocitySimpleLogger");
    velocity.setProperty(VelocityEngine.RESOURCE_LOADER, "class, file");
    velocity.setProperty("class.resource.loader.description",
        "Velocity Classpath Resource Loader");
    velocity.setProperty("class.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocity.setProperty("file.resource.loader.description",
        "Velocity File Resource Loader");
    velocity.setProperty("file.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    velocity.setProperty("file.resource.loader.path",
        properties.getProperty(TEMPLATES_PATH));
    velocity.setProperty("file.resource.loader.cache",
        "false");
    velocity.setProperty("file.resource.loader.modificationCheckInterval",
        "2");
    velocity.init();
  }

  public void generateCode(String templateName, Context context, String srcPath,
    String packageName, String className) throws Exception {
    Template t = velocity.getTemplate(templateName);
    FileWriter writer = new FileWriter(createFile(srcPath, packageName, className));
    t.merge(context, writer);
    writer.close();
  }

  private File createFile(String sourcePath, String packageName, String fileName)
      throws IOException {
    if (!sourcePath.endsWith("/"))
      sourcePath += "/";
    if (packageName != null && packageName.length() > 0)
      sourcePath = sourcePath + packageName.replace('.', '/');
    File file = new File(sourcePath);
    if (!file.exists()) {
      if (!file.mkdirs())
        throw new IOException("Creation of path failed: " + file.getAbsolutePath());
    }
    file = new File(sourcePath + "/" + fileName + ".java");
    file.createNewFile();
    return file;
  }
}
