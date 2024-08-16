package me.earth.cheerpjexperiments;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Base64;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class CheerpJExperiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Hi!");
        boolean noAwt = Boolean.parseBoolean(System.getProperty("noAwt", "false"));
        System.out.println("NoAwt: " + noAwt);
        CheerpJGui gui;
        if (!noAwt) {
            gui = new CheerpJGui();
            gui.init();
        } else {
            gui = null;
        }

        try (InputStream is = CheerpJExperiment.class.getClassLoader().getResourceAsStream("bib.class")) {
            assert is != null;
            ClassReader reader = new ClassReader(is);
            ClassNode cn = new ClassNode();
            reader.accept(cn, 0);

            for (MethodNode mn : cn.methods) {
                if (Modifier.isAbstract(mn.access) || Modifier.isNative(mn.access) || "<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                    continue;
                }

                AbstractInsnNode node = mn.instructions.getFirst();
                AbstractInsnNode last = null;
                while (node != null) {
                    if (node instanceof MethodInsnNode) {
                        InsnList debug = new InsnList();
                        debug.add(new LdcInsnNode("Calling: " + ((MethodInsnNode) node).owner + "." + ((MethodInsnNode) node).name + ((MethodInsnNode) node).desc + " from " + cn.name + "." + mn.name + mn.desc));
                        debug.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CheerpJExperiment.class), "println", "(Ljava/lang/String;)V", false));
                        if (last == null) {
                            mn.instructions.insert(debug);
                        } else {
                            mn.instructions.insert(last, debug);
                        }
                    }

                    last = node;
                    node = node.getNext();
                }

                InsnList debug = new InsnList();
                debug.add(new LdcInsnNode(cn.name + "." + mn.name + mn.desc));
                debug.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CheerpJExperiment.class), "println", "(Ljava/lang/String;)V", false));
                mn.instructions.insert(debug);
                mn.localVariables.clear();
                mn.visitMaxs(0, 0);
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cn.accept(classWriter);
            byte[] bytes = classWriter.toByteArray();
            try (FileOutputStream fos = new FileOutputStream("bib.class")) {
                fos.write(bytes);
            }

            new ClassLoader() {
                {
                    Class<?> clazz = defineClass("bib", bytes, 0, bytes.length);
                    Method af = clazz.getMethod("af"); // some static method
                    System.out.println(af.invoke(null));
                    System.out.println("Invoked af method successfully");
                }
            };

            String string = Base64.getEncoder().encodeToString(bytes);
            if (!noAwt) {
                SwingUtilities.invokeAndWait(() -> gui.displayArea.append("Base64:\n"));
                SwingUtilities.invokeAndWait(() -> gui.displayArea.append(string + "\n"));
                SwingUtilities.invokeAndWait(() -> gui.displayArea.append("Finished\n"));
            }
            System.exit(0);
        } catch (Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.println();
            pw.close();
            if (!noAwt) {
                SwingUtilities.invokeAndWait(() -> gui.displayArea.append(sw + "\n"));
            }
        }

        System.out.println("Bye!");
    }

    // called from the byte code
    public static void println(String string) {
        System.out.println(string);
    }

}
