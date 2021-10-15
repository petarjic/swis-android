package com.swis.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class ColorList {



    @SerializedName("colorCode")
    String colorCode;

    @SerializedName("isSelected")
    boolean isSelected;


    public static final ColorList[] COLOR_LISTS = {
            new ColorList("#FFFF007F", false),
            new ColorList("#E6FF007F", false),
            new ColorList("#CCFF007F", false),
            new ColorList("#B3FF007F", false),
            new ColorList("#99FF007F", false),
            new ColorList("#80FF007F", false),
            new ColorList("#66FF007F", false),
            new ColorList("#4DFF007F", false),
            new ColorList("#33FF007F", false),
            new ColorList("#1AFF007F", false),

            new ColorList("#FF7F00FF", false),
            new ColorList("#E67F00FF", false),
            new ColorList("#CC7F00FF", false),
            new ColorList("#B37F00FF", false),
            new ColorList("#997F00FF", false),
            new ColorList("#807F00FF", false),
            new ColorList("#667F00FF", false),
            new ColorList("#4D7F00FF", false),
            new ColorList("#337F00FF", false),
            new ColorList("#1A7F00FF", false),

            new ColorList("#FFFF00FF", false),
            new ColorList("#E6FF00FF", false),
            new ColorList("#CCFF00FF", false),
            new ColorList("#B3FF00FF", false),
            new ColorList("#99FF00FF", false),
            new ColorList("#80FF00FF", false),
            new ColorList("#66FF00FF", false),
            new ColorList("#4DFF00FF", false),
            new ColorList("#33FF00FF", false),
            new ColorList("#1AFF00FF", false),


            new ColorList("#FF9932CC", false),
            new ColorList("#E69932CC", false),
            new ColorList("#CC9932CC", false),
            new ColorList("#B39932CC", false),
            new ColorList("#999932CC", false),
            new ColorList("#809932CC", false),
            new ColorList("#669932CC", false),
            new ColorList("#4D9932CC", false),
            new ColorList("#339932CC", false),
            new ColorList("#1A9932CC", false),


            new ColorList("#FF98FB98", false),
            new ColorList("#E698FB98", false),
            new ColorList("#CC98FB98", false),
            new ColorList("#B398FB98", false),
            new ColorList("#9998FB98", false),
            new ColorList("#8098FB98", false),
            new ColorList("#6698FB98", false),
            new ColorList("#4D98FB98", false),
            new ColorList("#3398FB98", false),
            new ColorList("#1A98FB98", false),

            new ColorList("#FF0BE4CD", false),
            new ColorList("#E60BE4CD", false),
            new ColorList("#CC0BE4CD", false),
            new ColorList("#B30BE4CD", false),
            new ColorList("#990BE4CD", false),
            new ColorList("#800BE4CD", false),
            new ColorList("#660BE4CD", false),
            new ColorList("#4D0BE4CD", false),
            new ColorList("#330BE4CD", false),
            new ColorList("#1A0BE4CD", false),


            new ColorList("#FF000000", false),
            new ColorList("#E6000000", false),
            new ColorList("#CC000000", false),
            new ColorList("#B3000000", false),
            new ColorList("#99000000", false),
            new ColorList("#80000000", false),
            new ColorList("#66000000", false),
            new ColorList("#4D000000", false),
            new ColorList("#33000000", false),
            new ColorList("#1A000000", false),

            new ColorList("#FF2c6097", false),
            new ColorList("#E62c6097", false),
            new ColorList("#CC2c6097", false),
            new ColorList("#B32c6097", false),
            new ColorList("#992c6097", false),
            new ColorList("#802c6097", false),
            new ColorList("#662c6097", false),
            new ColorList("#4D2c6097", false),
            new ColorList("#332c6097", false),
            new ColorList("#1A2c6097", false),


            new ColorList("#FF2278d4", false),
            new ColorList("#E62278d4", false),
            new ColorList("#CC2278d4", false),
            new ColorList("#B32278d4", false),
            new ColorList("#992278d4", false),
            new ColorList("#802278d4", false),
            new ColorList("#662278d4", false),
            new ColorList("#4D2278d4", false),
            new ColorList("#332278d4", false),
            new ColorList("#1A2278d4", false),

            new ColorList("#FFFF6F00", false),
            new ColorList("#E6FF6F00", false),
            new ColorList("#CCFF6F00", false),
            new ColorList("#B3FF6F00", false),
            new ColorList("#99FF6F00", false),
            new ColorList("#80FF6F00", false),
            new ColorList("#66FF6F00", false),
            new ColorList("#4DFF6F00", false),
            new ColorList("#33FF6F00", false),
            new ColorList("#1AFF6F00", false),

            new ColorList("#FFDAA520", false),
            new ColorList("#E6DAA520", false),
            new ColorList("#CCDAA520", false),
            new ColorList("#B3DAA520", false),
            new ColorList("#99DAA520", false),
            new ColorList("#80DAA520", false),
            new ColorList("#66DAA520", false),
            new ColorList("#4DDAA520", false),
            new ColorList("#33DAA520", false),
            new ColorList("#1ADAA520", false),

    };


    public ColorList( String colorCode, boolean isSelected) {
        this.colorCode = colorCode;
        this.isSelected = isSelected;
    }


    public ColorList() {

    }


    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    private static List<ColorList> allColorList;

    public static List<ColorList> getAllColors() {
        if (allColorList == null) {
            allColorList = Arrays.asList(COLOR_LISTS);
        }
        return allColorList;
    }
}
