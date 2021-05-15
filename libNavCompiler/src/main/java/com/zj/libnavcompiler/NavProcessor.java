package com.zj.libnavcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.zj.libnavannotation.ActivityDestination;
import com.zj.libnavannotation.FragmentDestination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.zj.libnavannotation.FragmentDestination", "com.zj.libnavannotation.ActivityDestination"})
public class NavProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //日志打印
        messager = processingEnv.getMessager();
        //文件处理工具
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);

        if (!activityElements.isEmpty() || !fragmentElements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(activityElements, ActivityDestination.class, destMap);
            handleDestination(fragmentElements, FragmentDestination.class, destMap);

            FileOutputStream fileOutputStream = null;
            OutputStreamWriter writer = null;
            try {
                //filer.createResource() 创建源文件
                //可以指定为class文件输出的地方
                //StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
                //StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/goodVideo/app/build/generated/source/apt/目录下
                //StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                messager.printMessage(Diagnostic.Kind.NOTE, "resourcePath: " + resourcePath);

                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets";

                File file = new File(assetsPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File outputFile = new File(file, OUTPUT_FILE_NAME);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                String content = JSON.toJSONString(destMap);
                fileOutputStream = new FileOutputStream(outputFile);
                writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClz, HashMap<String, JSONObject> destMap) {
        if (elements.isEmpty()) {
            return;
        }

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            //全类名
            String clzName = typeElement.getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "clzName: " + clzName);

            //页面id
            int id = Math.abs(clzName.hashCode());
            //页面的pageUrl
            String pageUrl = null;
            //是否需要登录
            boolean needLogin = false;
            //是否作为首页的第一个页面
            boolean asStarter = false;
            //标记该页面是Fragment，还是Activity
            boolean isFragment = false;

            Annotation annotation = element.getAnnotation(annotationClz);
            if (annotation instanceof ActivityDestination) {
                ActivityDestination activityDestination = (ActivityDestination) annotation;
                pageUrl = activityDestination.pageUrl();
                needLogin = activityDestination.needLogin();
                asStarter = activityDestination.asStarter();
                isFragment = false;
            } else if (annotation instanceof FragmentDestination) {
                FragmentDestination fragmentDestination = (FragmentDestination) annotation;
                pageUrl = fragmentDestination.pageUrl();
                needLogin = fragmentDestination.needLogin();
                asStarter = fragmentDestination.asStarter();
                isFragment = true;
            }

            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：" + clzName);
            } else {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("needLogin", needLogin);
                object.put("asStarter", asStarter);
                object.put("pageUrl", pageUrl);
                object.put("className", clzName);
                object.put("isFragment", isFragment);
                destMap.put(pageUrl, object);
            }
        }

    }

}
