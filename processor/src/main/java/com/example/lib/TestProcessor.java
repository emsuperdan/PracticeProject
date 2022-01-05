package com.example.lib;

import static com.example.lib.CompileConstants.METHOD_LOAD;
import static com.example.lib.CompileConstants.NAME_OF_ROOT;
import static com.example.lib.CompileConstants.PACKAGE_OF_GENERATE_FILE;
import static com.example.lib.CompileConstants.SEPARATOR;

import com.example.annotation.annotation.TestClassAnnotation;
import com.example.annotation.annotation.bean.DiffMeta;
import com.example.annotation.annotation.bean.IDiffMap;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author dan.tang
 */

@AutoService(Processor.class)
public class TestProcessor extends BaseProcess {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE,">>> DiffProcessor init <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> diffElements =
                roundEnv.getElementsAnnotatedWith(TestClassAnnotation.class);
        this.parseDiffElements(diffElements);
        return false;
    }


    public void parseDiffElements(Set<? extends Element> diffElements) {
        if (diffElements == null || diffElements.size() == 0) {
            return;
        }

        messager.printMessage(Diagnostic.Kind.NOTE,">>> DiffProcessor   -- <<<" + diffElements.size());
        Map<ClassName, Set<ClassName>> diffMap = new HashMap<>();
        for (Element element : diffElements) {
            ClassName interFaceElement = null;
            ClassName subClass;
            TypeElement typeElement = null;
            if (element.getKind() == ElementKind.METHOD) {
                typeElement = (TypeElement) element.getEnclosingElement();
            } else if (element.getKind() == ElementKind.CLASS) {
                typeElement = (TypeElement) element;
            }


            subClass = ClassName.get(typeElement);
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            if (interfaces != null && interfaces.size() == 1) {
                TypeMirror typeMirror = interfaces.get(0);
                TypeElement asElement = (TypeElement) typeUtil.asElement(typeMirror);
                interFaceElement = ClassName.get(asElement);
            }

            if (interFaceElement != null && subClass != null) {
                if (diffMap.get(interFaceElement) == null) {
                    Set<ClassName> classNameSet = new HashSet<>();
                    diffMap.put(interFaceElement, classNameSet);
                }

                diffMap.get(interFaceElement).add(subClass);
            }
            messager.printMessage(Diagnostic.Kind.NOTE,">>> DiffProcessor   -- <<<");
        }

        ParameterizedTypeName parameterizedMap = ParameterizedTypeName.get(
                ClassName.get(HashMap.class), ClassName.get(Class.class),
                ParameterizedTypeName.get(DiffMeta.class));

        ParameterSpec rootParamSpec = ParameterSpec.builder(parameterizedMap, "routes").build();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_LOAD)
                .addParameter(rootParamSpec)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        int index = 0;
        for (Map.Entry<ClassName, Set<ClassName>> meta : diffMap.entrySet()) {
            String currentMeta = "diffMeta" + index;
            methodBuilder.addStatement("$T " + currentMeta + "= new $T()",
                    ParameterizedTypeName.get(DiffMeta.class),
                    ParameterizedTypeName.get(DiffMeta.class));
            for (ClassName className : meta.getValue()) {
                methodBuilder.addStatement(currentMeta + ".add($T.class)", className);
            }
            methodBuilder.addStatement("routes.put($T.class, " + currentMeta + ")", meta.getKey());
            index++;
        }

        String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
        try {
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .addSuperinterface(IDiffMap.class)
                            .build()).build().writeTo(mFiler);
        } catch (IOException e) {
        }

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new HashSet<>();
        supportTypes.add(TestClassAnnotation.class.getCanonicalName());
        return supportTypes;
    }

}
