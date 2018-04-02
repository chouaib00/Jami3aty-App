package com.ibnkhaldoun.studentside.enums;

import com.ibnkhaldoun.studentside.R;

/**
 * @definition this final class will have the method and the field
 * of the type of the post in which the professor will add and
 * some kind of that.
 */
public final class PostTypes {

    public static final int AVIS_TYPE = 1;
    public static final int CONSULTATION_TYPE = 2;
    public static final int MARK_TYPE = 3;

    public static int getType(int type) {
        switch (type) {
            case AVIS_TYPE:
                return R.string.avis_sring;
            case CONSULTATION_TYPE:
                return R.string.consultation_string;
            case MARK_TYPE:
                return R.string.mark_string;
            default:
                return R.string.avis_sring;
        }

    }
}
