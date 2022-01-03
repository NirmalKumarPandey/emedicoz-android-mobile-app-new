package com.emedicoz.app.modelo.courses;

import android.app.Activity;

import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhinav on 2/16/2018.
 */

public class CourseLockedManager {
    public static String DEFAULT_POSITION = "-1";

//    public static void addCourseLockStatus(String courseId, String topicId, String position, Activity activity) {
//        ArrayList<HashMap<String, String>> courseLockedArrayList = new ArrayList<>();
//        if (Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS) != null) {
//            courseLockedArrayList = (ArrayList<HashMap<String, String>>) Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS);
//        }
//
//        int flag = 0;
//        for (HashMap<String, String> courseLocked : courseLockedArrayList) {
//            if (courseLocked.get(courseId) != null && courseLocked.get(courseId).contains(",")) { // contains "," in the string value
//                for (int i = 0; i < courseLocked.get(courseId).split(",").length; i++) {
//                    if (!courseLocked.get(courseId).split(",")[i].equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                        courseLocked.put(courseId, courseLocked.get(courseId) + String.format("%s%s", ",", String.format("%s_%s_%s", courseId, topicId, position)));
//                        flag = 1;
//                        break;
//                    }
//                }
//            } else if (courseLocked.containsKey(courseId) && !courseLocked.get(courseId).equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                courseLocked.put(courseId, courseLocked.get(courseId) + String.format("%s%s", ",", String.format("%s_%s_%s", courseId, topicId, position)));
//                flag = 1;
//                break;
//            }
//        }
//        if (flag == 0) {
//            HashMap<String, String> courseStringHashMap = new HashMap<>();
//            courseStringHashMap.put(courseId, String.format("%s_%s_%s", courseId, topicId, position));
//            courseLockedArrayList.add(courseStringHashMap);
//        }
//        Helper.getStorageInstance(activity).addRecordStore(Const.COURSE_LOCK_STATUS, courseLockedArrayList);
//    }
//
//    public static boolean getCourseLockStatus(String courseId, String topicId, String position, Activity activity) {
//        ArrayList<HashMap<String, String>> courseLockedArrayList = new ArrayList<>();
//        if (Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS) != null) {
//            courseLockedArrayList = (ArrayList<HashMap<String, String>>) Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS);
//        }
//
//        for (HashMap<String, String> courseLocked : courseLockedArrayList) {
//            if (courseLocked.get(courseId) != null && courseLocked.get(courseId).contains(",")) { // contains "," in the string value
//                for (int i = 0; i < courseLocked.get(courseId).split(",").length; i++) {
//                    if (courseLocked.get(courseId).split(",")[i].equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                        return true;
//                    }
//                }
//            } else if (courseLocked.get(courseId) != null && courseLocked.get(courseId).equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static void addCourseLockStatus(String courseId, String topicId, Activity activity) {
        ArrayList<HashMap<String, String>> courseLockedArrayList = new ArrayList<>();
        if (Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS) != null) {
            courseLockedArrayList = (ArrayList<HashMap<String, String>>) Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS);
        }
        int flag = 0;
        for (HashMap<String, String> courseLocked : courseLockedArrayList) {
//            if (courseLocked.get(courseId) != null) { // contains "," in the string value
//                for (int i = 0; i < courseLocked.get(courseId).split(",").length; i++) {
//                    if (!courseLocked.get(courseId).split(",")[i].equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                        courseLocked.put(courseId, courseLocked.get(courseId) + String.format("%s%s", ",", String.format("%s_%s_%s", courseId, topicId, position)));
//                        flag = 1;
//                        break;
//                    }
//                }
//            } else
            if (courseLocked.get(courseId) != null && courseLocked.containsKey(courseId)) {
                if (Integer.parseInt(courseLocked.get(courseId)) >= Integer.parseInt(topicId)) {
                    flag = 1;
                    break;
                } else {
                    courseLocked.put(courseId, String.format("%s", String.format("%s", topicId)));
                    flag = 1;
                    break;
                }
            }
        }
        if (flag == 0) {
            HashMap<String, String> courseStringHashMap = new HashMap<>();
            courseStringHashMap.put(courseId, String.format("%s", topicId));
            courseLockedArrayList.add(courseStringHashMap);
        }
        Helper.getStorageInstance(activity).addRecordStore(Const.COURSE_LOCK_STATUS, courseLockedArrayList);
    }

    public static String getCourseLockStatus(String courseId, String topicId, Activity activity) {
        ArrayList<HashMap<String, String>> courseLockedArrayList = new ArrayList<>();
        if (Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS) != null) {
            courseLockedArrayList = (ArrayList<HashMap<String, String>>) Helper.getStorageInstance(activity).getRecordObject(Const.COURSE_LOCK_STATUS);
        }

        for (HashMap<String, String> courseLocked : courseLockedArrayList) {
//            if (courseLocked.get(courseId) != null && courseLocked.get(courseId).contains(",")) { // contains "," in the string value
//                for (int i = 0; i < courseLocked.get(courseId).split(",").length; i++) {
//                    if (courseLocked.get(courseId).split(",")[i].equals(String.format("%s_%s_%s", courseId, topicId, position))) {
//                        return true;
//                    }
//                }
//            } else
            if (courseLocked.get(courseId) != null) {
                return courseLocked.get(courseId);
            }
        }
        return DEFAULT_POSITION;
    }
    /*public class CourseLocked implements Serializable {
        private String courseId;
        private String paremterizedId;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getParemterizedId() {
            return paremterizedId;
        }

        public void setParemterizedId(String paremterizedId) {
            this.paremterizedId = paremterizedId;
        }
    }*/
}
