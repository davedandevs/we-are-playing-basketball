package by.vibe.weareplayingbasketball.controller;


import by.vibe.weareplayingbasketball.model.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartController {

  @GetMapping("/start")
  public ResponseEntity<?> start() {
    return ResponseEntity.ok(SuccessResponse.builder().success(true).build());
  }
}
