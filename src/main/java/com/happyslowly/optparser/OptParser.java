package com.happyslowly.optparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OptParser implements Parser {
    private Map<String, SingleParser> groups;
    private Map<String, String> groupHelps;
    private String name;
    private String group;
    private SingleParser defaultParser;

    public OptParser(String name) {
        this.name = name;
    }

    @Override
    public SingleParser addGroup(String name) {
        return addGroup(name, "");
    }

    @Override
    public SingleParser addGroup(String name, String help) {
        if (groups == null) {
            groups = new HashMap<>();
        }
        if (groupHelps == null) {
            groupHelps = new TreeMap<>();
        }

        ParserImpl p = new ParserImpl(name);
        p.setParent(this);
        groups.put(name, p);
        groupHelps.put(name, help);

        return p;
    }

    @Override
    public SingleParser add(Opt opt) {
        if (defaultParser == null) {
            defaultParser = new ParserImpl(name);
        }
        return defaultParser.add(opt);
    }

    @Override
    public void parse(String[] input) {
        String first = input[0];
        if (groups != null && groups.containsKey(first)) {
            group = first;
            groups.get(first).parse(input);
        } else if (first.equalsIgnoreCase("-h") ||
                first.equalsIgnoreCase("--help")) {
            printHelp();
        } else {
            defaultParser.parse(input);
        }
    }

    @Override
    public Map<String, Object> getOptions() {
        if (group != null) {
            return groups.get(group).getOptions();
        }
        return defaultParser.getOptions();
    }

    @Override
    public List<String> getArguments() {
        if (group != null) {
            return groups.get(group).getArguments();
        }
        return defaultParser.getArguments();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void printHelp() {
        StringBuilder sb = new StringBuilder();
        if (groups != null) {
            sb.append(String.format("Usage: %s COMMAND%n", name));
            sb.append("Commands:\n");
            int maxLen = groups.keySet().stream().map(String::length).max(Integer::compareTo).orElse(0);
            String fmt = Helper.getOptionFormat(maxLen);
            for (Map.Entry<String, String> help : groupHelps.entrySet()) {
                sb.append(String.format(fmt, help.getKey(), help.getValue()));
            }
            System.out.println(sb.toString());
        } else {
            defaultParser.printHelp();
        }
    }

    @Override
    public String getGroup() {
        return group;
    }
}
