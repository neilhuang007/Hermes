package dev.hermes.injection.operations;

import dev.hermes.injection.annotations.Inject;
import dev.hermes.injection.annotations.Modify;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public class BytecodeUtil {

    public static byte[] loadClassBytes(String deobfClassName) {

        String path = deobfClassName.replace('.', '/') + ".class";
        System.out.println(path);
        try (InputStream inputStream = BytecodeUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                System.err.println("Could not load class bytes for: " + deobfClassName);
                return null;
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            System.err.println("Failed to read class bytes for: " + deobfClassName);
            e.printStackTrace();
            return null;
        }
    }

    public static void scanAndPrintAnnotatedMethods(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        if (desc.equals(Type.getDescriptor(Inject.class)) || desc.equals(Type.getDescriptor(Modify.class))) {
                            System.out.println("@" + (desc.equals(Type.getDescriptor(Inject.class)) ? "Inject" : "Modify") +
                                    " found in method: " + name);
                        }
                        return super.visitAnnotation(desc, visible);
                    }
                };
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }
}
