package com.fastaccess.data.dao;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.fastaccess.helper.InputHelper;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 11 Feb 2017, 11:03 PM
 */

@Getter @Setter
public class NameParser {

    private String name;
    private String username;

    public NameParser(@Nullable String url) {
        if (!InputHelper.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            List<String> segments = uri.getPathSegments();
            if (segments == null || segments.size() < 2) {
                return;
            }
            this.name = segments.get(1);
            this.username = segments.get(0);
        }
    }

    @Override public String toString() {
        return "NameParser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
