package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.TaskHead;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadRepositoryTest {

    private static final List<TaskHead> TASKHEADS =
            Lists.newArrayList(new TaskHead("title1"), new TaskHead("title2"), new TaskHead("title3"));

    private TaskHeadRepository mTaskHeadsRepository;

    @Mock
    private TaskHeadDataSource.LoadTaskHeadsCallback mLoadTaskHeadsCallback;
    @Mock
    private TaskHeadDataSource mTaskHeadsRemoteDataSource;
    @Mock
    private TaskHeadDataSource mTaskHeadsLocalDataSource;
    @Captor
    private ArgumentCaptor<TaskHeadDataSource.LoadTaskHeadsCallback> mTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsRepository = TaskHeadRepository.getInstance(
                mTaskHeadsRemoteDataSource, mTaskHeadsLocalDataSource);
    }

    /*
    * Get taskHeads
    * */
    @Test
    public void getTaskHeadsWithDirtyCache_taskHeadsAreRetrievedFromRemote() {
        // When calling getTaskHeads in the repo with dirty cache
        mTaskHeadsRepository.refreshTaskHeads();
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);
        // And the remote data source has data available
        setTaskHeadsAvailable(mTaskHeadsRemoteDataSource, TASKHEADS);

        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

    @Test
    public void getTaskHeads_fromRemote() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        verify(mTaskHeadsRemoteDataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }


    private void setTaskHeadsAvailable(TaskHeadDataSource dataSource, List<TaskHead> taskHeads) {
        verify(dataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }

    @Test
    public void deleteTaskHead_deleteTaskToServiceApiRemovedFromCache() {
        TaskHead newTaskHead = new TaskHead();
        mTaskHeadsRepository.saveTaskHead(newTaskHead);
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(newTaskHead.getId()), is(true));

        // When deleted
        mTaskHeadsRepository.deleteTaskHead(newTaskHead.getId());

        // Verify it's removed from repository
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(newTaskHead.getId()), is(false));
    }
}