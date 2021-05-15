package library.adapter.controller.port;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public interface HomeController {
    
    @GetMapping("/")
    public String index(Model model);
    
    @PostMapping("/cleanup")
    @ResponseBody
    public String cleanUp();
}