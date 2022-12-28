package io.github.v2compose.network.bean;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import io.github.v2compose.util.Check;

/**
 * Created by ghui on 22/06/2017.
 */

public class ThxResponseInfo extends BaseInfo {
    @Pick(value = "a[href=/balance]", attr = Attrs.HREF)
    private String link;

    @Override
    public boolean isValid() {
        return Check.notEmpty(link);
    }
}
