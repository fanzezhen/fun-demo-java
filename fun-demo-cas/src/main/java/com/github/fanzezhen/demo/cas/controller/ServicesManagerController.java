package com.github.fanzezhen.demo.cas.controller;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author anumbrella
 */
@RestController
@RequestMapping("/services")
public class ServicesManagerController {
    @Resource
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

}