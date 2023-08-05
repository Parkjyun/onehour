package onehour.demo.controller;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//(responsebody + controller)
@RequestMapping("/api")//공통적인 url은 여기에 작성
public class HelloController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");//httpstatus, header, body를 포함하는 응답 데이터를 포함하는 클래스
    }

}
