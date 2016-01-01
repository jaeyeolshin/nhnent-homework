package com.nhnent.hw.controller;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.nhnent.hw.NhnentHomeworkApplication;
import com.nhnent.hw.data.VisitorBook;
import com.nhnent.hw.data.VisitorBookRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NhnentHomeworkApplication.class)
@TestPropertySource(locations="classpath:test.properties")
@WebIntegrationTest
public class VisitorBookControllerTests {
    
    private final String BASE_URL = "http://localhost:8080/api/visitorbooks";
    private final RestTemplate rest = new TestRestTemplate();
    
    @Autowired
    private VisitorBookRepository repository;
    
    private class FakeVisitorBook implements Cloneable {
        public long id;
        public String email;
        @SuppressWarnings("unused")
        public String passwd;
        public String content;
        @SuppressWarnings("unused")
        public Date createdAt;
        @SuppressWarnings("unused")
        public Date modifiedAt;
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
    
    private FakeVisitorBook storedBook;
    
    @Before
    public void prepare() {
        this.storedBook = new FakeVisitorBook();
        this.storedBook.email = "test@test.com";
        this.storedBook.passwd = "asdf1234";
        this.storedBook.content = "Test Data Stored!";
        
        ResponseEntity<VisitorBook> response =
                rest.postForEntity(BASE_URL, this.storedBook, VisitorBook.class);
        
        this.storedBook.id = response.getBody().getId();
        this.storedBook.createdAt = response.getBody().getCreatedAt();
        this.storedBook.modifiedAt = response.getBody().getModifiedAt();
    }
    
    @After
    public void finish() {
        repository.deleteAll();
    }

    @Test
    public void testGet() {
        ResponseEntity<VisitorBook[]> response = 
                rest.getForEntity(BASE_URL, VisitorBook[].class);
        
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody().length == 1);
        
        VisitorBook book = response.getBody()[0];
        assertTrue(book.getEmail().equals(this.storedBook.email));
        assertTrue(book.getContent().equals(this.storedBook.content));
    }
    
    @Test
    public void testPost() {
        FakeVisitorBook book = new FakeVisitorBook();
        book.email = "abc@email.net";
        book.passwd = "1234";
        book.content = "Hello, World!";
        
        ResponseEntity<VisitorBook> response =
                rest.postForEntity(BASE_URL, book, VisitorBook.class);
        
        VisitorBook responseBody = response.getBody();
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(responseBody.getId() != 0);
        assertTrue(responseBody.getCreatedAt().equals(responseBody.getModifiedAt()));
        
        repository.delete(responseBody.getId());
    }
    
    @Test
    public void testPostBadRequestByInvalidEmail() {
        FakeVisitorBook book = new FakeVisitorBook();
        book.email = "abc@email";
        book.passwd = "1234";
        book.content = "Hello, World!";
        
        ResponseEntity<VisitorBook> response =
                rest.postForEntity(BASE_URL, book, VisitorBook.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void testPut() {
        String newContent = "Good Afternoon~";
        this.storedBook.content = newContent;
        HttpEntity<FakeVisitorBook> entity = 
                new HttpEntity<FakeVisitorBook>(this.storedBook);
        ResponseEntity<VisitorBook> response =
                rest.exchange(
                        BASE_URL + "/" + this.storedBook.id, 
                        HttpMethod.PUT, 
                        entity, VisitorBook.class);
        
        VisitorBook responseBody = response.getBody();
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(responseBody.getContent().equals(newContent));
        assertFalse(responseBody.getCreatedAt().equals(responseBody.getModifiedAt()));
    }
    
    @Test
    public void testPutNotFoundByWrongId() {
        HttpEntity<FakeVisitorBook> entity = new HttpEntity<FakeVisitorBook>(this.storedBook);
        ResponseEntity<VisitorBook> response =
                rest.exchange(
                        BASE_URL + "/" + Long.MAX_VALUE, 
                        HttpMethod.PUT, 
                        entity, VisitorBook.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void testPutNotFoundByWrongPasswd()
        throws CloneNotSupportedException {
        FakeVisitorBook fakeBook = (FakeVisitorBook)this.storedBook.clone();
        fakeBook.passwd = "4321";
        
        HttpEntity<FakeVisitorBook> entity = new HttpEntity<FakeVisitorBook>(fakeBook);
        ResponseEntity<VisitorBook> response =
                rest.exchange(
                        BASE_URL + "/" + fakeBook.id, 
                        HttpMethod.PUT, 
                        entity, VisitorBook.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

}
