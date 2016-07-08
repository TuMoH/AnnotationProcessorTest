package com.timursoft;

import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

import static org.junit.Assert.*;

public class AnnotationProcessorTest {

    @Test
    public void testAp() throws Exception {
        assertTrue(compile(new File(getClass().getClassLoader().getResource("TestClass.java").toURI())));
    }

    private static boolean compile(File... files) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        return compiler.getTask(null, null, null, null, null,
                compiler.getStandardFileManager(null, null, null)
                        .getJavaFileObjects(files)).call();
    }

}