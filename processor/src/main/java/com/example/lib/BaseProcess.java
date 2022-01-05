package com.example.lib;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import sun.rmi.runtime.Log;

/**
 *
 * @author dan.tang
 */
public abstract class BaseProcess extends AbstractProcessor {

    Filer mFiler;
    Elements elementUtils;
    Types typeUtil;
    String moduleName;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtil = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();

        if (processingEnv.getOptions() != null && !processingEnv.getOptions().isEmpty()) {
            moduleName = processingEnv.getOptions().get(CompileConstants.KEY_MODULE_NAME);
        }

        if (moduleName != null && !moduleName.isEmpty()) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            throw new RuntimeException("Country Diff::Compiler >>> No module name, for more information, look at gradle log.");
        }

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
