package com.happyslowly.optparser;

import java.util.*;
import java.util.stream.Collectors;

public class ParserImpl implements SingleParser {

    private String name;
    private Map<String, Opt> opts;
    private Map<String, Object> options;
    private List<String> arguments;
    private Parser parent;

    public ParserImpl(String name) {
        this.name = name;
        this.opts = new LinkedHashMap<>();
        this.options = new HashMap<>();
        this.arguments = new ArrayList<>();
    }

    public Parser getParent() {
        return parent;
    }

    public void setParent(Parser parser) {
        this.parent = parser;
    }

    @Override
    public SingleParser add(Opt opt) {
        opts.put(opt.getName(), opt);
        return this;
    }

    @Override
    public void parse(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String token = args[i];
            if (token.startsWith("-")) {
                if (token.equalsIgnoreCase("-h") || token.equalsIgnoreCase("--help")) {
                    printHelp();
                    return;
                }

                Opt opt = opts.get(token);
                if (opt == null) {
                    throw new IllegalArgumentException("Unknown option: " + token + " - ");
                }

                if (opt.getType() == OptType.BOOLEAN) {
                    options.put(opt.getMetaVar(), true);
                } else {
                    if (i < args.length - 1) {
                        String next = args[++i];
                        if (next.startsWith("-")) {
                            throw new IllegalArgumentException("option is missing for " + token);
                        }
                        options.put(opt.getMetaVar(), processValue(next, opt.getType()));
                    } else {
                        throw new IllegalArgumentException("option is missing for " + token);
                    }
                }
            } else {
                arguments.add(token);
            }
        }
        // Setup All the defaults
        opts.entrySet().stream().filter(e -> e.getValue().getDefaultValue() != null).forEach(e -> {
            if (!options.containsKey(e.getKey())) options.put(e.getValue().getMetaVar(), e.getValue().getDefaultValue());
        });

        // check all the required options are there
        for (Opt opt : opts.values()) {
            if (opt.isRequired() && !options.containsKey(opt.getMetaVar())) {
                throw new IllegalArgumentException("option " + opt.getName() + " must be provided");
            }
        }
    }

    @Override
    public void printHelp() {
        StringBuilder sb = new StringBuilder();
        if (parent != null) {
            sb.append(String.format("Usage: %s %s [OPTIONS]%n",
                    parent.getName(), this.getName()));
        } else {
            sb.append(String.format("Usage: %s [OPTIONS]%n", this.getName()));
        }
        sb.append("OPTIONS:\n");
        int maxLen = opts.keySet().stream().map(String::length).max(Integer::compareTo).orElse(0);
        String fmt = Helper.getOptionFormat(maxLen);
        for (Map.Entry<String, Opt> opt : opts.entrySet()) {
            sb.append(String.format(fmt, opt.getKey(), opt.getValue().getHelp()));
        }
        System.out.println(sb.toString());
    }

    @Override
    public Map<String, Object> getOptions() {
        return options;
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String getName() {
        return name;
    }

    private Object processValue(String token, OptType type) {
        switch (type) {
            case INTEGER:
                return Integer.parseInt(token);
            case LONG:
                return Long.parseLong(token);
            default:
                return token;
        }
    }

}
