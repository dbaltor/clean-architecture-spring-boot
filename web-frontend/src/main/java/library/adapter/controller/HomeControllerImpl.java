package library.adapter.controller;

import library.LibraryApplication;
import library.adapter.controller.port.HomeController;
import library.usecase.port.BookService;
import library.usecase.port.ReaderService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class HomeControllerImpl implements HomeController{
    public static final String BOOKS_TEMPLATE = "Index";

    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;
    
    @Override
    public String index(Model model) {
        // Set background color of response page
        model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());
        return BOOKS_TEMPLATE;
    }
    
    @Override
    public String cleanUp() {
        readerService.cleanUpDatabase();
        bookService.cleanUpDatabase();
        return "The data have been removed";
    }
}