package amodule.lesson.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.xiangha.R;

import java.util.Map;

import amodule.lesson.activity.CourseDetail;

public class CourseDetailRadioButtonView extends RelativeLayout {
    public CourseDetailRadioButtonView(Context context) {
        this(context, null);
    }

    public CourseDetailRadioButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseDetailRadioButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_course_radiobutton, this, true);
    }
}
