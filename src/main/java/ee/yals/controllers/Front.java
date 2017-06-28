package ee.yals.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
@Controller
public class Front {

    /**
     * Index (/) page
     * @return The index (FTL)
     */
    @RequestMapping("/")
    public String index(){
        return "index";
    }

}
