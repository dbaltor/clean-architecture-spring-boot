package library.adapter.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class BooksRequest {
    public @NonNull Long bookIds[];
}
