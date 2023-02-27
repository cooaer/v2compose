package io.github.v2compose.network.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

@Pick("div#Wrapper")
public class AppendTopicPageInfo extends BaseInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick("div.inner ul li")
    private List<Tip> tips;
    @Pick("div.problem")
    private Problem problem;

    @Nullable
    public Problem getProblem() {
        return problem;
    }

    public List<Tip> getTips() {
        return tips;
    }

    public String getOnce() {
        return once;
    }

    public Map<String, String> toPostMap(String content) {
        Map<String, String> map = new HashMap<>(2);
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(once) && tips != null && tips.size() > 1;
    }

    @Override
    public String toString() {
        return "AppendTopicPageInfo{" +
                "once='" + once + '\'' +
                ", tips=" + tips +
                '}';
    }

    public static class Tip implements Serializable {
        @Pick
        public String text;

        @Override
        public String toString() {
            return "Tip{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    public static class Problem implements Serializable {
        @Pick(attr = Attrs.HTML)
        private String html;
        @Pick(attr = Attrs.OWN_TEXT)
        private String title;
        @Pick("ul li")
        private List<String> tips;

        public boolean isEmpty() {
            return Check.isEmpty(tips) && Check.isEmpty(title);
        }

        public String getHtml() {
            return html;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getTips() {
            return tips != null ? tips : Collections.emptyList();
        }

        @Override
        public String toString() {
            return "Problem{" +
                    "html='" + html + '\'' +
                    ", title='" + title + '\'' +
                    ", tips=" + tips +
                    '}';
        }
    }
}
