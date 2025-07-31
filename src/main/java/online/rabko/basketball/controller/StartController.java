package online.rabko.basketball.controller;


import online.rabko.basketball.model.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Start controller.
 */
@RestController
public class StartController {

    /**
     * Start.
     *
     * @return the success response entity
     */
    @GetMapping("/start")
    public ResponseEntity<?> start() {
        return ResponseEntity.ok(SuccessResponse.builder().success(true).build());
    }
}
