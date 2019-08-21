package com.happyslowly.optparser;


import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
@AllArgsConstructor
public class Opt {
    private String name;
    private String help;
    private OptType type;
    private String metaVar;
    private boolean required;
    private Object defaultValue;

    public static OptBuilder builder() {
        return new OptBuilder();
    }

    public static class OptBuilder {
        private String name;
        private String help;
        private OptType type;
        private String metaVar;
        private boolean required;
        private Object defaultValue;

        public OptBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OptBuilder help(String help) {
            this.help = help;
            return this;
        }

        public OptBuilder withType(OptType type) {
            this.type = type;
            return this;
        }

        public OptBuilder metaVar(String metaVar) {
            this.metaVar = metaVar;
            return this;
        }

        public OptBuilder asRequired() {
            this.required = true;
            return this;
        }

        public OptBuilder withDefault(Object val) {
            this.defaultValue = val;
            return this;
        }

        public Opt build() {
            if (metaVar == null) {
                metaVar = getMetaVarFromName(name);
            }
            if (type == null) {
                type = OptType.STRING;
            }
            return new Opt(name, help, type, metaVar, required, defaultValue);
        }

        private static String getMetaVarFromName(String name) {
            String[] parts = name.replaceAll("^-+", "")
                    .split("-");
            StringBuilder sb = new StringBuilder();
            sb.append(parts[0]);
            if (parts.length > 1) {
                for (int i = 1; i < parts.length; ++i) {
                    sb.append(parts[1].substring(0, 1).toUpperCase());
                    sb.append(parts[1].substring(1));
                }
            }
            return sb.toString();
        }
    }
}
