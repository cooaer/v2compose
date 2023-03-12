package io.github.v2compose.network.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.v2compose.util.Check;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 21/05/2017.
 * https://www.v2ex.com/
 * bottom box
 */

@Pick("div.box:last-child div > table")
public class NodesNavInfo extends ArrayList<NodesNavInfo.Item> implements IBase {
    private String mResponseBody;

    @Override
    public String getResponse() {
        return mResponseBody;
    }

    @Override
    public void setResponse(String response) {
        mResponseBody = response;
    }

    @Override
    public boolean isValid() {
        if (size() <= 0) return true;
        return Check.notEmpty(get(0).category);
    }

    public static class Item implements Serializable {
        @Pick("span.fade")
        private String category;
        @Pick("a")
        private List<NodeItem> nodes;

        @Override
        public String toString() {
            return "Item{" +
                    "category='" + category + '\'' +
                    ", nodes=" + nodes +
                    '}';
        }

        public String getCategory() {
            return category;
        }

        public List<NodeItem> getNodes() {
            return nodes;
        }

        public static class NodeItem implements Serializable {
            @Pick
            private String title;
            @Pick(attr = Attrs.HREF)
            private String link;

            @Override
            public String toString() {
                return "NodeItem{" +
                        "name='" + title + '\'' +
                        ", link='" + link + '\'' +
                        '}';
            }

            public String getName() {
                return link.substring(4);
            }

            public String getTitle() {
                return title;
            }

            public String getLink() {
                return link;
            }
        }
    }

}
