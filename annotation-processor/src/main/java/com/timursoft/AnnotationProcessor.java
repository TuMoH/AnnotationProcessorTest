package com.timursoft;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        Element classElement = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();

        TypeSpec.Builder builder = TypeSpec.classBuilder(classElement.getSimpleName().toString() + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(classElement.asType()));

        classElement.getEnclosedElements().stream()
                .filter(it -> it instanceof ExecutableElement)
                .map(it -> (ExecutableElement) it)
                .filter(it -> it.getParameters().size() == 1)
                .forEach(it -> builder.addMethod(MethodSpec.methodBuilder(it.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(void.class)
                            .addParameter(TypeName.get(it.getParameters().get(0).asType()), "text")
                            .addStatement("text = $S + text", "Tag: ")
                            .addStatement("super.$N(text)", it.getSimpleName().toString())
                            .build())
                );

        JavaFile javaFile = JavaFile.builder(classElement.getEnclosingElement().toString(),
                builder.build()).build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Test.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
