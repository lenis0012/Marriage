package com.lenis0012.bukkit.marriage2.internal;

import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.Marriage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

public class MarriagePlugin extends JavaPlugin {
    private static MarriageCore core;

    public static Marriage getCore() {
        return core;
    }

    @SuppressWarnings("unchecked")
    private final List<Method>[] methods = new List[Register.Type.values().length];

    public MarriagePlugin() {
        core = new MarriageCore(this, getClassLoader());

        //Scan methods
        for(int i = 0; i < methods.length; i++) {
            methods[i] = Lists.newArrayList();
        }
        scanMethods(core.getClass());
    }

    private void scanMethods(Class<?> clazz) {
        if(clazz == null) {
            return;
        }

        // Loop through all methods in class
        for(Method method : clazz.getMethods()) {
            Register register = method.getAnnotation(Register.class);
            if(register != null) {
                methods[register.type().ordinal()].add(method);
            }
        }

        // Scan methods in super class
        scanMethods(clazz.getSuperclass());
    }

    protected File getPluginFile() {
        return getFile();
    }

    @Override
    public void onLoad() {
        executeMethods(Register.Type.LOAD);
    }

    @Override
    public void onEnable() {
        executeMethods(Register.Type.ENABLE);
    }

    @Override
    public void onDisable() {
        executeMethods(Register.Type.DISABLE);
    }

    private void executeMethods(Register.Type type) {
        List<Method> list = Lists.newArrayList(methods[type.ordinal()]);
        while(!list.isEmpty()) {
            Method method = null;
            int lowestPriority = Integer.MAX_VALUE;
            for(Method m : list) {
                Register register = m.getAnnotation(Register.class);
                if(register.priority() < lowestPriority) {
                    method = m;
                    lowestPriority = register.priority();
                }
            }

            if(method != null) {
                list.remove(method);
                Register register = method.getAnnotation(Register.class);
                if (type == Register.Type.ENABLE) {
                    getLogger().log(Level.INFO, "Loading " + register.name() + "...");
                } else if (type == Register.Type.DISABLE) {
                    getLogger().log(Level.INFO, "Unloading " + register.name() + "...");
                }
                try {
                    method.invoke(core);
                } catch(Exception e) {
                    getLogger().log(Level.SEVERE, "Failed to load " + register.name(), e);
                }
            } else {
                list.clear();
            }
        }

        getLogger().log(Level.INFO, type.getCompletionMessage());
    }
}