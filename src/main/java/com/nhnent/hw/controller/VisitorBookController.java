package com.nhnent.hw.controller;

import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nhnent.hw.data.VisitorBook;
import com.nhnent.hw.data.VisitorBookRepository;

@RequestMapping(path="/api/visitorbooks")
@RestController
public class VisitorBookController {
    
    @Autowired
    private VisitorBookRepository repository;

    @RequestMapping(method=RequestMethod.GET)
    public Iterable<VisitorBook> getAllVisitorBooks() {
        return repository.findAllByOrderByModifiedAtAsc();
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public VisitorBook saveVisitorBook(@RequestBody VisitorBook visitorBook) {
        if (!visitorBook.hasValidEmail()) {
            throw new BadRequestException("Not Valid Email.");
        }
        Date now = new Date();
        visitorBook.setCreatedAt(now);
        visitorBook.setModifiedAt(now);
        repository.save(visitorBook);
        return visitorBook;
    }
    
    @RequestMapping(path="/{id}", method=RequestMethod.PUT)
    public VisitorBook updateVisitorBook(
            @PathVariable long id, 
            @RequestBody VisitorBook visitorBook) {
        VisitorBook found = 
                repository.findByIdAndPasswd(id, visitorBook.getPasswd());
        if (found == null) {
            throw new VisitorBookNotFoundException(id);
        }
        visitorBook.setModifiedAt(new Date());
        repository.save(visitorBook);
        return visitorBook;
    }
    
    @RequestMapping(path="/{id}", method=RequestMethod.DELETE)
    @ResponseStatus(value=HttpStatus.OK)
    public void deleteVisitorBook(@PathVariable long id) {
        if (!repository.exists(id))
            throw new VisitorBookNotFoundException(id);
        repository.delete(id);
    }
    
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    private class VisitorBookNotFoundException extends RuntimeException {
        private static final long serialVersionUID = -7626322547638600089L;

        private VisitorBookNotFoundException(long id) {
            super("No such visitor book: " + id);
        }
    }
    
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    private class BadRequestException extends RuntimeException {
        private static final long serialVersionUID = -5032106088723522027L;

        private BadRequestException(String msg) {
            super(msg);
        }
    }
}
