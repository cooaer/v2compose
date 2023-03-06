package io.github.v2compose.network.bean;

import androidx.compose.runtime.Stable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.github.v2compose.util.AvatarUtils;
import io.github.v2compose.util.Check;

/**
 * Created by ghui on 27/05/2017.
 * 节点详情
 * https://www.v2ex.com/api/nodes/show.json?name=qna
 */

@Stable
public class NodeInfo extends BaseInfo implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("title")
    private String title;
    @SerializedName("topics")
    private int topics;
    @SerializedName("stars")
    private int stars;
    @SerializedName("header")
    private String header = "";
    @SerializedName("created")
    private long created;
    @SerializedName("avatar_large")
    private String avatar;

    @Override
    public String toString() {
        return "NodeInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", topics=" + topics +
                ", stars=" + stars +
                ", header='" + header + '\'' +
                ", created=" + created +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int getTopics() {
        return topics;
    }

    public int getStars() {
        return stars;
    }

    public String getHeader() {
        return header != null ? header : "";
    }

    public long getCreated() {
        return created;
    }

    public String getAvatar() {
        return AvatarUtils.adjustAvatar(avatar);
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(name);
    }


}
