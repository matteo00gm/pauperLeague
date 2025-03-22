package com.mtgleague.controller;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class ZeroSSLController {
    @GetMapping("/.well-known/pki-validation/DA9BB67B67D4A1C592713663F2ACCAD3.txt")
    public ResponseEntity<String> getVerificationString() {
        String fileContent = "109C545DEE9C134544F002A62AED5AD1EBBFDE09F3158F80A30BA57670916CA1\n" +
                "comodoca.com\n" +
                "af0dc5570ac621a";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
