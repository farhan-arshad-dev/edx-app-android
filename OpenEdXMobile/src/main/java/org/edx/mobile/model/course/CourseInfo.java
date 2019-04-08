package org.edx.mobile.model.course;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model class to hold the course data received from the Branch.io during deep linking.
 */
public class CourseInfo implements Serializable {
    @SerializedName("course_id")
    public String courseId;

    @SerializedName("topic_id")
    public String topicId;

    @SerializedName("thread_id")
    public String threadId;
}
