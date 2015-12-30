package com.nhnent.hw.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VisitorBookRepository extends CrudRepository<VisitorBook, Long> {
    VisitorBook findByIdAndPasswd(long id, String passwd);
    List<VisitorBook> findAllByOrderByModifiedAtAsc();
}
