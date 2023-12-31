package dev.hermes.module;

import dev.hermes.Hermes;
import dev.hermes.value.Value;
import dev.hermes.value.impl.BoolValue;
import dev.hermes.value.impl.FloatValue;
import dev.hermes.value.impl.IntValue;
import dev.hermes.value.impl.ListValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ModuleConfig {
    private final ArrayList<File> configs = new ArrayList<File>();
    private int prevConfigs;
    private double scrollY;

    public ModuleConfig() {
        this.loadConfigs();
        this.load();
    }

    public void loadConfigs() {
        if (prevConfigs != Objects.requireNonNull(Hermes.fileManager.getNConfigDir().listFiles()).length) {

            prevConfigs = Objects.requireNonNull(Hermes.fileManager.getNConfigDir().listFiles()).length;

            configs.clear();

            scrollY = 0;

            FilenameFilter filter = (file, str) -> str.endsWith("hermes");

            File[] fileArray = Hermes.fileManager.getNConfigDir().listFiles(filter);

            if (fileArray != null) {
                Collections.addAll(configs, fileArray);
            }
        }
    }

    public void save(File file) {
        ArrayList<String> toSave = new ArrayList<String>();

        for (Module module : Hermes.moduleManager.getModules()) {
            toSave.add("ModuleName:" + module.moduleName + ":" + module.isToggled() + ":" + module.getKeybinding());
            // toSave.add("ModulePos:");
        }

        for (Module module : Hermes.moduleManager.getModules()) {
            if (module.getValues().size() > 0) {
                for (Value<?> value : module.getValues()) {
                    toSave.add("SET:" + module.moduleName + ":" + value.getName() + ":" + value.get());
                }
            }
        }

        try {
            PrintWriter pw = new PrintWriter(file);
            for (String str : toSave) {
                pw.println(str);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            // Don't give crackers clues...
            if (Hermes.DEVELOPMENT_SWITCH) e.printStackTrace();
        }
    }

    public void load(File file) {

        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            // Don't give crackers clues...
            if (Hermes.DEVELOPMENT_SWITCH) e.printStackTrace();
        }

        try {
            for (String s : lines) {

                String[] args = s.split(":");

                if (s.toLowerCase().startsWith("modulename:")) {
                    Module m = Hermes.moduleManager.getModule(args[1]);
                    if (m != null) {
                        m.setToggled(Boolean.parseBoolean(args[2]));
                        // Load config is later than init modules
                        Hermes.moduleManager.setKeybind(m, Integer.parseInt(args[3]));
                    }
                } else if (s.toLowerCase().startsWith("set:")) {
                    Module m = Hermes.moduleManager.getModule(args[1]);
                    if (m != null) {
                        Value<?> set = m.getValueByName(args[2]);
                        if (set != null) {
                            if (set instanceof BoolValue) {
                                ((BoolValue) set).set(Boolean.parseBoolean(args[3]));
                            }
                            if (set instanceof ListValue) {
                                ((ListValue) set).set(args[3]);
                            }
                            if (set instanceof FloatValue) {
                                ((FloatValue) set).set(Float.parseFloat(args[3]));
                            }
                            if (set instanceof IntValue) {
                                ((IntValue) set).set(Integer.parseInt(args[3]));
                            }
                        }
                    }
                    // PlayerUtils.tellPlayer("args[1]: " + args[1] + " args[2]: " + args[2] + " args[3]: " + args[3]);
                }
            }
        } catch (Exception exception) {
            if (Hermes.DEVELOPMENT_SWITCH) {
                exception.printStackTrace();
            }
        }
    }

    public void save() {
        this.save(Hermes.fileManager.getNConfigFile());
    }

    public void load() {
        this.load(Hermes.fileManager.getNConfigFile());
    }

    public ArrayList<File> getConfigs() {
        return configs;
    }

    public double getScrollY() {
        return scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }
}
