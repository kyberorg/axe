package io.kyberorg.yalsee.controllers;

import io.kyberorg.yalsee.Endpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles tech resources. Currently only Fail endpoint for tests.
 *
 * @since 2.0
 */
@Controller
public class TechPartsController {

    /**
     * This endpoint meant to be used only in application tests for simulating application fails.
     *
     * @return always throws RuntimeException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = {Endpoint.ForTests.FAIL_ENDPOINT, Endpoint.ForTests.FAIL_API_ENDPOINT})
    public String iWillAlwaysFail() {
        throw new RuntimeException("I will always fail");
    }
}
