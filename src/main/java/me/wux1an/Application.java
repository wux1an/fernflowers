package me.wux1an;

import org.apache.commons.cli.*;

public class Application {

    public static void main(String[] args) {
        Option input   = new Option("i", true, "input, the directory to be scanned.");
        Option output  = new Option("o", true, "output, the directory to saved result.");
        Option unzip   = new Option("unzip", "unzip the jar file");
        Option thread  = new Option("thread", true, "thread, default: 20");
        Option timeout = new Option("timeout", true, "decompile timeout time, default: 10");
        Option help    = new Option("h", "help");

        Options options = new Options();
        options.addOption(input);
        options.addOption(output);
        options.addOption(unzip);
        options.addOption(thread);
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
        config.input   = cmd.getOptionValue(input.getOpt());
        config.output  = cmd.getOptionValue(output.getOpt());
        config.unzip   = cmd.hasOption(unzip.getOpt());
        config.thread  = cmd.hasOption(thread.getOpt()) ? Integer.parseInt(cmd.getOptionValue(thread.getOpt())) : 20;
        config.timeout = cmd.hasOption(timeout.getOpt()) ? Integer.parseInt(cmd.getOptionValue(timeout.getOpt())) : 10;

        Decompiler decompiler = new Decompiler(config);
        decompiler.action();
    }

    private static void help(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar fernflowers.jar [args]", options);
    }
}