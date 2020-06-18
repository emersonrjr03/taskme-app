package com.herms.taskme.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.herms.taskme.model.TaskSomeone;
import com.herms.taskme.model.User;

@Repository
public interface TaskSomeoneRepository extends CrudRepository<TaskSomeone, Long>, PagingAndSortingRepository<TaskSomeone, Long> {

    public List<TaskSomeone> findAllByOrderByCreatedOnDesc();
    public Page<TaskSomeone> findAllByOrderByCreatedOnDesc(Pageable pageable);
    public Page<TaskSomeone> findByTitleContainingIgnoreCaseOrderByCreatedOnDesc(Pageable pageable, String term);

    public Page<TaskSomeone> findAllByUser_IdAndTitleContainingIgnoreCaseOrderByCreatedOnDesc(Pageable pageable, Long userId, String term);
    public Page<TaskSomeone> findAllByUserOrderByCreatedOnDesc(User user, Pageable pageable);
    
    @Query("select ta.taskSomeone from TaskApplication ta where ta.user.id = :userId AND ta.status = 'A' order by created_on")
    public List<TaskSomeone> findAllPreviousTasksFromUserIdOrderByCreatedOnDesc(@Param("userId") Long userId);
    
}
