package com.kiwi.auready.taskheads;

import com.kiwi.auready.TestUseCaseScheduler;
import com.kiwi.auready.UseCaseHandler;
import com.kiwi.auready.data.TaskHead;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;
import com.kiwi.auready.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready.taskheads.domain.usecase.GetTaskHeadDetails;
import com.kiwi.auready.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready.taskheads.domain.usecase.UpdateTaskHeadOrders;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready.StubbedData.TaskStub.TASKHEADS;
import static com.kiwi.auready.StubbedData.TaskStub.TASKHEAD_DETAILS;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * TaskHeadsPresenter test
 */
public class TaskHeadsPresenterTest {

    private TaskHeadsPresenter mTaskHeadsPresenter;

    @Mock
    private TaskRepository mRepository;
    @Mock
    private TaskHeadsContract.View mTaskHeadView;

    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadDetailsCallback> mLoadTaskHeadsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TaskDataSource.InitLocalDataCallback> mInitLocalDataCallbackCaptor;

    @Captor
    private ArgumentCaptor<TaskDataSource.DeleteTaskHeadsCallback> mDeleteTaskHeadsCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.UpdateTaskHeadOrdersCallback> mUpdateTaskHeadCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsPresenter = givenTaskHeadsPresenter();
    }

    private TaskHeadsPresenter givenTaskHeadsPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHeadDetails getTaskHeadDetails = new GetTaskHeadDetails(mRepository);
        DeleteTaskHeads deleteTaskHeads = new DeleteTaskHeads(mRepository);
        GetTaskHeadsCount getTaskHeadsCount = new GetTaskHeadsCount(mRepository);
        UpdateTaskHeadOrders updateTaskHeadOrders = new UpdateTaskHeadOrders(mRepository);

        return new TaskHeadsPresenter(useCaseHandler, mTaskHeadView,
                getTaskHeadDetails, deleteTaskHeads, getTaskHeadsCount, updateTaskHeadOrders);
    }

    @Test
    public void loadAllTaskHeadsFromRepository_andLoadIntoView() {
        mTaskHeadsPresenter.loadTaskHeads(false);

        verify(mRepository).getTaskHeadDetails(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadDetailsLoaded(TASKHEAD_DETAILS);

        ArgumentCaptor<List> showTaskHeadsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTaskHeadView).showTaskHeads(showTaskHeadsArgumentCaptor.capture());
        assertTrue(showTaskHeadsArgumentCaptor.getValue().size() == TASKHEAD_DETAILS.size());
    }

    @Test
    public void deleteTaskHeads_andDeleteTasks() {
        List<String> taskHeadIds = new ArrayList<>();
        taskHeadIds.add(TASKHEADS.get(0).getId());
        taskHeadIds.add(TASKHEADS.get(1).getId());
        taskHeadIds.add(TASKHEADS.get(2).getId());

        mTaskHeadsPresenter.deleteTaskHeads(TASKHEADS);

        verify(mRepository).deleteTaskHeads(eq(taskHeadIds), mDeleteTaskHeadsCallbackCaptor.capture());
    }

    @Test
    public void getTaskHeadsCountFromRepo_andShowsAddTaskHeadUi_whenCall_addNewTask() {
        mTaskHeadsPresenter.addNewTaskHead();

        verify(mRepository).getTaskHeadsCount();
        verify(mTaskHeadView).showTaskHeadDetail(anyInt());
    }

    @Test
    public void updateTaskHeadsOrder_toRepo() {
        mTaskHeadsPresenter.updateOrders(TASKHEADS);

        verify(mRepository).updateTaskHeadOrders((List<TaskHead>) anyCollectionOf(TaskHead.class), mUpdateTaskHeadCallbackCaptor.capture());
    }
}