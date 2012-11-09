package org.nkey.test.ntlm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author m.nikolaev Date: 09.11.12 Time: 20:09
 */
@Controller
public class SecuredController {
    @RequestMapping({ "/", "/index" })
    public String index() {
        return "welcom";
    }
}
