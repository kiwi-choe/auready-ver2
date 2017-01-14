package com.kiwi.auready_ver2;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.List;

/**
 * Stubbed data for test
 */
public final class StubbedData {

    // To prevent someone from accidentally instantiating this class
    // give it an empty constructor.
    private StubbedData() {
    }

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    public static abstract class TaskStub {

        private static final int ORDER = 0;
        public static TaskHead TASKHEAD = new TaskHead("stubbedTaskHeadId", "title1", ORDER);
        public static List<TaskHead> TASKHEADS = Lists.newArrayList(
                new TaskHead("stubbedTaskHeadId0", "title0", 0),
                new TaskHead("stubbedTaskHeadId1", "title1", 1),
                new TaskHead("stubbedTaskHeadId2", "title2", 2));

        public static final List<Member> MEMBERS = Lists.newArrayList(
                new Member("stubbedMember0", TASKHEAD.getId(), "email1", "name1"),
                new Member("stubbedMember1", TASKHEAD.getId(), "email2", "name2"),
                new Member("stubbedMember2", TASKHEAD.getId(), "email3", "name3"));

        public static TaskHeadDetail TASKHEAD_DETAIL =
                new TaskHeadDetail(TASKHEAD, MEMBERS);

        public static Task TASK = new Task("stubbedTaskId", MEMBERS.get(0).getId(), "description", 0);
        // 3 tasks, one active and two completed of MEMBER the index 0
        public static List<Task> TASKS = Lists.newArrayList(
                new Task("stubbedTask0", MEMBERS.get(0).getId(), "description", 0),
                new Task("stubbedTask1", MEMBERS.get(0).getId(), "description2", true, 0),
                new Task("stubbedTask2", MEMBERS.get(0).getId(), "description3", true, 0));
    }

    public static abstract class FriendStub {
        // We start the friends to 3.
        public static List<Friend> FRIENDS = Lists.newArrayList(
                new Friend("stubbedFriendId0", "aa@aa.com", "aa"),
                new Friend("stubbedFriendId1", "bb@bb.com", "bb"),
                new Friend("stubbedFriendId2", "cc@cc.com", "cc"));
    }

}
