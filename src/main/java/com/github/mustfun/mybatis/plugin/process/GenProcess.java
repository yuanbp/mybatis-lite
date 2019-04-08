package org.mybatis.gen.process;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.mybatis.gen.utils.FileUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @description GenProcess
 * @author chieftain
 * @create 2019-02-01 17:36
 */
public class GenProcess {

    private Project project;

    public void process (AnActionEvent event) {
        this.project = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();
        if ("xml".equals(getFileExtension(dataContext)))
        {
            VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            if (file != null)
            {
                boolean overwrite = true;
                List<String> warnings = new ArrayList();
                try
                {
                    InputStream is = file.getInputStream();
                    ConfigurationParser cp = new ConfigurationParser(warnings);
                    Configuration config = cp.parseConfiguration(is);

                    boolean result = RecombinationConfiguration(config);
                    if (result)
                    {
                        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
                        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
                        myBatisGenerator.generate(null);
                        Messages.showMessageDialog(this.project, "success", "Tips", Messages.getInformationIcon());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Messages.showMessageDialog(this.project, "生成失败:" + e.getMessage(), "Tips", Messages.getInformationIcon());
                }
            }
        }
        else
        {
            Messages.showMessageDialog(this.project, "请选择正确的 Mybaits Generator 配置文件(.xml)格式", "Tips", Messages.getWarningIcon());
        }
    }

    private static String getFileExtension(DataContext dataContext)
    {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(dataContext);
        return file == null ? null : file.getExtension();
    }

    private String assemblyTargetProject(Project project, String originalTargetProject)
    {
        if (project != null)
        {
            if (FileUtil.isAbsolutePath(originalTargetProject)) {
                return originalTargetProject;
            }
            return project.getBasePath() + "/" + originalTargetProject;
        }
        return "";
    }

    private boolean RecombinationConfiguration(Configuration configuration)
    {
        for (Context config : configuration.getContexts())
        {
            JavaModelGeneratorConfiguration jModelConfig = config.getJavaModelGeneratorConfiguration();
            if (config.getJavaModelGeneratorConfiguration() != null)
            {
                String targetProjectPath = assemblyTargetProject(this.project, jModelConfig.getTargetProject());
                if (!FileUtil.exist(targetProjectPath))
                {
                    Messages.showMessageDialog(this.project, "context  '" + config.getId() + "' 的 targetProject 参数:'" + targetProjectPath + "'路径不存在", "select file", Messages.getWarningIcon());
                    return false;
                }
                config.getJavaModelGeneratorConfiguration().setTargetProject(targetProjectPath);
            }
        }
        return true;
    }

    public static GenProcess getGenProcess () {
        return new GenProcess();
    }
}
