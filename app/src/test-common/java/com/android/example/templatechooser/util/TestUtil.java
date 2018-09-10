
package com.android.example.templatechooser.util;

import com.android.example.templatechooser.vo.Design;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static List<Design> createDesigns(int count, String name) {
        List<Design> designs = new ArrayList<>();
        for(int i = 0; i < count; i ++) {
            designs.add(createDesign(name + i));
        }
        return designs;
    }

    public static Design createDesign(String name) {
        return createDesign(UUID.randomUUID().hashCode(), name);
    }

    public static Design createDesign(Integer id, String name) {
        return new Design(id, name, new Design.Screenshots("mediumScreenshotUrl"), new ArrayList<Design.Variation>());
    }

}
