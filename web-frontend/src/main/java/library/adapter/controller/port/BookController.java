package library.adapter.controller.port;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

public interface BookController {

    @GetMapping("/books/list")
    //public String listbooks(@RequestParam(required=false, defaultValue="0") int count, Model model) {
    public String listbooks(
        @RequestParam(name = "page") Optional<Integer> pageNum, 
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Integer> readerId,
        Model model);

    @PostMapping("/books/load")
    @ResponseBody
    public String reqLoadDatabase(@RequestParam Optional<Integer> count);
}