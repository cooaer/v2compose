package io.github.v2compose.network.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 05/06/2017.
 */

@Pick("div#Wrapper")
public class CreateTopicPageInfo extends BaseInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;

    @Pick("div.problem")
    private Problem problem;

    public String getOnce() {
        return once;
    }

    @Nullable
    public Problem getProblem() {
        return problem;
    }

    public Map<String, String> toPostMap(String title, String content, String nodeName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("node_name", nodeName);
        map.put("once", once);
        return map;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(once);
    }

    @NonNull
    @Override
    public String toString() {
        return "CreateTopicPageInfo{" +
                "once='" + once + '\'' +
                ", problem=" + problem +
                '}';
    }

    public static class Problem implements Serializable {
        @Pick(attr = Attrs.HTML)
        private String html;

        @Pick(attr = Attrs.OWN_TEXT)
        private String title;
        @Pick("ul li")
        private List<String> tips;

        public String getHtml() {
            return html;
        }

        public List<String> getTips() {
            return tips != null ? tips : Collections.emptyList();
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "Problem{" +
                    "html='" + html + '\'' +
                    ", title='" + title + '\'' +
                    ", tips=" + tips +
                    '}';
        }

        public boolean isEmpty() {
            return Check.isEmpty(tips) && Check.isEmpty(title);
        }
    }
}
