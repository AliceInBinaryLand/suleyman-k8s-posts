package com.epam.suleymank8sposts.controller;


import com.epam.suleymank8sposts.model.Post;
import com.epam.suleymank8sposts.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

@RestController
public class PostController {

    @Autowired
    PostRepository postRepository;

    private RestTemplate restTemplate;
    @Value("${userserviceurl}")
    private String USER_SERVICE_URL;

    @PostMapping(path = "/posts")
    public ResponseEntity<Post> save(@RequestBody Post post){
        if(post.getText().isEmpty()){
            return new ResponseEntity<>(null , HttpStatus.BAD_REQUEST);
        }else{
            post.setPostedAt(new Date());
            Post savedPost = postRepository.save(post);
            restTemplate = new RestTemplate();
            ResponseEntity response = restTemplate.postForEntity(USER_SERVICE_URL+"increasePostCount/"+post.getAuthorId() ,null ,String.class );
            if(response.getStatusCode().isError()){
                postRepository.delete(savedPost); // rollback
                return  new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(savedPost , HttpStatus.OK);
        }
    }

    @GetMapping(path = "/posts/{id}")
    public ResponseEntity<Post> get(@PathVariable("id") long id){
        Optional<Post> postRecord = postRepository.findById(id);
        if(postRecord.isPresent()){
          return new ResponseEntity<>(postRecord.get() , HttpStatus.OK);
        }else {
            return  new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping(path = "/posts/{id}")
    public ResponseEntity<Post> delete(@PathVariable("id") long id){
        Optional<Post> postRecord = postRepository.findById(id);
        if(postRecord.isPresent()){
            postRepository.deleteById(id);
            ResponseEntity response = restTemplate.postForEntity(USER_SERVICE_URL+"decreasePostCount/"+postRecord.get().getAuthorId(),null ,String.class );
            return  new ResponseEntity<>(null , HttpStatus.OK);
        }else{
            return new ResponseEntity( null , HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/posts/{id}")
    public ResponseEntity<Post> update(@PathVariable("id") long id, @RequestBody Post post) {
        if(post.getText().isEmpty()){
            return new ResponseEntity(null , HttpStatus.BAD_REQUEST);
        }else{
            Optional<Post> postRecord = postRepository.findById(id);
            if(postRecord.isPresent()){
                post.setId(id);
                post.setPostedAt(new Date());
                postRepository.save(post);
                return  new ResponseEntity<>(post , HttpStatus.OK);
            }else{
                return new ResponseEntity( null , HttpStatus.NOT_FOUND);
            }
        }

    }

    @GetMapping(path = "/healthCheck")
    public ResponseEntity healthCheck(){
        return new ResponseEntity(null , HttpStatus.OK);
    }
}
