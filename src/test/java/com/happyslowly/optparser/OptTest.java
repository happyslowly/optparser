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
        Parser parser = new OptParser("loco");
        SingleParser modelResult = parser.addGroup("rucsModelResult",
                "pull from RUCS model result");
        SingleParser contextLogging = parser.addGroup("rucsContextLog",
                "pull from RUCS context logging");

        modelResult.add(Opt.builder().name("--start-dt").help("start date").build())
                .add(Opt.builder().name("--end-dt").help("end date").build())
                .add(Opt.builder().name("--caller-id").help("caller id").build());

        contextLogging.add(Opt.builder().name("--start-dt").help("start date").build())
                .add(Opt.builder().name("--end-dt").help("end date").build())
                .add(Opt.builder().name("--corr-id").help("corr id").build());

        parser.parse("rucsModelResult --start-dt 1970/01/01 --end-dt 1970/01/01 --caller-id abc".split(" "));

        Assert.assertEquals(
                new HashMap<String, Object>() {{
                    put("startDt", "1970/01/01");
                    put("endDt", "1970/01/01");
                    put("callerId", "abc");
                }}, parser.getOptions());

        Assert.assertEquals("rucsModelResult", parser.getGroup());

//        parser.parse("-h".split(" "));

//        parser.parse("rucsModelResult -h".split(" "));
    }

}
