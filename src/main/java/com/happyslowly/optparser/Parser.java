package com.happyslowly.optparser;

public interface Parser extends SingleParser {
    SingleParser addGroup(String name);
    SingleParser addGroup(String name, String help);
    String getGroup();
}
