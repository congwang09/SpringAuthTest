package com.plumstep.controller;

import io.swagger.annotations.ApiOperation;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class ClientController {
    @ApiOperation(value = "Return text message to show off successful call")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    ResponseEntity<?> getMessage() {
    		return ResponseEntity.ok("Client successfully called!");
    }
}
