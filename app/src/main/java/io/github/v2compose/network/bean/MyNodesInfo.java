package io.github.v2compose.network.bean;

import java.io.Serializable;
import java.util.List;

import io.github.v2compose.util.Check;
import io.github.v2compose.util.Utils;
import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 18/05/2017.
 * https://www.v2ex.com/my/nodes
 */

@Pick("div#my-nodes")
public class MyNodesInfo extends BaseInfo {

    @Pick("a.fav-node")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(items) <= 0) return true;
        return Check.notEmpty(items.get(0).title);
    }

    public static class Item implements Serializable {
        @Pick(value = "img", attr = Attrs.SRC)
        private String avatar;
        @Pick(value = "span.fav-node-name", attr = Attrs.OWN_TEXT)
        private String title;
        @Pick(value = "span.fade.f12")
        private int topicNum;
        @Pick(attr = Attrs.HREF)
        private String link;

        @Override
        public String toString() {
            return "Item{" +
                    "avatar='" + avatar + '\'' +
                    ", title='" + title + '\'' +
                    ", topicNum=" + topicNum +
                    ", link='" + link + '\'' +
                    '}';
        }

        public String getAvatar() {
            return avatar == null ? "" : avatar;
        }

        public String getTitle() {
            return title;
        }

        public int getTopicNum() {
            return topicNum;
        }

        private String _name;

        public String getName() {
            if (_name != null) return _name;
            _name = link.substring("/go/".length());
            return _name;
        }

        public String getLink() {
            return link;
        }
    }
}
