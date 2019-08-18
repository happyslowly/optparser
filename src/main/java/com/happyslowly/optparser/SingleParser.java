package com.happyslowly.optparser;

import java.util.List;
import java.util.Map;

public interface SingleParser {
    SingleParser add(Opt opt);
    void parse(String[] args);

    Map<String, Object> getOptions();
    List<String> getArguments();

    String getName();
    void printHelp();
}
