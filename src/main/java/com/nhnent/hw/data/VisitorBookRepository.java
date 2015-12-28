package com.nhnent.hw.data;

import org.springframework.data.repository.CrudRepository;

public interface VisitorBookRepository extends CrudRepository<VisitorBook, Long> {
    VisitorBook findByIdAndPasswd(long id, String passwd);
}
