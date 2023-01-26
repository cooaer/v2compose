package io.github.v2compose.network.bean;

import androidx.compose.runtime.Stable;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

@Stable
public class SoV2EXSearchResultInfo extends BaseInfo {
    @SerializedName("total")
    private int total;
    @SerializedName("hits")
    private List<Hit> hits;

    public int getTotal() {
        return total;
    }

    public List<Hit> getHits() {
        return hits != null ? hits : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "SoV2EXSearchResultInfo{" +
                "total=" + total +
                ", hits=" + hits +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Stable
    public static class Hit {
        @SerializedName("_source")
        private Source source;

        @SerializedName("highlight")
        private Highlight highlight;

        public Source getSource() {
            return source;
        }

        public Highlight getHighlight() {
            return highlight;
        }

        @Override
        public String toString() {
            return "Hit{" +
                    "source=" + source +
                    ", highlight=" + highlight +
                    '}';
        }

        @Stable
        public static class Source {
            @SerializedName("id")
            private String id;
            @SerializedName("title")
            private String title;
            @SerializedName("content")
            private String content;
            @SerializedName("node")
            private String nodeId;
            @SerializedName("replies")
            private int replies;
            @SerializedName("created")
            private String time;
            @SerializedName("member")
            private String creator;

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }

            public String getNodeId() {
                return nodeId;
            }

            public int getReplies() {
                return replies;
            }

            public String getTime() {
                return time;
            }

            public String getCreator() {
                return creator;
            }

            @Override
            public String toString() {
                return "Source{" +
                        "id='" + id + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", nodeId='" + nodeId + '\'' +
                        ", replies=" + replies +
                        ", time='" + time + '\'' +
                        ", creator='" + creator + '\'' +
                        '}';
            }
        }

        @Stable
        public static class Highlight {
            @SerializedName("title")
            private List<String> title;
            @SerializedName("content")
            private List<String> content;
            @SerializedName("postscript_list.content")
            private List<String> postscriptListContent;
            @SerializedName("reply_list.content")
            private List<String> replyListContent;

            public List<String> getTitle() {
                return title != null ? title : Collections.emptyList();
            }

            public List<String> getContent() {
                return content != null ? content : Collections.emptyList();
            }

            public List<String> getPostscriptListContent() {
                return postscriptListContent != null ? postscriptListContent : Collections.emptyList();
            }

            public List<String> getReplyListContent() {
                return replyListContent != null ? replyListContent : Collections.emptyList();
            }

            @Override
            public String toString() {
                return "Highlight{" +
                        "title=" + title +
                        ", content=" + content +
                        ", postscriptListContent=" + postscriptListContent +
                        ", replyListContent=" + replyListContent +
                        '}';
            }
        }
    }
}
