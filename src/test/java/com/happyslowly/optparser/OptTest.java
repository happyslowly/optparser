package com.happyslowly.optparser;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class OptTest {

    @Test
    public void test() {
        Opt opt = Opt.builder().name("--start-date").help("start date").build();
        Assert.assertEquals("startDate", opt.getMetaVar());
    }

    @Test
    public void testSimpleParser() {
        Parser parser = new OptParser("foo");
        parser.add(Opt.builder().name("-i").help("input file").build())
                .add(Opt.builder().name("-o").help("output file").build())
                .add(Opt.builder().name("-l").type(OptType.INTEGER).metaVar("lines").help("lines to write").build())
                .add(Opt.builder().name("-v").asAction().help("verbose").build());

        parser.parse("-v -i foo.txt -o bar.txt -l 42 third.txt".split(" "));
        Assert.assertEquals(
            new HashMap<String, Object>() {{
                put("v", true);
                put("i", "foo.txt");
                put("o", "bar.txt");
                put("lines", 42);
            }}, parser.getOptions());

        Assert.assertEquals(
            new ArrayList<String>() {{
                add("third.txt");
            }}, parser.getArguments());
    }

    @Test
    public void testGroups() {
        Parser parser = new OptParser("mycmd");
        SingleParser group1 = parser.addGroup("group1",
                "group1 sub commands");
        SingleParser group2 = parser.addGroup("group2",
                "group2 sub commands");

        group1.add(Opt.builder().name("--start-dt").help("start date").build())
                .add(Opt.builder().name("--end-dt").help("end date").build())
                .add(Opt.builder().name("--other").help("other option").build());

        group2.add(Opt.builder().name("--start-dt").help("start date").build())
                .add(Opt.builder().name("--end-dt").help("end date").build())
                .add(Opt.builder().name("--other2").help("other option 2").build());

        parser.parse("group1 --start-dt 1970/01/01 --end-dt 1970/01/01 --other abc".split(" "));

        Assert.assertEquals(
                new HashMap<String, Object>() {{
                    put("startDt", "1970/01/01");
                    put("endDt", "1970/01/01");
                    put("other", "abc");
                }}, parser.getOptions());

        Assert.assertEquals("group1", parser.getGroup());
    }

}
