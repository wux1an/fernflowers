package me.wux1an;

import org.apache.commons.cli.*;

public class Application {

    public static void main(String[] args) {
        Option input  = new Option("i", "input", true, "input, the directory to be scanned or a single file path.");
        Option output = new Option("o", "output", true, "output, the directory to saved result, default: current path.");
        Option unzip  = new Option("u", "unzip", false, "unzip the jar file");
        Option thread = new Option("n", "thread", true, "thread, default: 20");
        Option resume = new Option("r", "resume", false, "skip the decompiled files (record in 'decompiled.txt') and continue");
        Option backup = new Option("k", "backup", false, "don't delete the jar file in the output path when '--unzip' is specified");
        Option help   = new Option("h", "help", false, "help");

        Options options = new Options();
        options.addOption(input);
        options.addOption(output);
        options.addOption(unzip);
        options.addOption(thread);
        options.addOption(resume);
        options.addOption(backup);
        options.addOption(help);

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);

            if (cmd.hasOption(help.getOpt())) {
                help(options);
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            help(options);
            System.exit(0);
        }

        Config config = new Config();
        config.input  = cmd.getOptionValue(input.getOpt());
        config.output = cmd.getOptionValue(output.getOpt());
        config.unzip  = cmd.hasOption(unzip.getOpt());
        config.thread = cmd.hasOption(thread.getOpt()) ? Integer.parseInt(cmd.getOptionValue(thread.getOpt())) : 20;
        config.resume = cmd.hasOption(resume.getOpt());
        config.backup = cmd.hasOption(backup.getOpt());

        if (config.input == null) {
            System.out.println("Missing required option: i");
            help(options);
            System.exit(0);
        }
        if (config.output == null) config.output = "";
        Decompiler decompiler = new Decompiler(config);
        decompiler.action();
    }

    private static void help(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.printHelp("java -jar fernflowers.jar [args]", options);
    }
}