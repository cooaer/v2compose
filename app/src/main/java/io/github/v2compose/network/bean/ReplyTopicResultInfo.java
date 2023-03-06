package io.github.v2compose.network.bean;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

@Pick(value = "div#Wrapper")
public class ReplyTopicResultInfo extends BaseInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "div.problem", attr = Attrs.HTML)
    private String problem;

    public String getOnce() {
        return once;
    }

    public String getProblem() {
        return problem != null ? problem : "";
    }

    @Override
    public boolean isValid() {
        return once != null && !once.isEmpty();
    }

    @Override
    public String toString() {
        return "ReplyTopicResultInfo{" +
                "once='" + once + '\'' +
                ", problem='" + problem + '\'' +
                '}';
    }
}
