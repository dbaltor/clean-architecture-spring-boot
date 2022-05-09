package library.adapter.controller.port;

import java.util.Optional;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import library.adapter.controller.dto.BooksRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

public interface ReaderController {

    @GetMapping("/readers/list")
    public String listReaders(
        @RequestParam(name = "page") Optional<Integer> pageNum,
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Integer> readerId,
        Model model);

    @PostMapping("/readers/load")
    @ResponseBody
    public String loadDatabase(@RequestParam Optional<Integer> count);

    @PostMapping("/readers/{id}/borrowBooks")
    @ResponseBody
    public String borrowBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest);

    @PostMapping("/readers/{id}/returnBooks")
    @ResponseBody
    public String returnBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest);
}